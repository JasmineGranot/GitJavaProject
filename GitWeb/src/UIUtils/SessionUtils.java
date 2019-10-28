package UIUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class SessionUtils {

    public static String getUsername (HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        Object sessionAttribute = session != null ? session.getAttribute("username") : null;
        return sessionAttribute != null ? sessionAttribute.toString() : null;
    }

    public static String getOtherUser (HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        Object sessionAttribute = session != null ? session.getAttribute("otherUser") : null;
        return sessionAttribute != null ? sessionAttribute.toString() : null;
    }

    public static void setOtherUser (HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        session.setAttribute("otherUser", request.getParameter("otherUser"));
    }

    public static String getCurrentRepository (HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        Object sessionAttribute = session != null ? session.getAttribute("repository") : null;
        return sessionAttribute != null ? sessionAttribute.toString() : null;
    }

    public static void setCurrentRepository (HttpServletRequest request, String repoName) {
        HttpSession session = request.getSession(false);
        session.setAttribute("repository", repoName);
    }

    public static void clearSession (HttpServletRequest request) {
        request.getSession().invalidate();
    }

    public static void clearCurrentUser (HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        session.setAttribute("username", null);
    }
}