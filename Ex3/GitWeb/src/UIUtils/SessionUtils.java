package UIUtils;

import GitObjects.PullRequestObject;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class SessionUtils {

    public static String getUsername (HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        Object sessionAttribute = session != null ? session.getAttribute("username") : null;
        return sessionAttribute != null ? sessionAttribute.toString() : null;
    }

    public static PullRequestObject getPullRequest (HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        Object sessionAttribute = session != null ? session.getAttribute("pullRequest") : null;
        return sessionAttribute != null ? (PullRequestObject) sessionAttribute : null;
    }

    public static void setPullRequest (HttpServletRequest request, PullRequestObject pr) {
        HttpSession session = request.getSession(false);
        session.setAttribute("pullRequest", pr);
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