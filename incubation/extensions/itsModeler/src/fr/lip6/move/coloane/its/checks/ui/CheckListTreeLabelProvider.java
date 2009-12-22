package fr.lip6.move.coloane.its.checks.ui;

import fr.lip6.move.coloane.its.checks.CheckService;
import fr.lip6.move.coloane.its.checks.ServiceResult;
import fr.lip6.move.coloane.its.ui.forms.ITSEditorPlugin;
import fr.lip6.move.coloane.its.ui.forms.TypeTreeLabelProvider;

import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.swt.graphics.Image;

public class CheckListTreeLabelProvider extends TypeTreeLabelProvider implements
		IBaseLabelProvider {
	
	/**
	 * Return a nice formatted text for this element.
	 * The text includes some markers of being unsatisfied (per the model)
	 * @param element to display (TypeDeclaration, concept or VariableBinding)
	 * @return a nice formatted string
	 */
	@Override
	public String getText(Object element) {
		if (element instanceof CheckService) {
			CheckService cs = (CheckService) element;
			return cs.getName();
		} else if (element instanceof ServiceResult) {
			ServiceResult sr = (ServiceResult) element;
			return sr.toString();
		}
		return super.getText(element);
	}

	/**
	 * Return a nice graphic for the item.
	 * @param element to get an icon from
	 * @return an image (uniquely) loaded from the Resources
	 */
	@Override
	public Image getImage(Object element) {
		if (element instanceof CheckService) {
			// CheckService cs = (CheckService) element;
			// TODO : an icon per service
			return ITSEditorPlugin.getDefault().getImage(ITSEditorPlugin.IMG_REACH_SERVICE);
		} else if (element instanceof ServiceResult) {
			ServiceResult sr = (ServiceResult) element;
			if (sr.isSuccess())
				return ITSEditorPlugin.getDefault().getImage(ITSEditorPlugin.IMG_RESULTOK);
			else
				return ITSEditorPlugin.getDefault().getImage(ITSEditorPlugin.IMG_RESULTNOK);
		}
		return super.getImage(element);
	}
	
}
