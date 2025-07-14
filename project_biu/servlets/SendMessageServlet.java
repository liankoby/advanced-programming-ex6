package project_biu.servlets;

import project_biu.server.RequestParser.RequestInfo;
import project_biu.graph.TopicManagerSingleton;
import project_biu.graph.Topic;
import project_biu.graph.Message;
import project_biu.graph.Graph;
import project_biu.views.HtmlGraphWriter;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.IOException;
import java.util.List;

/**
 * Servlet responsible for publishing messages to topics via HTTP GET requests.
 * <p>
 * This servlet is mapped to the <code>/publish</code> path and accepts two query parameters:
 * <ul>
 *   <li><b>topic</b>: the name of the topic</li>
 *   <li><b>message</b>: the value to be published</li>
 * </ul>
 * It validates the input, updates the topic, regenerates the graph HTML, and returns an updated page.
 */
public class SendMessageServlet implements Servlet {

    /**
     * Handles the GET request to publish a message to a topic.
     *
     * @param ri        parsed request containing parameters "topic" and "message"
     * @param toClient  output stream to write the HTML response
     * @throws IOException if an error occurs while handling the request
     */
    @Override
    public void handle(RequestInfo ri, OutputStream toClient) throws IOException {
        String topicName = ri.getParameters().get("topic");
        String msg = ri.getParameters().get("message");

        System.out.println("📨 Received POST: topic=" + topicName + ", message=" + msg);

        PrintWriter out = new PrintWriter(toClient);

        if (topicName == null || msg == null || topicName.isEmpty() || msg.isEmpty()) {
            sendStyledError(out, "Missing topic or message");
            return;
        }

        TopicManagerSingleton.TopicManager tm = TopicManagerSingleton.get();

        if (!tm.hasTopic(topicName)) {
            System.out.println("❌ Topic does not exist: " + topicName);
            sendStyledError(out, "Topic not found: <code>" + topicName + "</code>");
            return;
        }

        Topic topic = tm.getTopic(topicName);
        topic.publish(new Message(msg));
        System.out.println("✅ Published message to topic: " + topicName);

        Graph graph = new Graph();
        graph.createFromTopics();
        HtmlGraphWriter.writeGraphHtml(graph);

        out.println("HTTP/1.1 200 OK");
        out.println("Content-Type: text/html");
        out.println();
        HtmlGraphWriter.writeGraphHtml(graph);
        out.flush();
    }

    /**
     * Sends an HTML-styled error message to the client.
     * Includes JavaScript to return to the previous page after 2 seconds.
     *
     * @param out          the PrintWriter connected to the HTTP response
     * @param messageHtml  the error message (already HTML-formatted)
     */
    private void sendStyledError(PrintWriter out, String messageHtml) {
        out.println("HTTP/1.1 200 OK");
        out.println("Content-Type: text/html");
        out.println();
        out.println("<html><head><style>");
        out.println(".error { color: white; background: #d9534f; padding: 10px; border-radius: 6px; margin: 20px; font-weight: bold; text-align:center; }");
        out.println("</style></head><body>");
        out.println("<div class='error'>" + messageHtml + "</div>");
        out.println("<script>setTimeout(() => window.history.back(), 2000);</script>");
        out.println("</body></html>");
        out.flush();
    }

    /**
     * Releases any resources (none needed in this servlet).
     */
    @Override public void close() {}
}
