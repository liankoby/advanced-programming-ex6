package project_biu.servlets;

import project_biu.graph.Topic;
import project_biu.graph.TopicManagerSingleton;
import project_biu.graph.Message;
import project_biu.graph.Graph;
import project_biu.views.HtmlGraphWriter;
import project_biu.server.RequestParser.RequestInfo;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.IOException;
import java.lang.reflect.Field;

/**
 * Servlet responsible for displaying and updating topic values.
 * <p>
 * Supports GET requests to the `/publish` endpoint:
 * - If query parameters `topic` and `value` are provided, the servlet attempts to publish the value to the topic.
 * - Displays a table with the latest values of all topics.
 * - Updates the visual graph representation via {@link HtmlGraphWriter}.
 * </p>
 */
public class TopicDisplayer implements Servlet {

    /**
     * Handles the HTTP GET request to display and optionally update topic values.
     *
     * @param ri        the request info containing parameters (e.g., topic, value)
     * @param toClient  the output stream to write the HTML response to
     * @throws IOException if writing to the stream fails
     */
    @Override
    public void handle(RequestInfo ri, OutputStream toClient) throws IOException {
        String topicName = ri.getParameters().get("topic");
        String value = ri.getParameters().get("value");

        PrintWriter out = new PrintWriter(toClient);
        out.println("HTTP/1.1 200 OK");
        out.println("Content-Type: text/html; charset=utf-8");
        out.println();

        boolean errorShown = false;

        // Handle publishing if parameters are present
        if (topicName != null && value != null) {
            Topic topic = TopicManagerSingleton.get().getTopic(topicName);
            if (topic == null) {
                // ❌ Show error if topic does not exist
                out.println("<p style='color:red;'>❌ Topic '" + topicName + "' does not exist.</p>");
                out.println("<p>Available topics:</p><ul>");
                for (Topic t : TopicManagerSingleton.get().getTopics()) {
                    out.println("<li>" + t.name + "</li>");
                }
                out.println("</ul>");
                errorShown = true;
            } else {
                // ✅ Publish value to topic
                topic.publish(new Message(value));

                // Update graph HTML
                Graph graph = new Graph();
                graph.createFromTopics();
                HtmlGraphWriter.writeGraphHtml(graph);
            }
        }

        // Show topic value table regardless
        if (!errorShown) {
            out.println("<p style='color:green;'>✅ Topic table:</p>");
        }

        out.println("<table border='1'>");
        out.println("<tr><th>Topic</th><th>Last Value</th></tr>");
        for (Topic t : TopicManagerSingleton.get().getTopics()) {
            String val = getLastMessageText(t);
            out.println("<tr><td>" + t.name + "</td><td>" + val + "</td></tr>");
        }
        out.println("</table>");
        out.flush();
    }

    /**
     * Uses reflection to extract the last message value from a topic.
     *
     * @param topic the topic to inspect
     * @return the string value of the last message, or "0" if unavailable
     */
    private String getLastMessageText(Topic topic) {
        try {
            Field f = topic.getClass().getDeclaredField("lastMessage");
            f.setAccessible(true);
            Message msg = (Message) f.get(topic);
            return (msg != null && msg.asText != null) ? msg.asText : "0";
        } catch (Exception e) {
            return "0";
        }
    }

    /**
     * Closes the servlet (no resources to release in this implementation).
     */
    @Override
    public void close() {}
}
