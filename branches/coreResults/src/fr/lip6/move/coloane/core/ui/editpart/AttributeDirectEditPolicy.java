package fr.lip6.move.coloane.core.ui.editpart;

import fr.lip6.move.coloane.core.ui.commands.AttributeEditCommand;
import fr.lip6.move.coloane.interfaces.model.IAttribute;

import org.eclipse.draw2d.Label;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.DirectEditPolicy;
import org.eclipse.gef.requests.DirectEditRequest;


/**
 * Règles pour l'éditeur d'attribut
 */
public class AttributeDirectEditPolicy extends DirectEditPolicy {

	/** {@inheritDoc} */
	@Override
	protected final Command getDirectEditCommand(DirectEditRequest edit) {
		String labelText = (String) edit.getCellEditor().getValue();
		AttributeEditPart label = (AttributeEditPart) getHost();
		AttributeEditCommand command = new AttributeEditCommand((IAttribute) label.getModel(), labelText);
		return command;
	}

	/** {@inheritDoc} */
	@Override
	protected final void showCurrentEditValue(DirectEditRequest request) {
		String value = (String) request.getCellEditor().getValue();
		Label label = (Label) getHostFigure();
		label.setText(value);
		//hack to prevent async layout from placing the cell editor twice.
		getHostFigure().getUpdateManager().performUpdate();
	}
}