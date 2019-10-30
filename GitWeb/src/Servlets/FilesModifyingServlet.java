package Servlets;

import Engine.Magit;
import GitObjects.*;
import UIUtils.ServletUtils;
import Utils.MagitStringResultObject;
import Utils.MagitUtils;
import Utils.WorkingCopyChanges;
import com.google.gson.Gson;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(
        urlPatterns = "/fileChange"
)

public class FilesModifyingServlet extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response) {
        UserManager userManager = ServletUtils.getUserManager(getServletContext());
        Magit myMagit = ServletUtils.getMagitObject(getServletContext());
        response.setContentType("application/json");

        String usernameFromSession = UIUtils.SessionUtils.getUsername(request);
        User currUser = userManager.getUserByName(usernameFromSession);

        String repoName = UIUtils.SessionUtils.getCurrentRepository(request);
        Repository repo = userManager.getUserRepository(currUser, repoName);

        String action = request.getParameter("action");
        String json = "";
        switch (action){
            case ("getFileContent"):
            {
                String path = request.getParameter("filePath");
                json = getFileContentByPath(path);
                break;
            }
            case ("addNewFile"):
            {
                String path = request.getParameter("filePath");
                json = addNewFileToRepo(path, repo);
                break;
            }

            case ("addNewFolder"):
            {
                String path = request.getParameter("filePath");
                json = addNewFolderToRepo(path, repo);
                break;
            }
            case ("saveChangesInFile"):
            {
                String path = request.getParameter("filePath");
                String text = request.getParameter("data");

                json = changeFileData(repo, path, text);
                break;
            }
            case ("deleteFile"):
            {
                String path = request.getParameter("filePath");
                json = deleteFile(path, repo);
                break;
            }

        }
        try (PrintWriter out = response.getWriter()) {
            out.println(json);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String deleteFile(String path, Repository repo) {
        Gson gson = new Gson();
        MagitStringResultObject res = new MagitStringResultObject();
        String fullPath = MagitUtils.joinPaths(repo.getRepoPath(), path);
        File fileToDelete = new File(fullPath);
        if (!fileToDelete.exists()){
            res.setIsHasError(true);
            res.setErrorMSG(String.format("could not find file %s.", fileToDelete.getName()));
        }
        else{
            if (fileToDelete.delete()){
                res.setIsHasError(false);
                res.setData("deleted file successfully");
            }
            else{
                res.setIsHasError(true);
                res.setErrorMSG("file could not be deleatd");
            }
        }
        return gson.toJson(res);
    }

    private String changeFileData(Repository repo, String path, String text) {
        Gson gson = new Gson();
        MagitStringResultObject res = new MagitStringResultObject();
        String fullPath = MagitUtils.joinPaths(repo.getRepoPath(), path);
        try {
            MagitUtils.writeToFile(fullPath, text);
            res.setData("file created successfully");
            res.setIsHasError(false);
        } catch (IOException e) {
            res.setIsHasError(true);
            res.setErrorMSG(String.format("IO Error: %s", e.getMessage()));
        }
        return gson.toJson(res);
    }

    private String addNewFolderToRepo(String path, Repository repo) {
        Gson gson = new Gson();
        MagitStringResultObject res = new MagitStringResultObject();
        String fullPath = MagitUtils.joinPaths(repo.getRepoPath(), path);
        File newFolder = new File(fullPath);
        if (newFolder.exists()){
            res.setIsHasError(true);
            res.setErrorMSG("Folder already exist.");
        }
        else{
            if (newFolder.mkdir()){
                res.setIsHasError(false);
                res.setData("created folder successfully");
            }
            else{
                res.setIsHasError(true);
                res.setErrorMSG("Folder could not be created");
            }
        }
        return gson.toJson(res);
    }

    private String addNewFileToRepo(String path, Repository repo) {
        Gson gson = new Gson();
        MagitStringResultObject res = new MagitStringResultObject();
        String fullPath = MagitUtils.joinPaths(repo.getRepoPath(), path);
        try {
            MagitUtils.writeToFile(fullPath, "");
            res.setData("file created successfully");
            res.setIsHasError(false);
        } catch (IOException e) {
            res.setIsHasError(true);
            res.setErrorMSG(String.format("IO Error: %s", e.getMessage()));
        }
        return gson.toJson(res);
    }

    private String getFileContentByPath(String path) {
        Gson gson = new Gson();
        MagitStringResultObject res = new MagitStringResultObject();
        try {
            String text = MagitUtils.readFileAsString(path);
            res.setData(text);
            res.setIsHasError(false);
        } catch (IOException e) {
            res.setIsHasError(true);
            res.setErrorMSG(String.format("IO Error: %s", e.getMessage()));
        }
        return gson.toJson(res);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        processRequest(request, response);
    }


    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) {
        processRequest(request, response);
    }

    @Override
    public String getServletInfo() {
        return "Servlet handling parsing and validating of xml file.";
    }
}

