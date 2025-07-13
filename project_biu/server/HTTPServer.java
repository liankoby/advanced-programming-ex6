package project_biu.server;

import project_biu.servlets.Servlet;

public interface HTTPServer {
    void start();  // Start the server (blocking or non-blocking depending on implementation)
    void close();  // Gracefully shut down the server
    void addServlet(String method, String uri, Servlet servlet);  // Register a servlet
}
