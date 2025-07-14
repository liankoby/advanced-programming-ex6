package project_biu.server;

import project_biu.servlets.Servlet;

/**
 * Interface representing a simple HTTP server.
 * <p>
 * Allows starting and stopping the server, and registering servlets to handle requests.
 * This abstraction enables flexibility in implementing different server strategies.
 */
public interface HTTPServer {

    /**
     * Starts the server and begins listening for incoming HTTP requests.
     * The implementation may be blocking or non-blocking depending on the class.
     */
    void start();

    /**
     * Gracefully shuts down the server, releasing any open sockets or resources.
     */
    void close();

    /**
     * Registers a servlet to handle requests for a given HTTP method and URI prefix.
     *
     * @param method  the HTTP method (e.g., "GET", "POST")
     * @param uri     the request path or path prefix to match
     * @param servlet the servlet to handle matching requests
     */
    void addServlet(String method, String uri, Servlet servlet);
}
