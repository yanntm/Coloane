package fr.lip6.move.coloane.core.ui.properties;

import org.eclipse.core.runtime.Assert;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetWidgetFactory;

/**
 * Classe englobant un Label et un Text avec une gestion des Text en multi-ligne
 * (agrandissement automatique).<br>
 * Pour rafraichir un LabelText, il y a la méthode redraw().
 */
public class LabelText {
	/** Largeur du label */
	public static final int LABEL_WIDTH = 100;

	/** Nombre de ligne à afficher pour les Text multi-lignes */
	public static final int MAX_TEXT_HEIGHT = 4;

	private Text text;
	private CLabel label;
	private Composite parent;
	private ScrolledComposite sc;

	private int nbDelimiters = -1;

	// listener pour modifier la taille des champs de texte multiligne
	private ModifyListener listener = new ModifyListener() {
		public void modifyText(ModifyEvent e) {
			redraw();
		}
	};

	/**
	 * Constructeur de LabelText, il est appelé par tous les constructeur public
	 * @param parent Composite parent
	 * @param factory factory utilisé pour la création des widget
	 * @param label label
	 * @param value valeur
	 * @param style style SWT
	 * @param top indicateur de positionnement utilisé par le FormLayout
	 */
	private LabelText(Composite parent, TabbedPropertySheetWidgetFactory factory, String label, String value, int style, FormAttachment top) {
		FormData data;
		this.parent = parent;

		this.label = factory.createCLabel(parent, label);
		data = new FormData();
		data.left = new FormAttachment(0, 5);
		data.right = new FormAttachment(0, LABEL_WIDTH);
		data.top = top;
		this.label.setLayoutData(data);

		this.text = factory.createText(parent, value, style | SWT.V_SCROLL);
		data = new FormData();
		data.left = new FormAttachment(this.label, 5);
		data.right = new FormAttachment(100, -5);
		data.top = top;
		data.height = 15;
		text.setLayoutData(data);

		if ((style & SWT.MULTI) != 0) { //text.getVerticalBar() != null) {
			text.addModifyListener(listener);
			text.getVerticalBar().setVisible(false);
		}

		redraw();
	}

	/**
	 * @param parent Composite parent
	 * @param factory factory utilisé pour la création des widget
	 * @param label label
	 * @param value valeur
	 * @param style style SWT
	 */
	public LabelText(Composite parent, TabbedPropertySheetWidgetFactory factory, String label, String value, int style) {
		this(parent, factory, label, value, style, new FormAttachment(0, 0));
	}

	/**
	 * @param parent Composite parent
	 * @param factory factory utilisé pour la création des widget
	 * @param label label
	 * @param value valeur
	 * @param style style SWT
	 * @param top indicateur de positionnement utilisé par le FormLayout
	 */
	public LabelText(Composite parent, TabbedPropertySheetWidgetFactory factory, String label, String value, int style, LabelText top) {
		this(parent, factory, label, value, style, new FormAttachment(top.text, 0));
	}

	/**
	 * Redessine le LabelText.
	 */
	public final void redraw() {
		// En cas de texte multiligne, on limite l'agrandissement
		int newNbDelimiters = text.getText().split(Text.DELIMITER, -1).length;
		if (text.getVerticalBar() != null && newNbDelimiters != nbDelimiters) {
			int idealHeight = text.computeSize(SWT.DEFAULT, SWT.DEFAULT).y;
			nbDelimiters = newNbDelimiters;

			if (nbDelimiters <= MAX_TEXT_HEIGHT) {
				text.getVerticalBar().setVisible(false);
			} else {
				text.getVerticalBar().setVisible(true);
			}

			((FormData) text.getLayoutData()).height = Math.min(
					(idealHeight / nbDelimiters) * MAX_TEXT_HEIGHT,
					idealHeight);
		}

		// Récupération du ScrolledComposite
		if (sc == null) {
			Composite tmp = parent;
			while (!(tmp instanceof ScrolledComposite)) {
				tmp = tmp.getParent();
			}
			sc = (ScrolledComposite) tmp;
		}
		sc.setMinSize(parent.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		Composite tmp = parent;
		for (int i = 0; i < 20 && tmp != null; i++) {
			tmp.layout();
			tmp.redraw();
			tmp = tmp.getParent();
		}
	}

	/**
	 * @return <code>true</code> si le LabelText est visible
	 */
	public final boolean isVisible() {
		Assert.isTrue(text.isVisible() == label.isVisible());
		return text.isVisible();
	}

	/**
	 * Change la visibilité du LabelText
	 * @param visible nouvelle état
	 */
	public final void setVisible(boolean visible) {
		text.setVisible(visible);
		label.setVisible(visible);
		text.redraw();
		label.redraw();
	}

	/**
	 * @return la valeur du LabelText
	 */
	public final String getText() {
		return text.getText();
	}

	/**
	 * @param string nouvelle valeur du LabelText
	 */
	public final void setText(String string) {
		text.setText(string);
	}

	/**
	 * @return le nom du label
	 */
	public final String getLabel() {
		return label.getText();
	}

	/**
	 * @return le parent de ce pseudo-widget
	 */
	public final Composite getParent() {
		return parent;
	}

	/**
	 * @return le widget Text contenu dans ce LabelText
	 */
	public final Text getTextWidget() {
		return text;
	}
}