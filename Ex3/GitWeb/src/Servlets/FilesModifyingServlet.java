package Servlets;

import Engine.Magit;
import Exceptions.FileErrorException;
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
        response.setContentType("application/json");
        Magit myMagit = UIUtils.ServletUtils.getMagitObject(getServletContext());
        UserManager userManager = ServletUtils.getUserManager(getServletContext());
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
                String name = request.getParameter("fileName");
                json = addNewFileToRepo(path, name);
                break;
            }

            case ("addNewFolder"):
            {
                String path = request.getParameter("filePath");
                String name = request.getParameter("fileName");
                json = addNewFolderToRepo(path, name);
                break;
            }
            case ("saveChangesInFile"):
            {
                String path = request.getParameter("filePath");
                String text = request.getParameter("data");

                json = changeFileData(path, text);
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
        File fileToDelete = new File(path);
        if (path.equals(repo.getRepoPath())){
            res.setIsHasError(true);
            res.setErrorMSG("could not delete repository folder %s.");
        }
        else {
            if (!fileToDelete.exists()) {
                res.setIsHasError(true);
                res.setErrorMSG(String.format("could not find file %s.", fileToDelete.getName()));
            } else {
                try {
                    MagitUtils.deleteFolder(path, false);
                    res.setIsHasError(false);
                    res.setData("deleted file successfully");
                } catch (FileErrorException e) {
                    res.setIsHasError(true);
                    res.setErrorMSG("folder could not be deleted");
                }
            }
        }
        return gson.toJson(res);
    }

    private String changeFileData(String path, String text) {
        Gson gson = new Gson();
        MagitStringResultObject res = new MagitStringResultObject();
        try {
            File newF = new File(path);
            if(newF.isDirectory()){
                res.setIsHasError(true);
                res.setErrorMSG("Cannot change folder content");
            }
            else{
                MagitUtils.writeToFile(path, text);
                res.setData("File changed successfully");
                res.setIsHasError(false);
            }

        } catch (IOException e) {
            res.setIsHasError(true);
            res.setErrorMSG(String.format("IO Error: %s", e.getMessage()));
        }
        return gson.toJson(res);
    }

    private String addNewFolderToRepo(String path, String name) {
        Gson gson = new Gson();
        MagitStringResultObject res = new MagitStringResultObject();
        String fullPath = MagitUtils.joinPaths(path, name);
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

    private String addNewFileToRepo(String path, String name) {
        Gson gson = new Gson();
        MagitStringResultObject res = new MagitStringResultObject();
        File file = new File(path);
        if(file.isFile()) {
            res.setIsHasError(true);
            res.setErrorMSG("Cannot add new file to an existing file!");
        } else {
            String fullPath = MagitUtils.joinPaths(path, name);
            try {
                MagitUtils.writeToFile(fullPath, "");
                res.setData("file created successfully");
                res.setIsHasError(false);
            } catch (IOException e) {
                res.setIsHasError(true);
                res.setErrorMSG(String.format("IO Error: %s", e.getMessage()));
            }
        }
        return gson.toJson(res);
    }

    private String getFileContentByPath(String path) {
        Gson gson = new Gson();
        MagitStringResultObject res = new MagitStringResultObject();
        try {
            File file = new File(path);
            if(file.isFile()) {
                String text = MagitUtils.readFileAsString(path);

                res.setData(text);
                res.setIsHasError(false);
            }
            else {
                res.setIsHasError(false);
                res.setData("");
            }
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

