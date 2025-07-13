package project_biu.graph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Graph extends ArrayList<Node> {

    public boolean hasCycles() {
        for (Node n : this) {
            if (n.hasCycles()) {
                return true;
            }
        }
        return false;
    }

    // Legacy (singleton-based)
    public void createFromTopics() {
        createFromTopics(TopicManagerSingleton.get().getTopics());
    }

    public List<Node> getAllNodes() {
        return this;
    }

    // New: accepts any set of topics (for multi-client use)
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
