package project_biu.server;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class RequestParser {

    public static class RequestInfo {
        private final String httpCommand;
        private final String uri;
        private final String[] uriSegments;
        private final Map<String, String> parameters;
        private final Map<String, String> headers;
        private final byte[] content;

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

        public String getHttpCommand() { return httpCommand; }
        public String getUri() { return uri; }
        public String[] getUriSegments() { return uriSegments; }
        public Map<String, String> getParameters() { return parameters; }
        public Map<String, String> getHeaders() { return headers; }
        public byte[] getContent() { return content; }
    }

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
