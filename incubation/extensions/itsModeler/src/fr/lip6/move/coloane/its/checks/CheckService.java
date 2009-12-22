package fr.lip6.move.coloane.its.checks;

import fr.lip6.move.coloane.core.motor.ProcessController;
import fr.lip6.move.coloane.core.motor.ProcessController.TimeOutException;
import fr.lip6.move.coloane.its.io.ITSModelWriter;
import fr.lip6.move.coloane.its.obs.SimpleObservable;
import fr.lip6.move.coloane.its.ui.forms.ITSEditorPlugin;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;

public class CheckService extends SimpleObservable implements Iterable<ServiceResult> {

	private String name="ITS reachability";
	private String workdir;
	private CheckList parent;
	private List<ServiceResult> results = new LinkedList<ServiceResult>();
	private String reportText;
	private Map<String, String> parameters = new HashMap<String, String>();

	public CheckService(CheckList parent) {
		this.parent = parent;
	}

	protected CheckService(CheckList parent, String serviceName) {
		this(parent);
		name = serviceName;
	}

	public String getName() {
		return name;
	}

	public void setWorkdir(String workdir) {
		this.workdir = workdir;
		notifyObservers();
	}

	protected void addParameter (String paramName) {
		parameters.put(paramName, "");
	}

	public Set<String> getParameters () {
		return parameters.keySet();
	}

	public boolean setParameterValue (String param, String value) {
		if (parameters.containsKey(param)) {
			if (!parameters.get(param).equals(value)) {
				parameters.put(param,value);
				notifyObservers();
			}
			return true;
		}
		return false;
	}

	public String getParameterValue (String param) {
		return parameters.get(param);
	}

	public String getWorkDir() {
		return workdir;
	}

	public IPath getWorkDirPath () {
		return new Path(workdir);
	}

	public String run() {
		ITSModelWriter mw = new ITSModelWriter();
		String report;
		boolean success = true;
		try {
			mw.exportITSModel(parent.getTypes(), parent.getType(), workdir);
			report = "Run successful in folder "+workdir;
		} catch (Exception e) {
			success  = false;
			report = "An error occurred during service invocation :" + e + e.getMessage();
		}

		// RUN THE SERVICE
		IStatus status = runTool(getWorkDirPath());
		if (! status.isOK()) {
			success  = false;
			report = "An error occurred during service invocation :" + status.getMessage();
		} else {
			report = reportText;
		}
		addResult (new ServiceResult(success,report,this));
		return report;
	}




	/**
	 * Bare bones API for launching dot. Command line options are passed to
	 * Graphviz as specified in the options parameter. The location for dot is
	 * obtained from the user preferences.
	 * 
	 * @param options
	 *            command line options for dot
	 * @return a non-zero integer if errors happened
	 * @throws IOException
	 */
	public IStatus runTool(IPath workdir) {
		IPath toolFullPath = getToolPath();
		if (toolFullPath == null || toolFullPath.isEmpty()) {
			return new Status(
					IStatus.ERROR,
					ITSEditorPlugin.getID(),
			"Please specify the absolute path to the tool in the preferences page Coloane->ITS Path.");
		}
		if (!toolFullPath.toFile().isFile()) {
			return new Status(IStatus.ERROR, ITSEditorPlugin.getID(), "Could not find ITS tool at \"" + toolFullPath
					+ "\"");
		}
		List<String> cmd = buildCommandArguments();
		ByteArrayOutputStream errorOutput = new ByteArrayOutputStream();
		ByteArrayOutputStream stdOutput = new ByteArrayOutputStream();

		try {
			final ProcessController controller =
				new ProcessController(60000, cmd.toArray(new String[cmd.size()]), null, workdir.toFile());
			controller.forwardErrorOutput(errorOutput);
			controller.forwardOutput(stdOutput);
			int exitCode = controller.execute();
			if (exitCode != 0) {
				//				return new Status(IStatus.WARNING, ITSEditorPlugin.getID(), "ITS exit code: " + exitCode + "."
				//						+ createContentMessage(errorOutput));
				//			}
				if (errorOutput.size() > 0) {
					return new Status(IStatus.WARNING, ITSEditorPlugin.getID(), createContentMessage(errorOutput));
				}
			}
			reportText = stdOutput.toString();
			return Status.OK_STATUS;
		} catch (TimeOutException e) {
			return new Status(IStatus.ERROR, ITSEditorPlugin.getID(), "Check Service process did not finish in a timely way."
					+ createContentMessage(errorOutput));
		} catch (IOException e) {
			return new Status(IStatus.ERROR, ITSEditorPlugin.getID(), "Unexpected exception executing service."
					+ createContentMessage(errorOutput), e);
		}
	}

	protected List<String> buildCommandArguments() {
		ArrayList<String> cmd = new ArrayList<String>();
		cmd.add(getToolPath().toOSString());
		//		cmd.add("--quiet");
		//		cmd.add("-T" + format);
		cmd.add("-xml");
		cmd.add("modelMain.xml");
		return cmd;
	}

	protected IPath getToolPath() {
		return ITSEditorPlugin.getDefault().getITSReachPath();
	}

	/**
	 * format error message
	 * @param errorOutput the message
	 * @return the formatted message
	 */
	private static String createContentMessage(ByteArrayOutputStream errorOutput) {
		if (errorOutput.size() == 0) {
			return "";
		}
		return " Process produced the following error output: \n" + errorOutput;
	}

	protected void addResult(ServiceResult serviceResult) {
		results.add(serviceResult);
		notifyObservers();
	}

	@Override
	public Iterator<ServiceResult> iterator() {
		return results.iterator();
	}

	public CheckList getParent() {
		return parent;
	}

	protected void setReport(String report) {
		this.reportText = report;
	}

}
