package UIUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class SessionUtils {

    public static String getUsername (HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        Object sessionAttribute = session != null ? session.getAttribute("username") : null;
        return sessionAttribute != null ? sessionAttribute.toString() : null;
    }

    public static String getCurrentRepository (HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        Object sessionAttribute = session != null ? session.getAttribute("repository") : null;
        return sessionAttribute != null ? sessionAttribute.toString() : null;
    }

    public static void setCurrentRepository (HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        session.setAttribute("repository", request.getParameter("repository"));
    }

    public static void clearSession (HttpServletRequest request) {
        request.getSession().invalidate();
    }
}