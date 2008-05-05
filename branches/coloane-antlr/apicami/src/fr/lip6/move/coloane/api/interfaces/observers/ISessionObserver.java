package fr.lip6.move.coloane.api.interfaces.observers;

import java.util.ArrayList;

import fr.lip6.move.coloane.api.interfaces.IFkInfo;
import fr.lip6.move.coloane.api.interfaces.IFkVersion;
import fr.lip6.move.coloane.api.interfaces.IMenu;
import fr.lip6.move.coloane.api.interfaces.IUpdateItem;

public interface ISessionObserver {
	void update(IFkInfo fkInfo, ArrayList<IMenu> menu,ArrayList<IUpdateItem> update);
}
