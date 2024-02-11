package com.group1;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
// Jetty Server
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;
// GIT
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
// JSON
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class App extends AbstractHandler {
    private static int PORT = 8080;

    // TODO: add function description
    public void handle(String target,
            Request baseRequest,
            HttpServletRequest request,
            HttpServletResponse response)
            throws IOException, ServletException {

        System.out.println(target);
        
        // Handle POST requests from GitHub
        if (target.equals("/") && baseRequest.getMethod().equals("POST")) {
            handleWebhook(target, baseRequest, request, response);
        }
        // TODO: add handling for other cases

        response.setContentType("text/html;charset=utf-8");
        response.setStatus(HttpServletResponse.SC_OK);
        baseRequest.setHandled(true);

        try {
            response.getWriter().println("CI job done");
        } catch (IOException e) {
            System.err.println("Server Response FAILED.");
            e.printStackTrace();
        }
    }

    /**
     * Handles the webhook request from Github
     */
    public void handleWebhook(String target,
            Request baseRequest,
            HttpServletRequest request,
            HttpServletResponse response)
            throws IOException, ServletException {
        
        // Parse the JSON file in the request body
        StringBuilder body = readRequest(baseRequest);
        // check if the body of the request is not zero
        // otherwise, request should not be handled
        if (baseRequest.getContentLength() == 0 || body == null || body.length() == 0) {
            System.out.println("NO REQUEST!");
            return;
        }
        // parse request data
        JSONObject payload = parseJSON(body.toString());

        // extract GitHub data from POST request
        JSONObject repositoryJSON = (JSONObject) payload.get("repository");
        // get repository URL
        String repositoryURL = (String) repositoryJSON.get("clone_url");
        // get repository name
        String repositoryName = (String) repositoryJSON.get("full_name");
        // get the branch name the commit is on
        String branch = (String) payload.get("ref");
        // get the commit SHA
        String commitSHA = (String) payload.get("after");

        // get github token from secret file
        String token = readToken("secret/github_token.txt");

        // variables we want to store 
        String compileOutput = "";
        String testOutput = "";
        
        // here you do all the continuous integration tasks
        try {
            // 1st clone your repository
            // get branch from request
            Git git = cloneRepository(repositoryURL, branch, token);
            System.out.println("CLONE SUCCESS, starting build, this may take a while...");
            // notify GitHub 
            createCommitStatus(repositoryName, commitSHA, "pending", token);
            
            // 2nd compile the code
            File repoDirectory = git.getRepository().getDirectory().getParentFile();
            compileOutput = compileRepository(repoDirectory);
            System.out.println("COMPILE SUCCESS, compiling clone was successfull!");
            // 3rd run tests
            testOutput = runTests(git.getRepository().getDirectory().getParentFile());
            System.out.println("TEST FINNISHED, all tests have been run");

            // 4th notify GitHub test results
            // success = all test passed, failure = one or more tests failed
            createCommitStatus(repositoryName, commitSHA, "success", token);

        } catch (GitAPIException e) {
            System.err.println("[ERROR] FAILED to CLONE repository...");
            e.printStackTrace();
        }
        catch (IOException e) {
            createCommitStatus(repositoryName, commitSHA, "error", token);
            e.printStackTrace();
        }
        catch (InterruptedException e) {
            createCommitStatus(repositoryName, commitSHA, "error", token);
            System.err.println("[ABORT] repository compile INTERRUPTED.");
            e.printStackTrace();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            // 
            System.out.println("========== DATA STATE ==========");
            // check in console that we have actually captured any console output
            boolean compileOutputState = compileOutput != "";
            System.out.println("READ compile data: " + compileOutputState);

            boolean testOutputState = testOutput != "";
            System.out.println("READ test data: " + testOutputState);
            System.out.println("CI-actions finnished");
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
    public static Git cloneRepository(String repoURL, String branch, String token) 
        throws GitAPIException, IOException {

        File localPath = Files.createTempDirectory("ci-temporary").toFile();
        return Git.cloneRepository()
            .setURI(repoURL)
            .setBranch(branch)
            .setDirectory(localPath)
            // pass the token as username, is enough for token to work
            .setCredentialsProvider(new UsernamePasswordCredentialsProvider(token, ""))
            .call();
    }

    // TODO: add documentation
    public static String runCommand(String[] command, File file) 
        throws IOException, InterruptedException {
            
        StringBuilder output = new StringBuilder();
        
        // initialize builder with commands
        ProcessBuilder processBuilder = new ProcessBuilder(command);
        // set the correct working directory, point to it
        processBuilder.directory(file);
        Process process = processBuilder.start();
        // redirect error stream, to caputre it and print it where it occurs
        processBuilder.redirectErrorStream(true);
        // capture standard output
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line;
        while ((line = reader.readLine()) != null) {
            output.append(line + "\n");            
        }
        System.out.println(output.toString());
        // wait for exit
        int exit = process.waitFor();

        // check exit code to see that compilation was successful
        if (exit != 0) {
            throw new IOException("[ERROR] Command execution failed, exit code: " + exit + "\n" + output.toString());
        }

        return output.toString();
    }

    /**
     * Compile repository function uses ProcessBuilder to execture shell commands in Java.
     * Compiles the repository and catches any errors.
     * @param repoDir
     * @throws IOException
     * @throws InterruptedException
     */
    public static String compileRepository(File repoDir) 
        throws IOException, InterruptedException {
        
        // shell command used, using maven to compile repository
        // skip the test, we only want to compile
        String[] command = {"mvn", "clean", "install", "-DskipTests"};
        File workingFile = new File(repoDir, "ci-server");
        String output = runCommand(command, workingFile);

        return output;
    }

    // TODO: add documentation
    public static String runTests(File repoDir)
        throws IOException, InterruptedException {

        // shell command used, run the test file
        String[] command = {"mvn", "-B", "test", "--file", "pom.xml"};
        File workingFile = new File(repoDir, "ci-server");
        String output = runCommand(command, workingFile);

        return output;
    }

    public static void createCommitStatus(String repoName, String commitSHA, String state, String token) {
        
        try {
            // define URL to GitHub API
            URL apiURL = new URL("https://api.github.com/repos/" + repoName + "/statuses/" + commitSHA);
            
            // create connection
            HttpURLConnection connection = (HttpURLConnection) apiURL.openConnection();
            // BUILD POST to send data to GitHub
            // set request method to POST
            connection.setRequestMethod("POST");
            // set authorization header with the token, allow us to have access
            connection.setRequestProperty("Authorization", "token " + token);
            // set the content type to JSON
            connection.setRequestProperty("Content-Type", "application/json");
            // enable output
            connection.setDoOutput(true);
            // create the JSON content with the state
            JSONObject contentJSON = new JSONObject();
            contentJSON.put("state", state);
            contentJSON.put("context", "ci");
            contentJSON.put("description", "CI Status");
            
            String JSONString = contentJSON.toJSONString();

            // write JSON content to output stream
            try (OutputStream outputStream = connection.getOutputStream()) {
                outputStream.write(JSONString.getBytes("UTF-8"));
            }

            // get response code
            int responseCode = connection.getResponseCode();
            // if equal 201 it means a request was "Created" and we are done
            // if not, throw error, something went wrong
            if (responseCode != 201) {
                throw new Exception("[ERROR] Failed to create commit status, response code: " + responseCode);
            }

        } catch (MalformedURLException e) {
            System.err.println("[ERROR] URL to GitHub API BAD.");
            e.printStackTrace();
        } catch (IOException e) {
            System.err.println("[ERROR] Failed to open connection to GitHub API.");
            e.printStackTrace();
        }
        catch (Exception e) {
            e.printStackTrace();
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
     *         if the parsing fails
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

    // TODO: add documentation
    private StringBuilder readRequest(Request baseRequest) {
        try {
            BufferedReader contentReader = new BufferedReader(baseRequest.getReader());

            System.out.println("Request Body: ");
            String line = contentReader.readLine();
    
            StringBuilder body = new StringBuilder();
    
            while (line != null) {
                body.append(line);
                line = contentReader.readLine();
            }

            return body;

        } catch (IOException e) {
            System.err.println("[ERROR] FAILED to read request.");
            e.printStackTrace();
            return null;
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