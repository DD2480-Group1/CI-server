package com.group1;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;

import java.io.IOException;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Handler.Abstract;;

public class App extends AbstractHandler {
    public void handle(String target,
            Request baseRequest,
            HttpServletRequest request,
            HttpServletResponse response)
            throws IOException, ServletException {
        // response.setContentType("text/html;charset=utf-8");
        // response.setStatus(HttpServletResponse.SC_OK);
        // baseRequest.setHandled(true);

        // System.out.println(target);

        // here you do all the continuous integration tasks
        // for example
        // 1st clone your repository
        // 2nd compile the code

        // response.getWriter().println("CI job done");
    }

    public static void main(String[] args) {
        System.out.println("Hello, World");
    }
    // used to start the CI server in command line
    // public static void main(String[] args) throws Exception {
    // Server server = new Server(8080);
    // server.setHandler(new ContinuousIntegrationServer());
    // server.start();
    // server.join();
    // }
}
