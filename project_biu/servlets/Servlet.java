package project_biu.servlets;

import project_biu.server.RequestParser.RequestInfo;
import java.io.IOException;
import java.io.OutputStream;

/**
 * A generic interface for handling HTTP requests in the custom server.
 *
 * Each implementing class represents a servlet that can respond to a specific
 * path and HTTP method (GET or POST). Used in conjunction with {@code HTTPServer}.
 */
public interface Servlet {

    /**
     * Handles the HTTP request by processing the request info and writing a response to the client.
     *
     * @param ri the parsed request info, including URI, headers, and parameters
     * @param toClient the output stream for writing the HTTP response
     * @throws IOException if an I/O error occurs while handling the request
     */
    void handle(RequestInfo ri, OutputStream toClient) throws IOException;

    /**
     * Releases any resources held by the servlet (optional).
     *
     * @throws IOException if an error occurs during cleanup
     */
    void close() throws IOException;
}
