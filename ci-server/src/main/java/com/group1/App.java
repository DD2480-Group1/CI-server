package com.group1;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;

import java.io.IOException;

import org.json.JSONObject;
import java.io.PrintWriter;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;

class RepoHandle extends AbstractHandler {
    public void handle(String target,
            Request baseRequest,
            HttpServletRequest request,
            HttpServletResponse response)
            throws IOException, ServletException {
        response.setContentType("application/json;charset=utf-8");
        response.setStatus(HttpServletResponse.SC_OK);
        baseRequest.setHandled(true);

        JSONObject json = new JSONObject();
        json.put("type", "repo");
        json.put("repos", new String[] { "repo1", "repo2" });

        // Write the JSON object to the response
        PrintWriter out = response.getWriter();
        out.println(json.toString());
    }
}

public class App extends AbstractHandler {
    public void handle(String target,
            Request baseRequest,
            HttpServletRequest request,
            HttpServletResponse response)
            throws IOException, ServletException {

        if (target.equals("/api/repo")) {
            response.setContentType("application/json;charset=utf-8");
            response.setStatus(HttpServletResponse.SC_OK);
            baseRequest.setHandled(true);

            JSONObject json = new JSONObject();
            json.put("type", "repo");
            json.put("repos", new String[] { "repo1", "repo2" });

            // Write the JSON object to the response
            PrintWriter out = response.getWriter();
            out.println(json.toString());
            return;
        }

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

    // used to start the CI server in command line
    public static void main(String[] args) throws Exception {
        ContextHandler dashContextHandler = new ContextHandler("/");
        ResourceHandler resourceHandler = new ResourceHandler();
        resourceHandler.setResourceBase("src/main/webapp/ci-frontend/dist/");
        dashContextHandler.setHandler(resourceHandler);

        ContextHandler ciContextHandler = new ContextHandler("/ci");
        ciContextHandler.setHandler(new App());

        HandlerList handlers = new HandlerList();
        handlers.addHandler(dashContextHandler);
        handlers.addHandler(ciContextHandler);

        Server server = new Server(8080);
        server.setHandler(handlers);

        server.start();
        server.join();
    }
}
