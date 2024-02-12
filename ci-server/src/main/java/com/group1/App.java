package com.group1;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;

import java.io.IOException;
import java.io.File;
import java.io.FilenameFilter;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.json.simple.JSONObject;
import org.json.simple.JSONArray;

public class App extends AbstractHandler {
    public void handle(String target,
            Request baseRequest,
            HttpServletRequest request,
            HttpServletResponse response)
            throws IOException, ServletException {

        if (target.startsWith("/api/repo")) {
            response.setContentType("application/json;charset=utf-8");
            response.setStatus(HttpServletResponse.SC_OK);
            baseRequest.setHandled(true);

            JSONObject obj = new JSONObject();
            JSONArray list = new JSONArray();

            String[] repos = new File("data").list(new FilenameFilter() {
                public boolean accept(File dir, String name) {
                    return new File(dir, name).isDirectory();
                }
            });

            for (String repo : repos) {
                list.add(repo);
            }
            obj.put("repos", list);
            obj.put("type", "repo");

            response.getWriter().println(obj.toJSONString());

            return;
        }

        if (target.startsWith("/api/branch")) {
            String repo = request.getParameter("repo");
            if (repo.equals("")) {
                response.setContentType("application/json;charset=utf-8");
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                JSONObject obj = new JSONObject();
                JSONArray list = new JSONArray();
                obj.put("branches", list);
                obj.put("type", "branch");
                baseRequest.setHandled(true);
                return;
            }

            response.setContentType("application/json;charset=utf-8");
            response.setStatus(HttpServletResponse.SC_OK);
            baseRequest.setHandled(true);

            JSONObject obj = new JSONObject();
            JSONArray list = new JSONArray();

            String[] branches = new File("data/" + repo).list(new FilenameFilter() {
                public boolean accept(File dir, String name) {
                    return new File(dir, name).isDirectory();
                }
            });

            for (String branch : branches) {
                list.add(branch);
            }
            obj.put("branches", list);
            obj.put("type", "branch");

            response.getWriter().println(obj.toJSONString());

            return;
        }
        if (target.startsWith("/api/commit")) {
            String repo = request.getParameter("repo");
            String branch = request.getParameter("branch");
            if (repo.equals("") || branch.equals("")) {
                response.setContentType("application/json;charset=utf-8");
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                JSONObject obj = new JSONObject();
                JSONArray list = new JSONArray();
                obj.put("commits", list);
                obj.put("type", "commit");
                baseRequest.setHandled(true);
                return;
            }

            response.setContentType("application/json;charset=utf-8");
            response.setStatus(HttpServletResponse.SC_OK);
            baseRequest.setHandled(true);

            JSONObject obj = new JSONObject();

            String[] commits = new File("data/" + repo + "/" + branch).list();

            JSONArray commitList = new JSONArray();
            for (String commit : commits) {
                JSONObject commitObj = new JSONObject();
                commitObj.put("name", commit);
                commitObj.put("hash", "123345");
                commitObj.put("log", "commit log\n\n hahaha");
                commitObj.put("compilePass", "0");
                commitObj.put("testPass", "0");

                commitList.add(commitObj);
            }

            obj.put("commits", commitList);
            obj.put("type", "commit");

            response.getWriter().println(obj.toJSONString());

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
