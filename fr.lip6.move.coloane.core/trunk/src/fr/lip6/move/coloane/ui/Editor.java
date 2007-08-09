package fr.lip6.move.coloane.ui;

import fr.lip6.move.coloane.interfaces.model.IModel;

import fr.lip6.move.coloane.main.Coloane;
import fr.lip6.move.coloane.motor.formalism.Formalism;
import fr.lip6.move.coloane.motor.formalism.FormalismManager;
import fr.lip6.move.coloane.ui.model.IModelImpl;
import fr.lip6.move.coloane.ui.model.ModelImplAdapter;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.EventObject;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
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
import org.eclipse.gef.ui.actions.ZoomInAction;
import org.eclipse.gef.ui.actions.ZoomOutAction;
import org.eclipse.gef.ui.palette.FlyoutPaletteComposite.FlyoutPreferences;
import org.eclipse.gef.ui.palette.PaletteViewer;
import org.eclipse.gef.ui.palette.PaletteViewerProvider;
import org.eclipse.gef.ui.parts.ContentOutlinePage;
import org.eclipse.gef.ui.parts.GraphicalEditorWithFlyoutPalette;
import org.eclipse.gef.ui.parts.GraphicalViewerKeyHandler;
import org.eclipse.gef.ui.properties.UndoablePropertySheetEntry;
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
import org.eclipse.ui.part.IPageSite;
import org.eclipse.ui.part.PageBook;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;
import org.eclipse.ui.views.properties.IPropertySheetEntry;
import org.eclipse.ui.views.properties.IPropertySheetPage;
import org.eclipse.ui.views.properties.PropertySheetPage;
import org.eclipse.ui.views.properties.PropertySheetSorter;

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
			if (type == ZoomManager.class) {
				return getGraphicalViewer().getProperty(ZoomManager.class.toString());
			}
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
			if (thumbnail == null) {
				initializeOverview();
			}
			pageBook.showPage(overview);
			thumbnail.setVisible(true);
		}

		protected void unhookOutlineViewer() {
			getSelectionSynchronizer().removeViewer(getViewer());
			if (disposeListener != null && getEditor() != null && !getEditor().isDisposed()) {
				getEditor().removeDisposeListener(disposeListener);
			}
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
	public Editor() { }

	/**
	 * Configuration de l'editeur
	 *
	 */
	@Override
	protected final void configureGraphicalViewer() {
		ArrayList<String> zoomContributions;

		super.configureGraphicalViewer();

		GraphicalViewer viewer = getGraphicalViewer();
		ScalableFreeformRootEditPart rootEditPart = new ScalableFreeformRootEditPart();

		viewer.setEditPartFactory(new PartFactory());
		viewer.setRootEditPart(rootEditPart);
		viewer.setKeyHandler(new GraphicalViewerKeyHandler(viewer));

		ContextMenuProvider cmProvider = new ColoaneContextMenuProvider(viewer, getActionRegistry());

		getSite().setSelectionProvider(viewer);
		getSite().registerContextMenu(cmProvider, viewer);

		viewer.setContextMenu(cmProvider);

		// Zoom
		ZoomManager manager = rootEditPart.getZoomManager();
		getActionRegistry().registerAction(new ZoomInAction(manager));
		getActionRegistry().registerAction(new ZoomOutAction(manager));

		// Liste des zooms possibles 1 = 100%
		double[] zoomLevels = new double[] {0.25, 0.5, 0.75, 1.0, 1.5, 2.0, 2.5, 3.0, 4.0, 5.0, 10.0, 20.0};
		manager.setZoomLevels(zoomLevels);

		// On ajoute certains zooms pr�d�finis
		zoomContributions = new ArrayList<String>();
		zoomContributions.add(ZoomManager.FIT_ALL);
		zoomContributions.add(ZoomManager.FIT_HEIGHT);
		zoomContributions.add(ZoomManager.FIT_WIDTH);
		manager.setZoomLevelContributions(zoomContributions);

	}

	/**
	 * Set up the editor's inital content (after creation).
	 */
	protected final void initializeGraphicalViewer() {
		super.initializeGraphicalViewer();
		GraphicalViewer viewer = getGraphicalViewer();
		viewer.setContents(getModel()); // set the contents of this editor

		// Fin de la construction
		model.setEndBuilding();
	}

	/**
	 * Retourne le modele attache a l'editeur
	 * @return IModelImpl Le modele augemente
	 */
	public final IModelImpl getModel() {
		return model;
	}

	/**
	 * Retourne le path du modele en cours d'edition
	 * @return Le chemin du modele en cours dans l'editeur
	 */
	public final IPath getCurrentPath() {
		IFile file = ((IFileEditorInput) getEditorInput()).getFile();
		String projectName = file.getProject().getName();
		IPath path = new Path(projectName);
		return path.append(file.getProjectRelativePath());
	}

	/**
	 * Creation de la palette d'outils
	 * @return PaletteRoot
	 */
	protected final PaletteRoot getPaletteRoot() {
		paletteRoot = PaletteFactory.createPalette(this.formalism);
		return paletteRoot;
	}

	/**
	 * Retourne les preferences de la palette
	 * @return FlyoutPreferences
	 */
	protected final FlyoutPreferences getPalettePreferences() {
		return PaletteFactory.createPalettePreferences();
	}

	/**
	 * Determine le contenu de l'editeur.
	 * Ce contenu est lu depuis un fichier.
	 * Ce fichier a bien sur ete cree par l'assistant
	 * @param input Toutes les informations concernant le modele
	 */

	protected final void setInput(IEditorInput input) {
		super.setInput(input);

		IFile file = ((IFileEditorInput) input).getFile();
		setPartName(file.getName());

		// Le gestionnaire de formalismes pour reconnaitre le formalisme en fonction de l'extension
		FormalismManager formManager = Coloane.getDefault().getMotor().getFormalismManager();
		this.formalism = formManager.getFormalismByExtension(file.getFileExtension());

		// Mise en place de l'editeur
		// On est oblige d'attendre le formalisme pour creer le domaine d'edition
		// En effet, le formalisme determine la palette qui sera affichee
		setEditDomain(new DefaultEditDomain(this));

		// Creation d'un instance du handler
		XmlEditor handler = new XmlEditor();


		try {
			SAXParserFactory factory = SAXParserFactory.newInstance();
			factory.setValidating(true); // Demande de validation lors du parse du fichier
			SAXParser saxParser = factory.newSAXParser();
			saxParser.parse(file.getLocation().toString(), handler);
		} catch (Exception e) {
			System.out.println(e.toString());
			Coloane.showErrorMsg(Coloane.getTranslate().getString("ui.Editor.3") + file.getName() + " - " + e.getMessage()); //$NON-NLS-1$ //$NON-NLS-2$
		}


		// Creation du model a partir du model generique
		model = new ModelImplAdapter(handler.getModel(), this.formalism);

		// Indication de debut de construction
		model.setBeginBuilding();

		// Si la fenetre d'apercu existe... On affiche la miniature
		if (outlinePage != null) {
			outlinePage.setContents(getModel());
		}
	}

	/**
	 * Sauvegarde d'un fichier
	 */
	public final void doSave(IProgressMonitor monitor) {

		IFile file = ((IFileEditorInput) getEditorInput()).getFile();

		// Recuperation du modele generique
		IModel genericModel = getModel().getGenericModel();

		// Traduction du modele au format xml
		String xmlString = XmlEditor.translateToXML(genericModel);

		// Creation de l'input stream a partir d'une chaine de caractere
		InputStream inputS = new ByteArrayInputStream(xmlString.getBytes());

		// Ecriture du fichier de sauvegarder a partir du l'input stream
		try {
			file.setContents(inputS, true, false, monitor);
		} catch (CoreException e) {
			System.err.println("Erreur lors de la sauvegarde du modele");
			e.printStackTrace();
		}

		getCommandStack().markSaveLocation();
	}

	/**
	 * Sauvegarde d'un fichier dans un autre emplacement
	 */
	public final void doSaveAs() {

		// Ouvre une boite de dialogue
		Shell shell = getSite().getWorkbenchWindow().getShell();
		SaveAsDialog dialog = new SaveAsDialog(shell);
		dialog.setOriginalFile(((IFileEditorInput) getEditorInput()).getFile());
		dialog.open();

		// Recupere le resultat de la boite de dialogue
		IPath path = dialog.getResult();


		if (path != null) {
			// try to save the editor's contents under a different file name
			final IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(path);
			try {
				new ProgressMonitorDialog(shell).run(false, false, new WorkspaceModifyOperation() {
					public void execute(final IProgressMonitor monitor) {
						// Recuperation du modele generique
						IModel genericModel = getModel().getGenericModel();
						String xmlString = XmlEditor.translateToXML(genericModel);
						InputStream inputS = new ByteArrayInputStream(xmlString.getBytes());
						try {
							// Si le fichier existe alors on l'ecrase sinon on en cree un nouveau
							if (file.exists()) {
								file.setContents(inputS, true, false, monitor);
							} else {
								file.create(inputS, true, monitor);
							}
						} catch (CoreException ce) {
							ce.printStackTrace();
						}
					}
				});

				// Suppression de ces lignes pour corriger le bug du saveAS
				// On installe le nouveau contenu dans l'editeur
				//setInput(new FileEditorInput(file));
				//getCommandStack().markSaveLocation();
				firePropertyChange(PROP_TITLE);

			} catch (InterruptedException ie) {
				// Should not happen, since the monitor dialog is not cancelable
				ie.printStackTrace();
			} catch (InvocationTargetException ite) {
				ite.printStackTrace();
			}
		}
	}

	public final boolean isSaveAsAllowed() {
		return true;
	}

	public final void commandStackChanged(EventObject event) {
		firePropertyChange(IEditorPart.PROP_DIRTY);
		super.commandStackChanged(event);
	}

	protected final PaletteViewerProvider createPaletteViewerProvider() {
		return new PaletteViewerProvider(getEditDomain()) {
			protected void configurePaletteViewer(PaletteViewer viewer) {
				super.configurePaletteViewer(viewer);
				viewer.addDragSourceListener(new TemplateTransferDragSourceListener(viewer));
			}
		};
	}

	protected final FigureCanvas getEditor() {
		return (FigureCanvas) getGraphicalViewer().getControl();
	}

	public final Object getAdapter(Class type) {
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
			page.setRootEntry(new UndoablePropertySheetEntry(getCommandStack()));
			return page;

		// On definit le manager de Zoom
		} else if (type == ZoomManager.class) {
			return ((ScalableFreeformRootEditPart) getGraphicalViewer().getRootEditPart()).getZoomManager();
		}

		// Dans tous les autres cas
		return super.getAdapter(type);
	}
}
