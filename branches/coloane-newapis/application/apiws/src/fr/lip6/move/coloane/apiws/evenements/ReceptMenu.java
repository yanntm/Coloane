package fr.lip6.move.coloane.apiws.evenements;

import fr.lip6.move.coloane.apiws.objects.menu.SubMenuImpl;
import fr.lip6.move.coloane.apiws.objects.service.ServiceImpl;
import fr.lip6.move.coloane.interfaces.api.evenements.IReceptMenu;
import fr.lip6.move.coloane.interfaces.objects.menu.ISubMenu;
import fr.lip6.move.coloane.interfaces.objects.menu.IUpdateMenu;
import fr.lip6.move.coloane.interfaces.objects.service.IService;
import fr.lip6.move.wrapper.ws.WrapperStub.LMenuModification;
import fr.lip6.move.wrapper.ws.WrapperStub.MMenu;
import fr.lip6.move.wrapper.ws.WrapperStub.SubMenu;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Cette classe représent l'objet (qui définie des menus) à envoyer aux observateurs d'événements: réception de menus.
 */
public class ReceptMenu implements IReceptMenu {

	private List<ISubMenu> menus;

	private Map<String, IUpdateMenu> updateMenus;

	private List<IService> services;

	/**
	 * Constructeur des menus à envoyer aux observateurs.
	 * @param menus les menus reçu de la part du wrapper à traduire
	 * pour être comprehensible par le core de Coloane.
	 */
	public ReceptMenu(MMenu menus) {
		// TODO Completer le condtructeur de ReceptMenu
		this.menus = new ArrayList<ISubMenu>();
		if (menus != null) {
			for (int i = 0; i < menus.getRoots().length; i++) {
				this.menus.add(new SubMenuImpl(menus.getRoots()[i].getRoot(), menus.getRoots()[i].getName()));
			}
		}

		this.services = new ArrayList<IService>();
		if (menus != null) {
			for (int i = 0; i < menus.getRoots().length; i++) {
				createListService(menus.getRoots()[i].getRoot(), menus.getRoots()[i].getName(), menus.getRoots()[i].getName(), this.services);
			}
		}

		this.updateMenus = new HashMap<String, IUpdateMenu>();

	}

	/**
	 * Constructeur des modifications à faire sur les menu à envoyer aux observateurs.
	 * @param updateMenus les modification reçu de la part du wrapper à traduire
	 * pour être comprehensible par le core de Coloane.
	 */
	public ReceptMenu(LMenuModification updateMenus) {
		// TODO Completer le condtructeur de ReceptMenu
		this.menus = new ArrayList<ISubMenu>();
		this.updateMenus = new HashMap<String, IUpdateMenu>();
	}

	/**
	 * Constructeur des menus et des modifications à faire sur les menu à envoyer aux observateurs.
	 * @param menus les menus reçu de la part du wrapper à traduire
	 * pour être comprehensible par le core de Coloane.
	 * @param updateMenus les modification reçu de la part du wrapper à traduire
	 * pour être comprehensible par le core de Coloane.
	 */
	public ReceptMenu(MMenu menus, LMenuModification updateMenus) {
		// TODO Completer le condtructeur de ReceptMenu
		this.menus = new ArrayList<ISubMenu>();
		this.updateMenus = new HashMap<String, IUpdateMenu>();
	}

	/**
	 * {@inheritDoc}
	 */
	public final List<ISubMenu> getMenus() {
		return menus;
	}

	/**
	 * {@inheritDoc}
	 */
	public final Map<String, IUpdateMenu> getUpdateMenus() {
		return updateMenus;
	}

	/**
	 * {@inheritDoc}
	 */
	public final List<IService> getServices() {
		return services;
	}

	/**
	 * Remplie la liste des services
	 * @param sub le sous-menu qui contient des services
	 * @param root le menu principal où est contenu le service.
	 * @param parent le menu parent du service.
	 * @param services la liste des services à remplire
	 */
	private void createListService(SubMenu sub, String root, String parent, List<IService> services) {
		if (sub.getServices() != null) {
			for (int i = 0; i < sub.getServices().length; i++) {
				services.add(new ServiceImpl(sub.getServices()[i], root, parent, IService.TYPE_SIMPLE));
			}
		}
		if (sub.getServicesWithObjects() != null) {
			for (int i = 0; i < sub.getServicesWithObjects().length; i++) {
				services.add(new ServiceImpl(sub.getServicesWithObjects()[i], root, parent, IService.TYPE_OBJECT));
			}
		}
		if (sub.getServicesWithOneObject() != null) {
			for (int i = 0; i < sub.getServicesWithOneObject().length; i++) {
				services.add(new ServiceImpl(sub.getServicesWithOneObject()[i], root, parent, IService.TYPE_OBJECT));
			}
		}
		if (sub.getServiceWithOneText() != null) {
			for (int i = 0; i < sub.getServiceWithOneText().length; i++) {
				services.add(new ServiceImpl(sub.getServiceWithOneText()[i], root, parent, IService.TYPE_TEXT));
			}
		}
		if (sub.getServiceWithTexts() != null) {
			for (int i = 0; i < sub.getServiceWithTexts().length; i++) {
				services.add(new ServiceImpl(sub.getServiceWithTexts()[i], root, parent, IService.TYPE_TEXT));
			}
		}
		if (sub.getSubMenus() != null) {
			for (int i = 0; i < sub.getSubMenus().length; i++) {
				createListService(sub.getSubMenus()[i], root, sub.getSubMenus()[i].getName(), services);
			}
		}
	}

}
