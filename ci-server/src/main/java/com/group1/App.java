package com.group1;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
//import java.util.List;
import java.nio.file.Paths;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
// GIT
import org.eclipse.jgit.api.Git;
//import org.eclipse.jgit.api.ListBranchCommand.ListMode;
import org.eclipse.jgit.api.errors.GitAPIException;
//import org.eclipse.jgit.lib.Ref;
//import org.eclipse.jgit.revwalk.RevCommit;

import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;

public class App extends AbstractHandler {
    private static int PORT = 8080;

    // TODO: add function description
    public void handle(String target,
            Request baseRequest,
            HttpServletRequest request,
            HttpServletResponse response)
            throws IOException, ServletException {

        System.out.println(target);

        // here you do all the continuous integration tasks
        try {
            // 1st clone your repository
            // get token from secret file
            String token = readToken("secret/github_token.txt");
            // get branch from request
            Git git = cloneRepository("https://github.com/DD2480-Group1/CI-server", token);
            System.out.println("CLONE SUCCESS, starting build, this may take a while...");
            // 2nd compile the code
            compileRepository(git.getRepository().getDirectory().getParentFile());
            System.out.println("BUILD SUCCESS, compiling clone was successfull!");
            
            // Handle git data...
            /*
            // get all branches
            List<Ref> branches = git.branchList().setListMode(ListMode.ALL).call();
            for (Ref branch : branches) {
                System.out.println("Branch: " + branch.getName());
            }

            // get commit history
            Iterable<RevCommit> logs = git.log().all().call();
            for (RevCommit rev : logs) {
                System.out.println("Commit: " + rev);
            }
            */

        } catch (GitAPIException e) {
            System.out.println("FAILED to CLONE repository...");
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        catch (InterruptedException e) {
            System.out.println("repository compile INTERRUPTED.");
            e.printStackTrace();
        }

        response.setContentType("text/html;charset=utf-8");
        response.setStatus(HttpServletResponse.SC_OK);
        baseRequest.setHandled(true);
        try {
            response.getWriter().println("CI job done");
        } catch (IOException e) {
            System.out.println("Server Response FAILED.");
            e.printStackTrace();
        }
    }

    /**
     * Clone repository function gets/downloads the repository from Git and returns it.
     * Store the files in a temporary directory that will be deleted automatically
     * when session is complete.
     * @param repoUrl URL to repository, use SSH key to get it
     * @param username username login
     * @param password password login
     * @return
     * @throws GitAPIException
     * @throws IOException
     */
    public static Git cloneRepository(String repoUrl, String token) 
        throws GitAPIException, IOException {

        File localPath = Files.createTempDirectory("ci-temporary").toFile();
        return Git.cloneRepository()
            .setURI(repoUrl)
            .setDirectory(localPath)
            // pass the token as username, is enough for token to work
            .setCredentialsProvider(new UsernamePasswordCredentialsProvider(token, ""))
            .call();
    }

    /**
     * Compile repository function uses ProcessBuilder to execture shell commands in Java.
     * Compiles the repository and catches any errors.
     * @param repoDir
     * @throws IOException
     * @throws InterruptedException
     */
    public static void compileRepository(File repoDir) 
        throws IOException, InterruptedException {
        
        // shell command used, using maven to compile repository
        String[] command = {"mvn", "clean", "install"};

        // initialize builder with commands
        ProcessBuilder processBuilder = new ProcessBuilder(command);
        // set the correct working directory, point to it
        File workingFile = new File(repoDir, "ci-server");
        processBuilder.directory(workingFile);
        Process process = processBuilder.start();
        // redirect error stream, to caputre it and print it
        /*
        processBuilder.redirectErrorStream(true);
        // start builder
        Process process = processBuilder.start();
        // capture output
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        StringBuilder output = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            output.append(line + "\n");            
        }
         */
        // wait for exit
        int exit = process.waitFor();

        // check exit code to see that compilation was successful
        if (exit != 0) {
            throw new IOException("FAILED to COMPILE, exit code: " + exit); //+ ", output: " + output.toString());
        }
    }

    // TODO: add documentation
    public static String readToken(String filePath) 
        throws IOException {

        return new String(Files.readAllBytes(Paths.get(filePath)), StandardCharsets.UTF_8).trim();
    }

    /**
     * @param str The string that should be parsed into a JSONObject
     * @return A JSONObject representing the parsed string, or an empty JSONObject 
     * if the parsing fails
     */
    private JSONObject parseJSON(String str) {
        try {
            JSONParser parser = new JSONParser();
            JSONObject json = (JSONObject) parser.parse(str);
            return json;
        } catch (org.json.simple.parser.ParseException e) {
            System.err.println("[ERROR] In parseJSON(String): Failed parsing Json from string"); 
            e.printStackTrace();
            return new JSONObject();
        }
    }

    /**
     * Main function starts a Jetty server works
     * as the CI server. 
     * @param args command-line arguments
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        Server server = new Server(PORT);
        server.setHandler(new App());
        try {
            server.start();
            System.out.println(("Sucess! Server started on port " + PORT));
            server.join();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}