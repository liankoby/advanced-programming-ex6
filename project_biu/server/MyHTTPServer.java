package project_biu.server;

import project_biu.servlets.Servlet;
import project_biu.server.RequestParser.RequestInfo;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class MyHTTPServer implements HTTPServer {
    private final Map<String, Map<String, Servlet>> servlets = new HashMap<>();
    private boolean running = true;
    private int port;
    private int maxClients;
    private ServerSocket serverSocket;

    public MyHTTPServer(int port, int maxClients) {
        this.port = port;
        this.maxClients = maxClients;
    }

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

    @Override
    public void close() {
        this.running = false;
        try {
            if (serverSocket != null) serverSocket.close();
        } catch (IOException e) {
            System.err.println("Error closing server socket: " + e.getMessage());
        }
    }

    @Override
    public void addServlet(String method, String uri, Servlet servlet) {
        servlets.putIfAbsent(method, new HashMap<>());
        servlets.get(method).put(uri, servlet);
    }
}
