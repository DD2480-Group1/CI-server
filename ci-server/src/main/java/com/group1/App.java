package com.group1;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;

import java.io.IOException;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.BufferedReader;

public class App extends AbstractHandler {
    public void handle(String target,
            Request baseRequest,
            HttpServletRequest request,
            HttpServletResponse response)
            throws IOException, ServletException {
        response.setContentType("text/html;charset=utf-8");
        response.setStatus(HttpServletResponse.SC_OK);
        baseRequest.setHandled(true);

        System.out.println(target);

        // Handle POST requests from GitHub
        if (target.equals("/") && baseRequest.getMethod().equals("POST")) {
            handleWebhook(target, baseRequest, request, response);
        }

        // here you do all the continuous integration tasks
        // for example
        // 1st clone your repository
        // 2nd compile the code

        response.getWriter().println("CI job done");
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
            return new JSONObject();
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
        BufferedReader contentReader = new BufferedReader(baseRequest.getReader());
        String line = contentReader.readLine();
        StringBuilder body = new StringBuilder();
        while (line != null) {
            body.append(line);
            line = contentReader.readLine();
        }

        JSONObject payload = parseJSON(body.toString());
        
        // Do the webhook tasks here
        // Clone repo
        // checkout branch
        // build project
        // test project
        // write files 
        // delete cloned repo
        // respond to github
    }



    // used to start the CI server in command line
    public static void main(String[] args) throws Exception {
        Server server = new Server(8080);
        server.setHandler(new App());
        server.start();
        server.join();
    }
}
