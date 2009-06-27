package its.tpnui;

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import fr.lip6.move.coloane.interfaces.model.INode;

public class TransitionLabelProvider extends LabelProvider implements
		ITableLabelProvider {

	
	
	@Override
	public Image getColumnImage(Object element, int columnIndex) {
		// do stuff if you want nice icons in the table
		return null;
	}

	@Override
	public String getColumnText(Object element, int columnIndex) {
		INode node = (INode) element;
		
		switch (columnIndex) {
		case 0 :
			return node.getAttribute("name").getValue();
		case 1 :
			return node.getAttribute("earliestFiringTime").getValue();
		case 2 :
			return node.getAttribute("latestFiringTime").getValue();
		default :
			throw new RuntimeException("Column index out of bounds in TypeLabelProvider.");
		}
	}

	@Override
	public void addListener(ILabelProviderListener listener) {
		
	}

	@Override
	public void dispose() {
		// nothing to do

	}

	@Override
	public boolean isLabelProperty(Object element, String property) {
		// default to true to ensure update on any change 
		return true;
	}

	@Override
	public void removeListener(ILabelProviderListener listener) {
		// TODO Auto-generated method stub

	}

}
