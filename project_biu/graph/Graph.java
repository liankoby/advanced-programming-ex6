package project_biu.graph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents a directed graph of {@link Node} objects corresponding to Topics and Agents.
 * <p>
 * This graph supports cycle detection, dynamic construction from the topic system,
 * and provides access to all participating nodes.
 */
public class Graph extends ArrayList<Node> {

    /**
     * Checks whether the graph contains any cycles.
     *
     * @return true if a cycle exists in the graph; false otherwise
     */
    public boolean hasCycles() {
        for (Node n : this) {
            if (n.hasCycles()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Builds the graph structure using all registered topics
     * via the {@link TopicManagerSingleton}.
     * <p>
     * This is a convenience method for backwards compatibility.
     */
    public void createFromTopics() {
        createFromTopics(TopicManagerSingleton.get().getTopics());
    }

    /**
     * Returns all nodes in the graph.
     *
     * @return list of nodes
     */
    public List<Node> getAllNodes() {
        return this;
    }

    /**
     * Constructs a directed graph based on the given topics and their agent connections.
     * <ul>
     *     <li>Each topic becomes a node prefixed with "T".</li>
     *     <li>Each agent becomes a node prefixed with "A".</li>
     *     <li>Edges are created:
     *         <ul>
     *             <li>From topic → agent (subscriptions)</li>
     *             <li>From agent → topic (publications)</li>
     *         </ul>
     *     </li>
     * </ul>
     *
     * @param topics the iterable list of topics to build the graph from
     */
    public void createFromTopics(Iterable<Topic> topics) {
        this.clear();
        Map<String, Node> nodes = new HashMap<>();

        for (Topic t : topics) {
            String topicNodeName = "T" + t.name;
            nodes.putIfAbsent(topicNodeName, new Node(topicNodeName));
        }

        for (Topic t : topics) {
            String topicNodeName = "T" + t.name;

            for (Agent a : t.subs) {
                String agentNodeName = "A" + a.getName();
                nodes.putIfAbsent(agentNodeName, new Node(agentNodeName));
                nodes.get(topicNodeName).addEdge(nodes.get(agentNodeName));
            }

            for (Agent a : t.pubs) {
                String agentNodeName = "A" + a.getName();
                nodes.putIfAbsent(agentNodeName, new Node(agentNodeName));
                nodes.get(agentNodeName).addEdge(nodes.get(topicNodeName));
            }
        }

        this.addAll(nodes.values());
    }
}
