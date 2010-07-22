package fr.lip6.move.coloane.testapiws;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import fr.lip6.move.coloane.apiws.interfaces.api.IApiConnection;
import fr.lip6.move.coloane.apiws.interfaces.session.IApiSession;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "fr.lip6.move.coloane.testApiWS";

	// The shared instance
	private static Activator plugin;
	
	private IApiConnection connection = null;
	
	private IApiSession session = null;
	
	/**
	 * The constructor
	 */
	public Activator() {
	}
	
	public IApiConnection getConnection(){
		return connection;
	}
	
	public void setConnection(IApiConnection connection){
		this.connection = connection;
	}
	
	public IApiSession getSession(){
		return session;
	}
	
	public void setSession(IApiSession session){
		this.session = session;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static Activator getDefault() {
		return plugin;
	}

	/**
	 * Returns an image descriptor for the image file at the given
	 * plug-in relative path
	 *
	 * @param path the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path) {
		return imageDescriptorFromPlugin(PLUGIN_ID, path);
	}
}