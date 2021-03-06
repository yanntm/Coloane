/**
 * Copyright (c) 2006-2010 MoVe - Laboratoire d'Informatique de Paris 6 (LIP6).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Jean-Baptiste VORON (LIP6) - Project Head / Initial contributor
 *   Clément DÉMOULINS (LIP6) - Project Manager
 *
 * Official contacts:
 *   coloane@lip6.fr
 *   http://coloane.lip6.fr
 */
package fr.lip6.move.coloane.core.ui.wizards.newmodel;

import fr.lip6.move.coloane.core.main.Coloane;
import fr.lip6.move.coloane.core.model.factory.FormalismManager;
import fr.lip6.move.coloane.interfaces.formalism.IFormalism;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

/**
 * First page of the creation model wizard: selectf the formalism.
 * 
 * @see NewModelWizard
 * @author Jean-Baptiste Voron
 */
public class SelectFormalismPage extends WizardPage {
	/** Height used to space the wizard page components */
	private static final int GRID_HEIGHT = 20;

	/* Graphical components */
	private Label label = null;
	private Table tableFormalism = null;

	/** Formalism cache (used to manipulate IFormalism rather than String) */
	private Map<String, IFormalism> formalismCache = new HashMap<String, IFormalism>();

	/**
	 * Constructor
	 */
	public SelectFormalismPage() {
		super("newmodel"); //$NON-NLS-1$
		setTitle(Messages.SelectFormalismPage_0);
		setDescription(Messages.SelectFormalismPage_1);
	}

	/** {@inheritDoc} */
	@Override
	public final void createControl(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);

		GridData gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		gridData.heightHint = GRID_HEIGHT;
		gridData.verticalAlignment = GridData.FILL;

		// Select box
		label = new Label(composite, SWT.NONE);
		label.setText(Messages.SelectFormalismPage_2);

		tableFormalism = new Table(composite, SWT.SINGLE | SWT.BORDER);
		tableFormalism.setLayoutData(gridData);
		tableFormalism.setHeaderVisible(false);
		tableFormalism.setLinesVisible(false);
		tableFormalism.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				return;
			}

			@Override
			public void widgetSelected(SelectionEvent e) {
				TableItem selectedTableItem = (TableItem) e.item;
				String selectedFormalism = selectedTableItem.getText();
				updateStatus(selectedFormalism);
			}
		});

		tableFormalism.removeAll();

		// Fetch the formalisms list
		List<IFormalism> listOfFormalisms = FormalismManager.getInstance().getListOfFormalisms();

		// Browse and display them into a list with their icon
		for (IFormalism formalism : listOfFormalisms) {
			formalismCache.put(formalism.getName().toLowerCase(), formalism);
			TableItem item = new TableItem(tableFormalism, SWT.NULL); // Add it
																		// into
																		// the
																		// table
			item.setText(formalism.getName().toUpperCase()); // Set the text
			item.setImage(ImageDescriptor.createFromFile(Coloane.class, formalism.getImageName()).createImage()); // Set
																													// the
																													// picture
		}

		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 1;

		composite.setLayout(gridLayout);
		setControl(composite);
	}

	/** {@inheritDoc} */
	@Override
	public final boolean canFlipToNextPage() {
		return (getErrorMessage() == null) && (tableFormalism.getSelectionCount() > 0);
	}

	/** {@inheritDoc} */
	@Override
	public final boolean isPageComplete() {
		return (getErrorMessage() == null) && (tableFormalism.getSelectionCount() > 0);
	}

	/**
	 * Update the selected formalism
	 * @param selectedFormalismName Update to this new value
	 */
	private void updateStatus(String selectedFormalismName) {
		IFormalism selectedFormalism = formalismCache.get(selectedFormalismName.toLowerCase());
		((NewModelWizard) getWizard()).setFormalism(selectedFormalism);
		getWizard().getContainer().updateButtons();
	}
}
