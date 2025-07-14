package project_biu;

import project_biu.server.HTTPServer;
import project_biu.server.MyHTTPServer;
import project_biu.servlets.ConfLoader;
import project_biu.servlets.TopicDisplayer;
import project_biu.servlets.HtmlLoader;

/**
 * Entry point for the Publisher/Subscriber visualization server.
 *
 * This class initializes and starts the HTTP server, registers all necessary servlets,
 * and keeps the server running until terminated by user input.
 */
public class Main {

    /**
     * Main method that starts the HTTP server and registers servlet handlers.
     *
     * @param args command-line arguments (not used)
     * @throws Exception if server fails to start or stop
     */
    public static void main(String[] args) throws Exception {
        // Create a new HTTP server listening on port 8080, with thread pool size 6
        HTTPServer server = new MyHTTPServer(8080, 6);

        // Register servlet for handling GET /publish (sending topic values)
        server.addServlet("GET", "/publish", new TopicDisplayer());

        // Register servlet for handling POST /upload (config upload)
        server.addServlet("POST", "/upload", new ConfLoader());

        // Register static file handler for serving HTML files (e.g., index.html)
        server.addServlet("GET", "/app/", new HtmlLoader("src/html_files"));

        // Start the server
        server.start();

        // Keep server alive until user presses Enter
        System.in.read();

        // Shutdown server gracefully
        server.close();
        System.out.println("done");
    }
}
