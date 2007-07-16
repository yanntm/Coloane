package fr.lip6.move.coloane.ui;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.EventObject;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.draw2d.FigureCanvas;
import org.eclipse.draw2d.LightweightSystem;
import org.eclipse.draw2d.MarginBorder;
import org.eclipse.draw2d.Viewport;
import org.eclipse.draw2d.parts.ScrollableThumbnail;
import org.eclipse.draw2d.parts.Thumbnail;
import org.eclipse.gef.ContextMenuProvider;
import org.eclipse.gef.DefaultEditDomain;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.LayerConstants;
import org.eclipse.gef.RootEditPart;
import org.eclipse.gef.dnd.TemplateTransferDragSourceListener;
import org.eclipse.gef.editparts.ScalableFreeformRootEditPart;
import org.eclipse.gef.editparts.ZoomManager;
import org.eclipse.gef.palette.PaletteRoot;
import org.eclipse.gef.ui.actions.ActionRegistry;
import org.eclipse.gef.ui.palette.PaletteViewer;
import org.eclipse.gef.ui.palette.PaletteViewerProvider;
import org.eclipse.gef.ui.palette.FlyoutPaletteComposite.FlyoutPreferences;
import org.eclipse.gef.ui.parts.ContentOutlinePage;
import org.eclipse.gef.ui.parts.GraphicalEditorWithFlyoutPalette;
import org.eclipse.gef.ui.parts.GraphicalViewerKeyHandler;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.actions.WorkspaceModifyOperation;
import org.eclipse.ui.dialogs.SaveAsDialog;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.part.IPageSite;
import org.eclipse.ui.part.PageBook;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;
import org.eclipse.ui.views.properties.IPropertySheetEntry;
import org.eclipse.ui.views.properties.IPropertySheetPage;
import org.eclipse.ui.views.properties.PropertySheetPage;
import org.eclipse.ui.views.properties.PropertySheetSorter;

import fr.lip6.move.coloane.main.Coloane;
import fr.lip6.move.coloane.motor.formalism.Formalism;
import fr.lip6.move.coloane.motor.formalism.FormalismManager;
import fr.lip6.move.coloane.ui.model.IModelImpl;
import fr.lip6.move.coloane.ui.model.ModelImplAdapter;
import org.apache.xerces.impl.io.MalformedByteSequenceException;

import java.io.*;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.parsers.SAXParser;

import fr.lip6.move.coloane.interfaces.model.IModel;

public class Editor extends GraphicalEditorWithFlyoutPalette {

	class OutlinePage extends ContentOutlinePage implements IAdaptable {

		private PageBook pageBook;

		private Canvas overview;

		private Thumbnail thumbnail;

		private DisposeListener disposeListener;

		public OutlinePage(EditPartViewer viewer) {
			super(viewer);
		}

		public void init(IPageSite pageSite) {
			super.init(pageSite);
			ActionRegistry registry = getActionRegistry();
			IActionBars bars = pageSite.getActionBars();

			String id = ActionFactory.UNDO.getId();
			bars.setGlobalActionHandler(id, registry.getAction(id));

			id = ActionFactory.REDO.getId();
			bars.setGlobalActionHandler(id, registry.getAction(id));

			id = ActionFactory.DELETE.getId();
			bars.setGlobalActionHandler(id, registry.getAction(id));

			bars.updateActionBars();
		}

		public void createControl(Composite parent) {
			pageBook = new PageBook(parent, SWT.NONE);
			overview = new Canvas(pageBook, SWT.NONE);
			pageBook.showPage(overview);
			showPage();
		}

		public void dispose() {
			unhookOutlineViewer();
			if (thumbnail != null) {
				thumbnail.deactivate();
				thumbnail = null;
			}
			super.dispose();
			Editor.this.outlinePage = null;
			outlinePage = null;
		}

		public Object getAdapter(Class type) {
			if (type == ZoomManager.class)
				return getGraphicalViewer().getProperty(ZoomManager.class.toString());
			return null;
		}

		public Control getControl() {
			return pageBook;
		}

		protected void initializeOverview() {
			LightweightSystem lws = new LightweightSystem(overview);
			RootEditPart rep = getGraphicalViewer().getRootEditPart();
			if (rep instanceof ScalableFreeformRootEditPart) {
				ScalableFreeformRootEditPart root = (ScalableFreeformRootEditPart) rep;
				thumbnail = new ScrollableThumbnail((Viewport) root.getFigure());
				thumbnail.setBorder(new MarginBorder(3));
				thumbnail.setSource(root.getLayer(LayerConstants.PRINTABLE_LAYERS));
				lws.setContents(thumbnail);
				disposeListener = new DisposeListener() {
					public void widgetDisposed(DisposeEvent e) {
						if (thumbnail != null) {
							thumbnail.deactivate();
							thumbnail = null;
						}
					}
				};
				getEditor().addDisposeListener(disposeListener);
			}
		}

		public void setContents(Object contents) {
			getViewer().setContents(contents);
		}

		protected void showPage() {
			if (thumbnail == null)
				initializeOverview();
			pageBook.showPage(overview);
			thumbnail.setVisible(true);
		}

		protected void unhookOutlineViewer() {
			getSelectionSynchronizer().removeViewer(getViewer());
			if (disposeListener != null && getEditor() != null
					&& !getEditor().isDisposed())
				getEditor().removeDisposeListener(disposeListener);
		}
	}

	/** La page d'apercu */
	private OutlinePage outlinePage;

	/** Le modele */
	private IModelImpl model;

	/** La palette */
	private PaletteRoot paletteRoot;

	/** Formalisme */
	private Formalism formalism;

	/** Constructeur de l'editeur */
	public Editor() {
	}

	/**
	 * Configuration de l'editeur
	 */
	protected void configureGraphicalViewer() {
		super.configureGraphicalViewer();

		GraphicalViewer viewer = getGraphicalViewer();
		viewer.setEditPartFactory(new PartFactory());
		viewer.setRootEditPart(new ScalableFreeformRootEditPart());
		viewer.setKeyHandler(new GraphicalViewerKeyHandler(viewer));

		ContextMenuProvider cmProvider = new ColoaneContextMenuProvider(viewer, getActionRegistry());
		viewer.setContextMenu(cmProvider);
		getSite().registerContextMenu(cmProvider, viewer);
	}

	/**
	 * Set up the editor's inital content (after creation).
	 * 
	 * @see org.eclipse.gef.ui.parts.GraphicalEditorWithFlyoutPalette#initializeGraphicalViewer()
	 * 
	 */
	protected void initializeGraphicalViewer() {
		super.initializeGraphicalViewer();
		System.out.println("Initialisation de l'espace de travail"); //$NON-NLS-1$
		GraphicalViewer viewer = getGraphicalViewer();
		viewer.setContents(getModel()); // set the contents of this editor

		// Fin de la construction
		model.setEndBuilding();
		// listen for dropped parts
		// viewer.addDropTargetListener(createTransferDropTargetListener());
	}

	/**
	 * Retourne le model.
	 * 
	 * @return IModelImpl
	 */
	public IModelImpl getModel() {
		return model;
	}

	/**
	 * Creation de la palette
	 * 
	 * @return PaletteRoot
	 */
	protected PaletteRoot getPaletteRoot() {
		if (paletteRoot != null) {
			paletteRoot = null;
		}

		paletteRoot = PaletteFactory.createPalette(this.formalism);

		return paletteRoot;
	}

	/**
	 * Retourne les preferences de la palette
	 * 
	 * @return FlyoutPreferences
	 */
	protected FlyoutPreferences getPalettePreferences() {
		return PaletteFactory.createPalettePreferences();
	}

	/**
	 * Determine le contenu de l'editeur. Ce contenu est lu depuis un fichier.
	 * Ce fichier a bien sur ete cree par l'assistant
	 * 
	 * @param input
	 *            IEditorInput : Informations (nom, fichier, location...)
	 *            concernant le contenu
	 */

	protected void setInput(IEditorInput input) {
		super.setInput(input);

		try {
			IFile file = ((IFileEditorInput) input).getFile();
			System.out.println("Fichier de contenu : " + file.getName()); //$NON-NLS-1$

			FormalismManager formManager = Coloane.getDefault().getMotor().getFormalismManager();

			// Creation d'un instance du handler
			XmlEditor handler = new XmlEditor();

			SAXParserFactory factory = SAXParserFactory.newInstance();

			factory.setValidating(true);
			
			// La factory produira un parseur validant
			SAXParser saxParser = factory.newSAXParser();
			saxParser.parse(file.getLocation().toString(), handler);

			// Formalism obtenu grace l'extension du fichier
			this.formalism = formManager.getFormalismByExtension(file.getFileExtension());

			// Creation du model a partir du model generique
			model = new ModelImplAdapter(handler.getModel(), formalism);

			// Si le model est nul
			// On cree un nouveau modele
			// TODO: Verifier si ce test est utile ou non!
			if (this.model == null) {
				this.formalism = formManager.getFormalismByExtension(file.getFileExtension());
				this.model = new ModelImplAdapter(formalism);
			} else {
				this.formalism = model.getFormalism();
			}

			model.setBeginBuilding(); // Le nom de la tabulation
			setPartName(file.getName());
			setEditDomain(new DefaultEditDomain(this)); // L'apercu
			if (outlinePage != null) {
				outlinePage.setContents(getModel());
			}

		} catch (MalformedByteSequenceException e) {
			// Le fichier à ouvrir est mal formé, on cree alors un nouveau
			// modele vierge
			IFile file = ((IFileEditorInput) input).getFile();

			FormalismManager formManager = Coloane.getDefault().getMotor().getFormalismManager();

			this.formalism = formManager.getFormalismByExtension(file.getFileExtension());

			this.model = new ModelImplAdapter(formalism);
			saveNew();
			// Debut de la construction du model
			model.setBeginBuilding();

			setPartName(file.getName());// Le nom de la tabulation
			setEditDomain(new DefaultEditDomain(this)); // L'apercu

			if (outlinePage != null) {
				outlinePage.setContents(getModel());
			}
		} catch (Exception e) {
			IFile file = ((IFileEditorInput) input).getFile();
			System.out.println(e.toString());
			Coloane.showErrorMsg(Coloane.traduction.getString("ui.Editor.3") +file.getName()+" - "+ e.getMessage()); //$NON-NLS-1$ //$NON-NLS-2$
		}

	}

	/**
	 * Sauvegarde d'un fichier
	 */

	public void doSave(IProgressMonitor monitor) {

		IFile file = ((IFileEditorInput) getEditorInput()).getFile();

		// Creation du l'editeur xml
		XmlEditor xml = new XmlEditor();

		// String permettant de stocker le modele au format xml
		String xmlString = ""; //$NON-NLS-1$

		try {
			// Recuperation du modele generique
			IModel m = getModel().getGenericModel();

			// Traduction du modele au format xml
			xmlString = xml.modelXML(m);

			// Creation de l'input stream a partir d'une chaine de caractere
			InputStream inputS = new ByteArrayInputStream(xmlString.getBytes());

			// Ecriture du fichier de sauvegarder à partir du l'input stream
			file.setContents(inputS, true, false, monitor);

			getCommandStack().markSaveLocation();
		} catch (CoreException ce) {
			ce.printStackTrace();

		}
	}
	
	private void saveNew() {

		IFile file = ((IFileEditorInput) getEditorInput()).getFile();

		// Creation du l'editeur xml
		XmlEditor xml = new XmlEditor();

		// String permettant de stocker le modele au format xml
		String xmlString = ""; //$NON-NLS-1$

		try {
			// Recuperation du modele generique
			IModel m = getModel().getGenericModel();

			// Traduction du modele au format xml
			xmlString = xml.modelXML(m);

			// Creation de l'input stream a partir d'une chaine de caractere
			InputStream inputS = new ByteArrayInputStream(xmlString.getBytes());

			// Ecriture du fichier de sauvegarder à partir du l'input stream
			file.setContents(inputS, true, false, null);

		} catch (CoreException ce) {
			ce.printStackTrace();

		}
	}

	public void doSaveAs() {
		// Show a SaveAs dialog
		Shell shell = getSite().getWorkbenchWindow().getShell();
		SaveAsDialog dialog = new SaveAsDialog(shell);
		dialog.setOriginalFile(((IFileEditorInput) getEditorInput()).getFile());
		dialog.open();

		IPath path = dialog.getResult();
		if (path != null) {
			// try to save the editor's contents under a different file name
			final IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(path);
			try {
				new ProgressMonitorDialog(shell).run(false, // don't fork
				false, // not cancelable
				new WorkspaceModifyOperation() { // run this
					// operation
					public void execute(final IProgressMonitor monitor) {
						try {

							XmlEditor xml = new XmlEditor();
							String xmlString = ""; //$NON-NLS-1$

							// Recuperation du modele generique
							IModel m = getModel().getGenericModel();

							// traduction du modele au format xml
							xmlString = xml.modelXML(m);

							InputStream inputS = new ByteArrayInputStream(xmlString.getBytes());

							// Si le fichier existe alors on l'ecrase sinon on
							// en cree un nouveau
							if (file.exists()) {
								file.setContents(inputS, true, false, monitor);

							} else {

								// Creation d'un nouveau fichier contenant le
								// modele au format xml
								file.create(inputS, // contents
								true, // keep saving, even if IFile is out of
								// sync with
								// the Workspace
								monitor); // progress monitor
							}
						} catch (CoreException ce) {
							ce.printStackTrace();

						}

					}
				});
				// set input to the new file
				setInput(new FileEditorInput(file));
				getCommandStack().markSaveLocation();
			} catch (InterruptedException ie) {
				// should not happen, since the monitor dialog is not cancelable
				ie.printStackTrace();
			} catch (InvocationTargetException ite) {
				ite.printStackTrace();
			}
		}
	}

	public boolean isSaveAsAllowed() {
		return true;
	}

	public void commandStackChanged(EventObject event) {
		firePropertyChange(IEditorPart.PROP_DIRTY);
		super.commandStackChanged(event);
	}

	public void createOutputStream(OutputStream os) throws IOException {
		ObjectOutputStream oos = new ObjectOutputStream(os);
		oos.writeObject(getModel());
		oos.close();
	}

	public void createOutputStream(OutputStream os, String s)
			throws IOException {
		ObjectOutputStream oos = new ObjectOutputStream(os);
		oos.writeObject(s);
		oos.close();
	}

	protected PaletteViewerProvider createPaletteViewerProvider() {
		return new PaletteViewerProvider(getEditDomain()) {
			protected void configurePaletteViewer(PaletteViewer viewer) {
				super.configurePaletteViewer(viewer);
				viewer.addDragSourceListener(new TemplateTransferDragSourceListener(viewer));
			}
		};
	}

	protected FigureCanvas getEditor() {
		return (FigureCanvas) getGraphicalViewer().getControl();
	}

	public Object getAdapter(Class type) {
		if (type == IContentOutlinePage.class) {
			outlinePage = new OutlinePage(getGraphicalViewer());
			return outlinePage;

			// On redefinit la fenetre de propriete
		} else if (type == IPropertySheetPage.class) {
			PropertySheetPage page = new PropertySheetPage() {
				{
					setSorter(new PropertySheetSorter() {
						public void sort(IPropertySheetEntry[] entries) {
							// Aucun tri !
						}
					});
				}
			};
			return page;
		}
		return super.getAdapter(type);
	}
}
