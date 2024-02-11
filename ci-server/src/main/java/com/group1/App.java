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

        // here you do all the continuous integration tasks
        // for example
        // 1st clone your repository
        // 2nd compile the code

        response.getWriter().println("CI job done");
    }
    
    /**
     * @param str The string that should be parsed into a JSONObject
     * @return A JSONObject representing the parsed string, or an empty JSONObject 
     * if the parsing fails
     * */
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

    // used to start the CI server in command line
    public static void main(String[] args) throws Exception {
        Server server = new Server(8080);
        server.setHandler(new App());
        server.start();
        server.join();
    }
}

