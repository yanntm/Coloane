/**
 * Copyright (c) 2006-2010 MoVe - Laboratoire d'Informatique de Paris 6 (LIP6).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Jean-Baptiste VORON (LIP6) - Project Head / Initial contributor
 *   Clément DÉMOULINS (LIP6) - Project Manager
 *
 * Official contacts:
 *   coloane@lip6.fr
 *   http://coloane.lip6.fr
 */
package fr.lip6.move.coloane.tools.graphviz;

import fr.lip6.move.coloane.tools.graphviz.io.LogUtils;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;
import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;

/** 
 * The plugin class.
 */
public class GraphVizActivator extends AbstractUIPlugin {

	/** 
	 * Dot algorithms available for use
	 */
	public enum DotAlgo {
		DOT, NEATO, CIRCO, FDP;
		/**
		 * Given a string, looks up the corresponding DotAlgo. If no match,
		 * returns the default NEATO.
		 * @param term the term
		 * @return see doc
		 */
		public static DotAlgo find(String term) {
			for (DotAlgo p : DotAlgo.values()) {
				if (p.name().equals(term)) {
					return p;
				}
			}
			return NEATO;
		}

		/** 
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return super.toString().toLowerCase();
		}
	}

	/**
	 * DotLocation keeps track of how the user wants to select the dot
	 * executable. We include strings for each term so that the settings files
	 * remain human readable.
	 */
	public enum DotMethod {
		AUTO, BUNDLE, DETECT, MANUAL;

		/**
		 * Given a string, looks up the corresponding DotMethod. If no match,
		 * returns the default AUTO.
		 * @param term the term
		 * @return see doc
		 */
		public static DotMethod find(String term) {
			for (DotMethod p : DotMethod.values()) {
				if (p.name().equals(term)) {
					return p;
				}
			}
			return AUTO;
		}
	}

	public static final String DOT_SEARCH_METHOD = "dotSearchMethod";

	// The manual path is entered by the user. It should never be changed or deleted except by the user.
	public static final String DOT_MANUAL_PATH = "dotManualPath";
	
	public static final String DOT_ALGO = "dotAlgo";
	
	public static final String DOT_FILE_NAME = "dot";

	private static String ID = GraphVizActivator.class.getPackage().getName();

	private static GraphVizActivator instance;


	// Store paths as strings so they won't get screwed up by platform issues.
	/**
	 * Path to bundled dot or null if it can't be found. See
	 * extractGraphVisBinaries().
	 */
	private String bundledDotLocation;

	/**
	 * Path to autodetected dot or null if it can't be found. See
	 * autodetectDots().
	 */
	private String autodetectedDotLocation;

	/**
	 * The path the bundled Graphviz install was extracted to (null if not
	 * found/looked up).
	 */
	public GraphVizActivator() {
		instance = this;
	}

	/** 
	 * Singleton
	 * @return the sole instance
	 */
	public static GraphVizActivator getInstance() {
		return instance;
	}

	/**
	 * Call to logger error.
	 * @param message the msg
	 * @param e the exception.
	 */
	public static void logUnexpected(String message, Exception e) {
		LogUtils.logError(ID, message, e);
	}

	/**
	 * This routine browses through the user's PATH looking for dot executables.
	 * 
	 * @return the absolute path if a suitable dot is found, null if not. This
	 *         is normally called once at plugin startup but it can also be
	 *         called while the plugin is running (in case user has installed
	 *         dot without restarting Eclipse).
	 */
	public final String autodetectDots() {
		autodetectedDotLocation = null;
		String paths = System.getenv("PATH");
		for (String path : paths.split(File.pathSeparator)) {
			File directory = new File(path);
			File[] matchingFiles = directory.listFiles(new ExecutableFinder(DOT_FILE_NAME));
			if (matchingFiles != null && matchingFiles.length > 0) {
				File found = matchingFiles[0];
				autodetectedDotLocation = found.getAbsolutePath();
				break;
			}
		}
		return autodetectedDotLocation;
	}
	
	/**
	 * A file filter that looks for executable files.
	 */
	private static class ExecutableFinder implements FileFilter {
		private String nameToMatch;

		/**
		 * constructor
		 * @param nameToMatch the name
		 */
		public ExecutableFinder(String nameToMatch) {
			this.nameToMatch = nameToMatch;
		}

		/** 
		 * {@inheritDoc}
		 */
		public boolean accept(File candidate) {
			if (!isExecutable(candidate)) {
				return false;
			}
			return candidate.getName().equalsIgnoreCase(nameToMatch)
							|| candidate.getName().startsWith(nameToMatch + '.');
		}
	}

	/**
	 * Tries to find and (if required) extract the binaries for a bundled
	 * Graphviz install. If cannot find, the Graphviz integration will only work
	 * if the #
	 * 
	 * @param context the context
	 * @return null if not found or the path otherwise
	 * @throws IOException if problems
	 */
	private String extractGraphVizBinaries(BundleContext context) throws IOException {
		Enumeration< ? > found = context.getBundle().findEntries("/graphviz-min/", "eg_dot*", false);
		if (found == null || !found.hasMoreElements()) {
			return null;
		}

		URL dotURL = (URL) found.nextElement();
		URL basicURL = new URL(dotURL, ".");
		URL fileURL = FileLocator.toFileURL(basicURL);
		if (!fileURL.getProtocol().startsWith("file")) {
			IStatus bundledBinariesStatus =
							new Status(IStatus.ERROR, ID, "Unexpected protocol for location of GraphViz binaries: '"
											+ fileURL + "'");
			Platform.getLog(context.getBundle()).log(bundledBinariesStatus);
			return null;
		}
		bundledDotLocation = FileLocator.toFileURL(dotURL).getPath();
		return bundledDotLocation;
	}

	/**
	 * bundled location if available
	 * @return null if none found.
	 */
	public final String getBundledDotLocation() {
		return bundledDotLocation;
	}

	// preference getters and setters

	/**
	 * Gets the path to the dot executable. It takes user's preferences into
	 * account so it should always do the right thing.
	 * @return the path to dot after resolution.
	 */
	public final IPath getDotLocation() {
		final String manualLocation = getManualDotPath();
		switch (getDotSearchMethod()) {
		case AUTO:
			if (bundledDotLocation != null) {
				return new Path(bundledDotLocation);
			}
			if (autodetectedDotLocation != null) {
				return new Path(autodetectedDotLocation);
			}
			if (manualLocation != null) {
				return new Path(manualLocation);
			} else {
				return null;
			}

		case BUNDLE:
			if (bundledDotLocation != null) {
				return new Path(bundledDotLocation);
			} else {
				return null;
			}

		case DETECT:
			if (autodetectedDotLocation != null) {
				return new Path(autodetectedDotLocation);
			} else {
				return null;
			}

		case MANUAL:
			if (manualLocation != null) {
				return new Path(manualLocation);
			} else {
				return null;
			}
		default:
			break;
		}

		// Someone must have edited the prefs file manually... just reset the
		// value.
		setDotSearchMethod(DotMethod.AUTO);
		return null;
	}

	/** Scans for dot search method.
	 * 
	 * @return the current dot method
	 */
	public final DotMethod getDotSearchMethod() {
		String value = getPreference(DOT_SEARCH_METHOD);
		if (value != null) {
			return DotMethod.find(value);
		} else {
			return DotMethod.AUTO;
		}
	}
	
	/**
	 * Scan for dot algo.
	 * @return the current dot algo.
	 */
	public final DotAlgo getDotAlgo() {
		String value = getPreference(DOT_ALGO);
		if (value != null) {
			return DotAlgo.find(value);
		} else {
			return DotAlgo.NEATO;
		}
	}
	
	/**
	 * split the dot path to get dot bin dir.
	 * @return the folder found.
	 */
	public final File getGraphVizDirectory() {
		IPath dotLocation = getDotLocation();
		if (dotLocation == null) {
			return null;
		} else {
			return dotLocation.removeLastSegments(1).toFile();
		}
	}

	/** Grab the manual dot path from prefs.
	 * 
	 * @return the path
	 */
	public final String getManualDotPath() {
		return getPreference(DOT_MANUAL_PATH);
	}

	/** Returns the preference with the given name
	 * @param preferenceName the pref
	 * @return the value */
	public final String getPreference(String preferenceName) {
		Preferences node =
						Platform.getPreferencesService().getRootNode().node(InstanceScope.SCOPE).node(
										GraphVizActivator.ID);
		return node.get(preferenceName, null);
	}

	/** 
	 * test for bundled install
	 * @return true if this is the case
	 */
	public final boolean hasBundledInstall() {
		return bundledDotLocation != null;
	}

	/**
	 * update setting in preferences.
	 * 
	 * @param dotMethod the new method
	 */
	public final void setDotSearchMethod(DotMethod dotMethod) {
		setPreference(DOT_SEARCH_METHOD, dotMethod.name());
	}

	/**
	 * update setting in preferences.
	 * @param dotAlgo new setting
	 */
	public final void setDotAlgo(DotAlgo dotAlgo) {
		setPreference(DOT_ALGO, dotAlgo.name());
	}

	/**
	 * update setting in preferences.
	 * @param newLocation the location
	 */
	public final void setManualDotPath(String newLocation) {
		setPreference(DOT_MANUAL_PATH, newLocation);
	}

	/**
	 * Sets the given preference to the given value.
	 * @param preferenceName the preference
	 * @param value the value to set
	 */
	private void setPreference(String preferenceName, String value) {
		IEclipsePreferences root = Platform.getPreferencesService().getRootNode();
		Preferences node = root.node(InstanceScope.SCOPE).node(GraphVizActivator.ID);
		node.put(preferenceName, value);
		try {
			node.flush();
		} catch (BackingStoreException e) {
			LogUtils.logError(ID, "Error updating preferences.", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void start(BundleContext context) throws Exception {
		// try to find a bundled dot.
		try {
			extractGraphVizBinaries(context);
		} catch (IOException e) {
			LogUtils.logError(ID, "Error looking for dot executable.", e);
		}

		// then try to find any installed copies of dot
		autodetectDots();
		if (autodetectedDotLocation == null && getDotSearchMethod() == DotMethod.DETECT) {
			setDotSearchMethod(DotMethod.AUTO);
			LogUtils.logWarning(ID, "Could not find a suitable dot executable.  Please specify one using Window -> Preferences -> Graphviz.", null);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void stop(BundleContext context) throws Exception {
		// nothing to do
	}

	
	/**
	 * Returns whether the given file is executable. Depending on the platform
	 * we might not get this right.
	 * @param file the file to test
	 * @return true if executable is detected with certitude
	 */
	public static boolean isExecutable(File file) {
		if (!file.isFile()) {
			return false;
		}
		if (Platform.getOS().equals(Platform.OS_WIN32)) {
			// executable attribute is a *ix thing, on Windows all files are
			// executable
			return true;
		}
		IFileStore store = EFS.getLocalFileSystem().fromLocalFile(file);
		if (store == null) {
			return false;
		}
		return store.fetchInfo().getAttribute(EFS.ATTRIBUTE_EXECUTABLE);
	}

	/**
	 * @return the ID
	 */
	public static String getID() {
		return ID;
	}

}
