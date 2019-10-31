package Servlets;

import Exceptions.FileErrorException;
import GitObjects.UserManager;
import UIUtils.ServletUtils;
import Utils.MagitUtils;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpServlet;
import java.io.File;

@WebListener
public class SystemServlet extends HttpServlet implements ServletContextListener {
    final String SERVER_PATH = "c:\\magit-ex3";
    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        File newServerFolder = new File(SERVER_PATH);
        if(!newServerFolder.exists()){
            newServerFolder.mkdir();
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        try{
            MagitUtils.deleteFolderRecursivly(SERVER_PATH, true);
            File serverFolder = new File(SERVER_PATH);
            serverFolder.delete();
        } catch (FileErrorException e) {
            System.out.println(e.getMessage());
        }
//        UserManager userManager = ServletUtils.getUserManager(getServletContext());
//        try {
//            userManager.removeFolders();
//            File serverFolder = new File(SERVER_PATH);
//            serverFolder.delete();
//        } catch (FileErrorException e) {
//            e.printStackTrace();
//        }


    }

    private void deleteWC(String filePath, boolean deleteMagit) throws FileErrorException {
        File root = new File(filePath);
        String[] files = root.list();
        String errorMsg;

        if (files != null) {
            for (String f : files) {
                File childPath = new File(MagitUtils.joinPaths(filePath, f));
                if (childPath.isDirectory()) {
                    if (!childPath.getName().equals(".magit") || deleteMagit) {
                        deleteWC(childPath.getAbsolutePath(), deleteMagit);
                        if (!childPath.delete()) {
                            errorMsg = "Had an issue deleting a file!";
                            throw new FileErrorException(errorMsg);
                        }
                    }
                }
                else {
                    if (!childPath.delete()) {
                        errorMsg = "Had an issue deleting a file!";
                        throw new FileErrorException(errorMsg);
                    }
                }
            }
        }
    }
}
