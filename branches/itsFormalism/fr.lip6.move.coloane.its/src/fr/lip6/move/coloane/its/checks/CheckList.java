package fr.lip6.move.coloane.its.checks;

import fr.lip6.move.coloane.its.ITypeListProvider;
import fr.lip6.move.coloane.its.TypeDeclaration;
import fr.lip6.move.coloane.its.TypeList;
import fr.lip6.move.coloane.its.obs.ISimpleObserver;
import fr.lip6.move.coloane.its.obs.SimpleObservable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class CheckList extends SimpleObservable implements Iterable<CheckService>, ISimpleObserver, ITypeListProvider {

	private TypeDeclaration type;
	private List<CheckService> services;
	private ITypeListProvider typeP;

	public CheckList(TypeDeclaration td, ITypeListProvider typeP) {
		this.typeP = typeP;
		type = td;
		services = new ArrayList<CheckService>();
	}

	public TypeDeclaration getType() {
		return type;
	}

	@Override
	public Iterator<CheckService> iterator() {
		return services.iterator();
	}

	public void addCheck(CheckService checkService) {
		services.add(checkService);
		checkService.addObserver(this);
		notifyObservers();
	}

	@Override
	public void update() {
		notifyObservers();
	}

	@Override
	public TypeList getTypes() {
		return typeP.getTypes();
	}

}