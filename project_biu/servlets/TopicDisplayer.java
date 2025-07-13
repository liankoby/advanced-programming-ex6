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

public class TopicDisplayer implements Servlet {

    @Override
    public void handle(RequestInfo ri, OutputStream toClient) throws IOException {
        String topicName = ri.getParameters().get("topic");
        String value = ri.getParameters().get("value");

        PrintWriter out = new PrintWriter(toClient);
        out.println("HTTP/1.1 200 OK");
        out.println("Content-Type: text/html; charset=utf-8");
        out.println();

        boolean errorShown = false;

        if (topicName != null && value != null) {
            Topic topic = TopicManagerSingleton.get().getTopic(topicName);
            if (topic == null) {
                // ❌ Error message shown ABOVE the table
                out.println("<p style='color:red;'>❌ Topic '" + topicName + "' does not exist.</p>");
                out.println("<p>Available topics:</p><ul>");
                for (Topic t : TopicManagerSingleton.get().getTopics()) {
                    out.println("<li>" + t.name + "</li>");
                }
                out.println("</ul>");
                errorShown = true;
            } else {
                // ✅ Publish value
                topic.publish(new Message(value));

                // Update graph.html
                Graph graph = new Graph();
                graph.createFromTopics();
                HtmlGraphWriter.writeGraphHtml(graph);
            }
        }

        // ✅ Always show the topic table
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

    @Override
    public void close() {}
}
