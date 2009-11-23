package fr.lip6.move.coloane.its.checks;

import fr.lip6.move.coloane.its.obs.SimpleObservable;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class ServiceResult extends SimpleObservable {

	private boolean success;
	private String report;
	private Calendar date;
	private CheckService cs;

	public ServiceResult(boolean success, String report, CheckService cs) {
		this.success = success;
		this.report = report;
		this.date = new GregorianCalendar();
		this.cs = cs;
	}

	public String getReport() {
		return report;
	}
	
	public boolean isSuccess() {
		return success;
	}

	public Calendar getDate() {
		return date;
	}

	public CheckService getParent() {
		return cs;
	}
	
	/** a nice short label describing this result, used in tree label provider.*/
	@Override
	public String toString() {
		String ret;
		if (success) {
			ret = "OK ";
		} else {
			ret = "KO ";
		}
		ret += date.toString();
		return ret;
	}

}