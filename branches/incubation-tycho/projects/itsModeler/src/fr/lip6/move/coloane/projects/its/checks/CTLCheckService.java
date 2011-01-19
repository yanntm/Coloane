package fr.lip6.move.coloane.projects.its.checks;

import fr.lip6.move.coloane.projects.its.checks.ServiceResult.Status;
import fr.lip6.move.coloane.projects.its.obs.ISimpleObserver;
import fr.lip6.move.coloane.projects.its.ui.forms.ITSEditorPlugin;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IPath;


public class CTLCheckService extends ITSCheckService implements ISimpleObserver {

	private static final String CTL_NAME = "CTL Check";
	private static final String CTL_FILE_NAME = "formula.ctl";
	private static final String CTL_FORWARD_PARAM = "Use Forward CTL model-checking (faster)";

	
	public CTLCheckService(CheckList parent) {
		super(parent, CTL_NAME);
		getParameters().addBooleanParameter(CTL_FORWARD_PARAM, true);
	}

	protected List<String> buildCommandArguments() {
		List<String> cmd = super.buildCommandArguments();
		cmd.add("-ctl");
		cmd.add(CTL_FILE_NAME);
		cmd.add("--legend");
		if (! getParameters().getBoolParameterValue(CTL_FORWARD_PARAM)) {
			cmd.add("--backward");
		} else {
			cmd.add("--forward");
		}
		return cmd;
	}

	protected IPath getToolPath() {
		return ITSEditorPlugin.getDefault().getITSCTLPath();
	}

	
	public String run(String ctlFormula, IServiceResultProvider ctlFormulaDescription) {
		currentFormula = ctlFormulaDescription;
		try {
			File file = new File(getWorkDir()+"/"+CTL_FILE_NAME);
			FileOutputStream writer = new FileOutputStream(file); //$NON-NLS-1$
			BufferedWriter sb = new BufferedWriter(new OutputStreamWriter(writer));
			file.createNewFile();
			sb.append(ctlFormula);
			sb.newLine();
			sb.close();
		} catch (Exception e) {
			String report = "An error occurred during service invocation :" + e + e.getMessage();
			addResult (new ServiceResult(Status.FAIL,report,this));
			return report;
		}
		String ret = super.run();
		currentFormula = null;
		return ret;
	}
	
	@Override
	protected Status interpretResult(String report) {
		// Now interpret the result
		if (report.contains("Formula is TRUE !")) {
			return Status.OK;
		} else if (report.contains("Formula is FALSE !")) {
			return Status.NOK;
		} else {
			return Status.FAIL;
		}
		
	}
	
	private IServiceResultProvider currentFormula;
	
	@Override
	public void addResult(ServiceResult serviceResult) {
		currentFormula.addResult(serviceResult);
	}
	
	List<CTLFormulaDescription> formulae = new ArrayList<CTLFormulaDescription>();
	
	public void addFormula (String name, String formula, String comments) {
		CTLFormulaDescription form = new CTLFormulaDescription(name,formula,comments,this);
		formulae.add(form);
		form.addObserver(this);
		notifyObservers();
	}
	
	public List<CTLFormulaDescription> getFormulae() {
		return formulae;
	}

	public void update() {
		notifyObservers();
	}
}