package fr.lip6.move.coloane.ui.views;

import org.eclipse.draw2d.Connection;
import org.eclipse.draw2d.IFigure;

public interface IArcFigure extends IFigure, Connection {
	public void setLabelText(String text);
}
