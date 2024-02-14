package com.group1;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.net.URI;
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
}
