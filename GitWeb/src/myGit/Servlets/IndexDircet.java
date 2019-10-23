package myGit.Servlets;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(
        name = "indexDirect",
        urlPatterns = {"/index.html"}
)
public class IndexDircet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.sendRedirect("Pages/LoginPage.html");
        //TODO: Change URL to fit "Localhost:8080/Hacovshim3/"  - instead "Localhost:8080/" how it's right now
    }
}
