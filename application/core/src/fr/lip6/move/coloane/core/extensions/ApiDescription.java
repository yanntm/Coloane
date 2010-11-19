package fr.lip6.move.coloane.core.extensions;

import fr.lip6.move.coloane.core.exceptions.ColoaneException;
import fr.lip6.move.coloane.core.session.Session;
import fr.lip6.move.coloane.core.ui.menus.ColoaneAPIRootMenu;
import fr.lip6.move.coloane.interfaces.api.IApi;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.console.MessageConsole;

/**
 * Describe a connected API.<br>
 * Those information will be useful to build the menus.
 * 
 * @author Jean-Baptiste Voron
 */
public class ApiDescription {
	
	/** Name of the services API */
	private String name;
	
	/** Description of the API */
	private String description;
	
	/** Icon associated with the API */
	private ImageDescriptor icon = null;
	
	/** The main class of the API */
	private IApi apiClass;
	
	/** The API root menu */
	private ColoaneAPIRootMenu rootMenu;
	
	/** 
	 * The associated message console
	 * Note that several APIs may share the same console.
	 * That's why the console is provided by the session
	 */
	private MessageConsole console;

	/**
	 * Constructor
	 * @param apiClass The main class of the API
	 * @param name The API name
	 * @throws ColoaneException if the API name is <code>null</code>
	 */
	public ApiDescription(IApi apiClass, String name) throws ColoaneException {
		this(apiClass, name, ""); //$NON-NLS-1$
	}
	
	/**
	 * Constructor
	 * @param apiClass The main class of the API
	 * @param name The API name
	 * @param description The API description (will be used as tip)
	 * @throws ColoaneException if the API name is <code>null</code>
	 */
	public ApiDescription(IApi apiClass, String name, String description) throws ColoaneException {
		this(apiClass, name, description, null);
	}
	
	/**
	 * Constructor
	 * @param apiClass The main class of the API
	 * @param name The API name
	 * @param description The API description (will be used as tip)
	 * @param iconPath The icon associated with the API
	 * @throws ColoaneException if the API name is <code>null</code>
	 */
	public ApiDescription(IApi apiClass, String name, String description, String iconPath) throws ColoaneException {
		this.apiClass = apiClass;

		this.name = name;
		if (name == null)
			throw new ColoaneException("The API name cannot be null"); //$NON-NLS-1$
		
		this.description = description;
		if (this.description == null)
			this.description = ""; //$NON-NLS-1$
		
		if (iconPath != null) {
			this.icon = ImageDescriptor.createFromFile(this.apiClass.getClass(), "/" + iconPath); //$NON-NLS-1$
		}
	}
	
	/**
	 * @return the API name
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * @return the API description
	 */
	public String getDescription() {
		return description;
	}
	
	/**
	 * @return the icon associated with the API
	 */
	public ImageDescriptor getIcon() {
		return icon;
	}
	
	/**
	 * @return the main class of the API
	 */
	public IApi getApiClass() {
		return apiClass;
	}
	
	/**
	 * @return The root menu associated with the API
	 */
	public ColoaneAPIRootMenu getRootMenu() {
		return rootMenu;
	}
	
	/**
	 * Set the root menu for the API
	 * @param rootMenu The root menu (including sub-menus) for this API
	 */
	public void setRootMenu(ColoaneAPIRootMenu rootMenu) {
		this.rootMenu = rootMenu;
	}
	
	/**
	 * Set the console used by the session to which the API is attached to
	 * @param console The console provided y the session
	 * @see Session#getConsole()
	 */
	public void setConsole(MessageConsole console) {
		this.console = console;
	}
	
	/**
	 * @return The console on which messages must be written to
	 */
	public MessageConsole getConsole() {
		return console;
	}
}