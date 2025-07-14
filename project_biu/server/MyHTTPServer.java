package project_biu.server;

import project_biu.servlets.Servlet;
import project_biu.server.RequestParser.RequestInfo;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

/**
 * A basic multiclient HTTP server implementation for serving servlets.
 * <p>
 * Supports mapping servlets to HTTP methods and paths. Handles GET/POST requests and delegates to matching servlet.
 * Designed to be used in the Advanced Programming exercise as the backend server.
 */
public class MyHTTPServer implements HTTPServer {
    private final Map<String, Map<String, Servlet>> servlets = new HashMap<>();
    private boolean running = true;
    private int port;
    private int maxClients;
    private ServerSocket serverSocket;

    /**
     * Constructs the server on a specific port with a maximum number of clients.
     *
     * @param port the port to bind the server socket
     * @param maxClients maximum number of client connections (not used for limiting in this version)
     */
    public MyHTTPServer(int port, int maxClients) {
        this.port = port;
        this.maxClients = maxClients;
    }

    /**
     * Starts the HTTP server loop.
     * Accepts connections and dispatches requests to the appropriate registered servlet.
     */
    @Override
    public void start() {
        try (ServerSocket server = new ServerSocket(port)) {
            this.serverSocket = server;
            System.out.println("✅ Server started on http://localhost:" + port);
            while (running) {
                try {
                    Socket client = server.accept();
                    handleClient(client);
                } catch (IOException e) {
                    if (running) {
                        System.err.println("Error handling client: " + e.getMessage());
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Failed to start server: " + e.getMessage());
        }
    }

    /**
     * Handles a single client request by parsing it and delegating to the appropriate servlet.
     *
     * @param client the accepted client socket
     */
    private void handleClient(Socket client) {
        try (
                BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
                OutputStream out = client.getOutputStream()
        ) {
            RequestInfo request = RequestParser.parseRequest(in);
            String method = request.getHttpCommand();
            String uri = request.getUri();

            Map<String, Servlet> methodMap = servlets.get(method);
            Servlet servlet = null;

            if (methodMap != null) {
                // Try exact match first
                servlet = methodMap.get(uri);

                // Fallback to longest matching prefix
                if (servlet == null) {
                    for (String prefix : methodMap.keySet()) {
                        if (uri.startsWith(prefix)) {
                            servlet = methodMap.get(prefix);
                            break;
                        }
                    }
                }
            }

            if (servlet != null) {
                servlet.handle(request, out);
            } else {
                out.write(("HTTP/1.1 404 Not Found\r\n\r\nUnknown path: " + uri).getBytes());
            }

        } catch (Exception e) {
            System.err.println("Client handling failed: " + e.getMessage());
        }
    }

    /**
     * Gracefully shuts down the server and closes the server socket.
     */
    @Override
    public void close() {
        this.running = false;
        try {
            if (serverSocket != null) serverSocket.close();
        } catch (IOException e) {
            System.err.println("Error closing server socket: " + e.getMessage());
        }
    }

    /**
     * Registers a servlet for a specific HTTP method and URI prefix.
     *
     * @param method  the HTTP method (e.g., "GET", "POST")
     * @param uri     the URI or URI prefix to match
     * @param servlet the servlet to handle matching requests
     */
    @Override
    public void addServlet(String method, String uri, Servlet servlet) {
        servlets.putIfAbsent(method, new HashMap<>());
        servlets.get(method).put(uri, servlet);
    }
}
