package project_biu.servlets;

import project_biu.server.RequestParser.RequestInfo;

import java.io.*;
import java.nio.file.Files;

/**
 * A servlet that serves static HTML files from a specified base directory.
 * <p>
 * This servlet is mapped to <code>/app/*</code> and is responsible for delivering
 * files like <code>index.html</code>, <code>form.html</code>, etc., to the browser.
 * </p>
 */
public class HtmlLoader implements Servlet {
    private final String basePath;

    /**
     * Creates a new HtmlLoader with a base path to serve files from.
     *
     * @param basePath the root directory where HTML files are stored
     */
    public HtmlLoader(String basePath) {
        this.basePath = basePath;
        System.out.println("HtmlLoader initialized with basePath: " + basePath);
    }

    /**
     * Handles HTTP GET requests and serves the requested HTML file.
     *
     * @param ri        the request info, including URI
     * @param toClient  the output stream to write the file content to
     * @throws IOException if the file cannot be read or written
     */
    @Override
    public void handle(RequestInfo ri, OutputStream toClient) throws IOException {
        String requestUri = ri.getUri();
        String relativePath;

        if (requestUri.startsWith("/app/")) {
            relativePath = requestUri.substring("/app/".length());
        } else {
            relativePath = requestUri;
        }

        File file = new File(basePath, relativePath);

        System.out.println("HtmlLoader handling URI: " + requestUri);
        System.out.println("Resolved file path: " + file.getAbsolutePath());

        PrintWriter out = new PrintWriter(toClient);

        if (!file.exists()) {
            System.out.println("❌ File not found: " + file.getAbsolutePath());
            out.println("HTTP/1.1 404 Not Found");
            out.println();
            out.println("<html><body><h1>404 - File Not Found</h1></body></html>");
        } else {
            System.out.println("✅ Serving file: " + file.getAbsolutePath());
            out.println("HTTP/1.1 200 OK");
            out.println("Content-Type: text/html");
            out.println();
            out.flush(); // Flush headers before sending content

            Files.copy(file.toPath(), toClient);
        }

        toClient.flush();
    }

    /**
     * Closes the servlet (no cleanup required in this implementation).
     */
    @Override
    public void close() {}
}
