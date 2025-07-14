package project_biu.views;

import project_biu.graph.Graph;
import project_biu.graph.Node;
import project_biu.graph.Message;
import project_biu.graph.Topic;
import project_biu.graph.TopicManagerSingleton;

import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * Utility class that generates an HTML visualization of the current graph.
 * <p>
 * The output HTML is saved to the file: {@code src/html_files/graph.html}.
 * It uses a template HTML file (with placeholders for nodes and edges) and
 * dynamically injects topic and agent nodes along with SVG lines representing edges.
 * </p>
 */
public class HtmlGraphWriter {

    /**
     * Generates an HTML representation of the given graph and writes it to graph.html.
     * <p>
     * This method reads a predefined template, computes node positions in a circular layout,
     * renders topic values, draws edges between nodes, and injects the results into the template.
     * </p>
     *
     * @param g the {@link Graph} object to render as HTML
     */
    public static void writeGraphHtml(Graph g) {
        try {
            List<String> templateLines = Files.readAllLines(
                    Paths.get("src/html_files/graph_template.html"), StandardCharsets.UTF_8);

            StringBuilder htmlBuilder = new StringBuilder();
            for (String line : templateLines) {
                htmlBuilder.append(line).append("\n");
            }

            String html = htmlBuilder.toString();
            StringBuilder nodesBuilder = new StringBuilder();
            StringBuilder edgesBuilder = new StringBuilder();

            Map<String, int[]> nodePositions = new HashMap<>();
            List<Node> nodes = new ArrayList<>(g);

            int radius = 300;
            int centerX = 500;
            int centerY = 400;
            int total = nodes.size();
            int index = 0;

            // Position and draw nodes
            for (Node node : nodes) {
                double angle = 2 * Math.PI * index / total;
                int x = (int) (centerX + radius * Math.cos(angle));
                int y = (int) (centerY + radius * Math.sin(angle));

                String name = node.getName();
                boolean isTopic = name.startsWith("T");
                String displayName = isTopic ? name.substring(1) : name;
                String label = "0";

                if (isTopic) {
                    try {
                        Topic t = TopicManagerSingleton.get().getTopic(displayName);
                        java.lang.reflect.Field f = t.getClass().getDeclaredField("lastMessage");
                        f.setAccessible(true);
                        Message msg = (Message) f.get(t);
                        if (msg != null && msg.data != null) {
                            label = new String(msg.data);
                        }
                    } catch (Exception e) {
                        label = "0";
                    }
                }

                String nodeHtml = String.format(
                        "<div class='node %s' style='left:%dpx; top:%dpx;'>%s</div>",
                        isTopic ? "topic" : "agent", x, y, displayName);
                nodesBuilder.append(nodeHtml).append("\n");

                if (isTopic) {
                    nodesBuilder.append(String.format(
                            "<div class='label' style='left:%dpx; top:%dpx;'>%s</div>",
                            x, y + 35, label)).append("\n");
                }

                nodePositions.put(name, new int[]{x + 50, y + 20});
                index++;
            }

            // Draw edges between nodes
            for (Node from : g) {
                for (Node to : from.getEdges()) {
                    int[] src = nodePositions.get(from.getName());
                    int[] tgt = nodePositions.get(to.getName());
                    if (src != null && tgt != null) {
                        double dx = tgt[0] - src[0], dy = tgt[1] - src[1];
                        double dist = Math.sqrt(dx * dx + dy * dy);
                        int offset = 35;
                        int sx = (int) (src[0] + offset * dx / dist);
                        int sy = (int) (src[1] + offset * dy / dist);
                        int tx = (int) (tgt[0] - offset * dx / dist);
                        int ty = (int) (tgt[1] - offset * dy / dist);

                        String edge = String.format(
                                "<line x1='%d' y1='%d' x2='%d' y2='%d' stroke='#f06292' stroke-width='2' marker-end='url(#arrow)'/>",
                                sx, sy, tx, ty);
                        edgesBuilder.append(edge).append("\n");
                    }
                }
            }

            html = html.replace("<!-- NODES_PLACEHOLDER -->", nodesBuilder.toString());
            html = html.replace("<!-- EDGES_PLACEHOLDER -->", edgesBuilder.toString());

            try (FileWriter fw = new FileWriter("src/html_files/graph.html")) {
                fw.write(html);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
