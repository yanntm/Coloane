package fr.lip6.move.coloane.results.reports;

import fr.lip6.move.coloane.interfaces.objects.IResultsCom;
import fr.lip6.move.coloane.results.Result;

public class GenericReport extends Report {

	public GenericReport(String label, IResultsCom results) {
		super(label, results);
	}

	@Override
	protected final void buildReport() {
		getResultList().add(new Result("", "This service is not supported"));
	}

}
