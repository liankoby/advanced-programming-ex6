package project_biu.servlets;

import project_biu.server.RequestParser.RequestInfo;

import java.io.*;
import java.nio.file.Files;

public class HtmlLoader implements Servlet {
    private final String basePath;

    public HtmlLoader(String basePath) {
        this.basePath = basePath;
        System.out.println("HtmlLoader initialized with basePath: " + basePath);
    }

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
            out.flush(); // Flush headers before file content

            Files.copy(file.toPath(), toClient);
        }

        toClient.flush();
    }

    @Override
    public void close() {}
}
