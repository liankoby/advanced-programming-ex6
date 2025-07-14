package project_biu.server;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * Utility class for parsing HTTP requests from an input stream.
 * <p>
 * Extracts the method, URI, query parameters, headers, and body content into a structured {@link RequestInfo} object.
 * Supports both GET and POST requests, including application/x-www-form-urlencoded form decoding.
 */
public class RequestParser {

    /**
     * Represents a parsed HTTP request.
     * <p>
     * Contains method, URI, URI segments, query/form parameters, headers, and body content.
     */
    public static class RequestInfo {
        private final String httpCommand;
        private final String uri;
        private final String[] uriSegments;
        private final Map<String, String> parameters;
        private final Map<String, String> headers;
        private final byte[] content;

        /**
         * Constructs a new RequestInfo object.
         *
         * @param httpCommand HTTP method (e.g., GET or POST)
         * @param uri raw request URI (possibly including query string)
         * @param uriSegments URI path segments split by "/"
         * @param parameters parsed parameters from query string and form body
         * @param headers HTTP headers
         * @param content raw request body content as byte array
         */
        public RequestInfo(String httpCommand, String uri, String[] uriSegments,
                           Map<String, String> parameters,
                           Map<String, String> headers,
                           byte[] content) {
            this.httpCommand = httpCommand;
            this.uri = uri;
            this.uriSegments = uriSegments;
            this.parameters = parameters;
            this.headers = headers;
            this.content = content;
        }

        /** @return HTTP method (e.g., GET, POST) */
        public String getHttpCommand() { return httpCommand; }

        /** @return raw URI from the request */
        public String getUri() { return uri; }

        /** @return URI path segments (split by "/") */
        public String[] getUriSegments() { return uriSegments; }

        /** @return map of query parameters and form data */
        public Map<String, String> getParameters() { return parameters; }

        /** @return map of HTTP headers */
        public Map<String, String> getHeaders() { return headers; }

        /** @return raw body content as byte array */
        public byte[] getContent() { return content; }
    }

    /**
     * Parses a raw HTTP request from the input stream.
     *
     * @param in the input reader connected to the socket
     * @return a {@link RequestInfo} object representing the parsed request
     * @throws IOException if the request is malformed or reading fails
     */
    public static RequestInfo parseRequest(BufferedReader in) throws java.io.IOException {
        String requestLine = in.readLine();
        if (requestLine == null) throw new java.io.IOException("Empty request");

        StringTokenizer tokenizer = new StringTokenizer(requestLine);
        String method = tokenizer.nextToken();
        String fullUri = tokenizer.nextToken();
        String uri = fullUri.split("\\?")[0];

        Map<String, String> params = new HashMap<>();
        if (fullUri.contains("?")) {
            String[] pairs = fullUri.split("\\?")[1].split("&");
            for (String pair : pairs) {
                String[] kv = pair.split("=", 2);
                if (kv.length == 2) {
                    params.put(URLDecoder.decode(kv[0], StandardCharsets.UTF_8),
                            URLDecoder.decode(kv[1], StandardCharsets.UTF_8));
                }
            }
        }

        // Read headers
        Map<String, String> headers = new HashMap<>();
        int contentLength = 0;
        String line;
        while ((line = in.readLine()) != null && !line.isEmpty()) {
            int colonIndex = line.indexOf(":");
            if (colonIndex > 0) {
                String key = line.substring(0, colonIndex).trim();
                String value = line.substring(colonIndex + 1).trim();
                headers.put(key, value);
                if (key.equalsIgnoreCase("Content-Length")) {
                    contentLength = Integer.parseInt(value);
                }
            }
        }

        // Read body
        char[] bodyChars = new char[contentLength];
        int read = in.read(bodyChars);
        String body = (read > 0) ? new String(bodyChars, 0, read) : "";
        byte[] content = body.getBytes(StandardCharsets.UTF_8);

        // ✅ Handle x-www-form-urlencoded POST body
        String contentType = headers.getOrDefault("Content-Type", "");
        if ("POST".equalsIgnoreCase(method) && contentType.startsWith("application/x-www-form-urlencoded")) {
            String[] pairs = body.split("&");
            for (String pair : pairs) {
                String[] kv = pair.split("=", 2);
                if (kv.length == 2) {
                    params.put(URLDecoder.decode(kv[0], StandardCharsets.UTF_8),
                            URLDecoder.decode(kv[1], StandardCharsets.UTF_8));
                }
            }
        }

        String[] uriSegments = uri.length() > 1 ? uri.substring(1).split("/") : new String[0];

        return new RequestInfo(method, uri, uriSegments, params, headers, content);
    }
}
