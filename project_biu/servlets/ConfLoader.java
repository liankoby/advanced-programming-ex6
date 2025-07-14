package project_biu.servlets;

import project_biu.configs.GenericConfig;
import project_biu.graph.Graph;
import project_biu.graph.Message;
import project_biu.graph.Topic;
import project_biu.graph.TopicManagerSingleton;
import project_biu.server.RequestParser.RequestInfo;
import project_biu.views.HtmlGraphWriter;

import java.io.*;
import java.nio.charset.StandardCharsets;

/**
 * Servlet responsible for handling configuration file uploads via multipart/form-data POST requests.
 * <p>
 * Parses the uploaded config file, resets the topic system, builds a new graph using the config,
 * initializes all topics with value "0", and renders a new graph HTML visualization.
 * </p>
 */
public class ConfLoader implements Servlet {

    /**
     * Handles the POST request for uploading a configuration file.
     * Extracts the file content, parses it, and builds the computational graph.
     *
     * @param ri        the parsed HTTP request info, including content and headers
     * @param toClient  the output stream to write the HTTP response
     * @throws IOException if I/O operations fail
     */
    @Override
    public void handle(RequestInfo ri, OutputStream toClient) throws IOException {
        byte[] body = ri.getContent();
        String bodyStr = new String(body, StandardCharsets.UTF_8);

        String contentType = ri.getHeaders().get("Content-Type");
        if (contentType == null || !contentType.contains("boundary=")) {
            respondError(toClient, "Missing multipart boundary");
            return;
        }

        String boundary = "--" + contentType.split("boundary=")[1];
        String[] parts = bodyStr.split(boundary);

        String fileContent = null;
        for (String part : parts) {
            if (part.contains("name=\"file\"")) {
                int index = part.indexOf("\r\n\r\n");
                if (index != -1) {
                    fileContent = part.substring(index + 4).trim();
                }
            }
        }

        if (fileContent == null || fileContent.isEmpty()) {
            respondError(toClient, "❌ No file content found in upload.");
            return;
        }

        File outDir = new File("src/config_files");
        outDir.mkdirs();
        File outFile = new File(outDir, "uploaded.conf");
        try (FileWriter fw = new FileWriter(outFile)) {
            fw.write(fileContent);
        }

        TopicManagerSingleton.reset();

        GenericConfig config = new GenericConfig();
        config.setConfFile(outFile.getPath());
        config.create();

        for (Topic t : TopicManagerSingleton.get().getTopics()) {
            t.publish(new Message("0"));
        }

        Graph graph = new Graph();
        graph.createFromTopics();
        HtmlGraphWriter.writeGraphHtml(graph);  // ✅ overwrites graph.html

        // ✅ Redirect center iframe to graph.html
        PrintWriter out = new PrintWriter(toClient);
        out.println("HTTP/1.1 303 See Other");
        out.println("Location: /app/graph.html");
        out.println();
        out.flush();
    }

    /**
     * Sends an HTTP 400 error message to the client with plain text explanation.
     *
     * @param toClient the output stream to write the response
     * @param msg      the error message to send
     * @throws IOException if writing fails
     */
    private void respondError(OutputStream toClient, String msg) throws IOException {
        PrintWriter out = new PrintWriter(toClient);
        out.println("HTTP/1.1 400 Bad Request");
        out.println("Content-Type: text/plain");
        out.println();
        out.println(msg);
        out.flush();
    }

    /**
     * Closes the servlet (no cleanup necessary).
     */
    @Override public void close() {}
}
