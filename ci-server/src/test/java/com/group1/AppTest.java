package com.group1;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.file.Paths;
import java.util.concurrent.CompletableFuture;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.json.simple.JSONObject;
import org.junit.Assert;
import org.junit.Test;

/**
 * Unit test for simple App.
 */
public class AppTest {

    // Test helper methods and variables:

    private static final String samplePath = "src/test/java/com/group1/sample_inputs/";

    private void startTempServer(int PORT) {
        ContextHandler dashContextHandler = new ContextHandler("/");
        ResourceHandler resourceHandler = new ResourceHandler();
        resourceHandler.setResourceBase("src/main/webapp/ci-frontend/dist/");
        dashContextHandler.setHandler(resourceHandler);

        ContextHandler ciContextHandler = new ContextHandler("/ci");
        ciContextHandler.setHandler(new App());

        HandlerList handlers = new HandlerList();
        handlers.addHandler(dashContextHandler);
        handlers.addHandler(ciContextHandler);

        Server server = new Server(PORT);
        server.setHandler(handlers);

        try {
            server.start();
            // System.out.println(("Sucess! Server started on port " + PORT));
            server.join();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public static String getCommitStatus(String repoName, String commitSHA, String token) {
        try {
            // define URL to GitHub API
            URL apiURL = new URL("https://api.github.com/repos/" + repoName + "/commits/" + commitSHA + "/status");

            // https://api.github.com/repos/OWNER/REPO/commits/REF/status

            // create connection
            HttpURLConnection connection = (HttpURLConnection) apiURL.openConnection();
            // BUILD POST to send data to GitHub
            // set request method to POST
            connection.setRequestMethod("GET");
            // set authorization header with the token, allow us to have access
            connection.setRequestProperty("Authorization", "token " + token);
            // set the content type to JSON
            connection.setRequestProperty("Content-Type", "application/json");
            // enable output
            connection.setDoOutput(true);

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            StringBuilder response = new StringBuilder();
            String line;

            line = reader.readLine();

            System.out.println("CONTENT");
            while (line != null) {
                response.append(line);
                line = reader.readLine();
            }

            // System.out.println(response.toString());

            JSONObject body = App.parseJSON(response.toString());

            return (String) body.get("state");

        } catch (Exception e) {

        }
        return "";
    }

    /**
     * Rigorous Test :-)
     */
    @Test
    public void shouldAnswerWithTrue() {
        assertTrue(true);
    }

    @Test
    public void shouldAnswerWithFalse() {
        assertFalse(false);
    }

    // Test that parseJSON successfully parses a simple
    // json string to a json object
    @Test
    public void successfullJSONParse() {
        try {
            String validJSON = "{\"name\":\"John\", \"age\":30}";
            JSONObject j = (JSONObject) App.parseJSON(validJSON);
            assertEquals(j.keySet().size(), 2);

        } catch (Exception e) {
            Assert.fail("Failed accessing parseJSON method");
        }
    }

    // If invalid json is parsed, the parse method should return an
    // empty JSONObject
    @Test
    public void failedJSONParse() {
        try {
            String notJSON = "just some text";
            JSONObject j = (JSONObject) App.parseJSON(notJSON);
            assertEquals(j.keySet().size(), 0);

        } catch (Exception e) {
            System.out.println("Stacktrace print is expected!");
            Assert.fail("Failed accessing parseJSON method");
        }
    }

    @Test
    public void validWebhookReqquest() {
        int PORT = 8181;
        Thread server = new Thread(() -> startTempServer(PORT));
        try {
            server.start();

            String path = samplePath + "validrequest.json";
            File file = new File(path);
            path = file.getAbsolutePath();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:" + String.valueOf(PORT) + "/ci/"))
                    .header("Content-Type", "application/json")
                    .POST(BodyPublishers.ofFile(Paths.get(path)))
                    .build();

            HttpClient client = HttpClient.newHttpClient();
            CompletableFuture<HttpResponse<String>> a = client.sendAsync(request, BodyHandlers.ofString());

            HttpResponse<String> response = a.join();
            assertEquals(200, response.statusCode());
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail("Test threw an exception and could not complete");
        }

    }

    @Test
    public void invalidWebhookRequest() {
        int PORT = 8182;
        Thread server = new Thread(() -> startTempServer(PORT));
        try {
            server.start();

            String path = samplePath + "invalidrequest.json";
            File file = new File(path);
            path = file.getAbsolutePath();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:" + String.valueOf(PORT) + "/ci/"))
                    .header("Content-Type", "application/json")
                    .POST(BodyPublishers.ofFile(Paths.get(path)))
                    .build();

            HttpClient client = HttpClient.newHttpClient();
            CompletableFuture<HttpResponse<String>> a = client.sendAsync(request, BodyHandlers.ofString());

            HttpResponse<String> response = a.join();
            assertEquals(400, response.statusCode());
        } catch (Exception e) {
            Assert.fail("Test threw an exception and could not complete");
        }
    }

    @Test
    public void notificationSetToTrue() {
        int PORT = 8183;
        Thread server = new Thread(() -> startTempServer(PORT));
        try {
            server.start();
            String token = App.readToken("secret/github_token.txt");
            System.out.println(">>>> TOKEN: " + token);

            App.createCommitStatus("DD2480-Group1/CI-server", "a3175ea9c0c709756dae28f33705651342ab6e8d", "failure",
                    token, "no description");

            String status = getCommitStatus("DD2480-Group1/CI-server", "a3175ea9c0c709756dae28f33705651342ab6e8d",
                    token);

            assertTrue(status.equals("failure"));

            String path = samplePath + "validrequest.json";
            File file = new File(path);
            path = file.getAbsolutePath();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:" + String.valueOf(PORT) + "/ci/"))
                    .header("Content-Type", "application/json")
                    .POST(BodyPublishers.ofFile(Paths.get(path)))
                    .build();

            HttpClient client = HttpClient.newHttpClient();
            CompletableFuture<HttpResponse<String>> a = client.sendAsync(request, BodyHandlers.ofString());
            HttpResponse<String> response = a.join();

            status = getCommitStatus("DD2480-Group1/CI-server", "a3175ea9c0c709756dae28f33705651342ab6e8d", token);
            System.out.println("STATUS SHOULD BE SUCCESS " + status);

            assertTrue(status.equals("success"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        // } catch(Exception e) {
        // e.printStackTrace();
        // Assert.fail("Test threw an exception and could not complete");
        // }
    }
}
