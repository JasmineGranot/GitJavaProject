package UIUtils;

import Engine.Magit;
import GitObjects.UserManager;

import javax.servlet.ServletContext;
import java.util.LinkedList;

public class ServletUtils {

	private static final String USER_MANAGER_ATTRIBUTE_NAME = "userManager";
	private static final String MAGIT_ATTRIBUTE_NAME = "Magit";

	private static final Object servletManagerLock = new Object();

	public static UserManager getUserManager(ServletContext servletContext) {

		synchronized (servletManagerLock) {
			if (servletContext.getAttribute(USER_MANAGER_ATTRIBUTE_NAME) == null) {
				servletContext.setAttribute(USER_MANAGER_ATTRIBUTE_NAME, new UserManager());
			}
		}
		return (UserManager) servletContext.getAttribute(USER_MANAGER_ATTRIBUTE_NAME);
	}

	public static Magit getMagitObject(ServletContext servletContext) {

		synchronized (servletManagerLock) {
			if (servletContext.getAttribute(MAGIT_ATTRIBUTE_NAME) == null) {
				servletContext.setAttribute(MAGIT_ATTRIBUTE_NAME, new Magit());
			}
		}
		return (Magit) servletContext.getAttribute(MAGIT_ATTRIBUTE_NAME);
	}

}
