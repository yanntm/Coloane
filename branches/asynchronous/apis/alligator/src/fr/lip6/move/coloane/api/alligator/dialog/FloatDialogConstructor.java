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
package fr.lip6.move.coloane.api.alligator.dialog;

import fr.lip6.move.coloane.api.alligator.wizard.ParametersPage;

import org.cosyverif.alligator.service.parameter.FloatParameter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

/**
 * @author Clément Démoulins
 */
public final class FloatDialogConstructor implements ItemDialog<FloatParameter> {

	private final ParametersPage page;
	private final Text input;
	private final Label label;
	private final FloatParameter parameter;

	public FloatDialogConstructor(final ParametersPage page, final Composite parent, final FloatParameter parameter) {
		this.parameter = parameter;
		this.page = page;
		this.label = new Label(parent, SWT.WRAP);
		this.label.setText(parameter.getName() + ":");
		this.label.setToolTipText(parameter.getHelp());
		this.label.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));

		this.input = new Text(parent, SWT.BORDER | SWT.SINGLE);
		this.input.setToolTipText(parameter.getHelp());
		this.input.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		ModifyListener listener = new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {
				page.setPageComplete(page.isPageComplete());
				if (isValid()) {
					parameter.setValue(Float.valueOf(input.getText()));
				}
			}

		};
		this.input.addModifyListener(listener);
		if (parameter.isActualParameter()) {
			input.setText(parameter.getValue().toString());
		} else if (parameter.hasDefaultValue()) {
			this.input.setText(parameter.getDefaultValue().toString());
		}
	}

	@Override
	public boolean isValid() {
		try {
			Float.valueOf(input.getText());
			page.removeError(this);
			return true;
		} catch (NumberFormatException e) {
			page.addError(this, "For parameter '" + parameter.getName() + "', value '" + input.getText() + "' must be a float.");
			return false;
		}
	}

	@Override
	public void reset() {
		input.setText(parameter.getDefaultValue().toString());
	}

	@Override
	public void performFinish() {
	}

}
