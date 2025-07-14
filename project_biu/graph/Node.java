package project_biu.graph;

import java.util.*;

/**
 * Represents a node in the computation graph.
 * A node can be either a Topic or an Agent and holds its name, outgoing edges, and an optional message.
 * <p>
 * Used for both visualization and cycle detection.
 */
public class Node {
    private String name;
    private List<Node> edges;
    private Message msg;

    private boolean isTopic;         // Distinguish topic vs agent
    private String agentType;        // Store agent type (e.g. PlusAgent, MulAgent)

    /**
     * Constructs a new Node with the given name.
     *
     * @param name the name of the node (e.g. topic name or agent name)
     */
    public Node(String name) {
        this.name = name;
        this.edges = new ArrayList<>();
    }

    /**
     * Adds a directed edge from this node to another node.
     *
     * @param node the target node
     */
    public void addEdge(Node node) {
        edges.add(node);
    }

    /**
     * Checks if this node is part of a cycle in the graph.
     *
     * @return true if a cycle is detected
     */
    public boolean hasCycles() {
        return hasCycles(new HashSet<>(), new HashSet<>());
    }

    // Internal DFS helper for cycle detection
    private boolean hasCycles(Set<Node> visited, Set<Node> stack) {
        if (stack.contains(this)) return true;
        if (visited.contains(this)) return false;

        visited.add(this);
        stack.add(this);
        for (Node neighbor : edges) {
            if (neighbor.hasCycles(visited, stack)) return true;
        }
        stack.remove(this);
        return false;
    }

    /** @return the name of the node */
    public String getName() {
        return name;
    }

    /** @return the list of outgoing edges */
    public List<Node> getEdges() {
        return edges;
    }

    /** @return the message currently stored in the node (used for topics) */
    public Message getMsg() {
        return msg;
    }

    /** Sets the message stored in this node (used for topics). */
    public void setMsg(Message msg) {
        this.msg = msg;
    }

    /** Renames this node. */
    public void setName(String name) {
        this.name = name;
    }

    /** Replaces the list of outgoing edges. */
    public void setEdges(List<Node> edges) {
        this.edges = edges;
    }

    /** @return true if this node represents a topic */
    public boolean isTopic() {
        return isTopic;
    }

    /** Sets whether this node represents a topic or not. */
    public void setIsTopic(boolean isTopic) {
        this.isTopic = isTopic;
    }

    /**
     * Returns the type of agent associated with this node, if known.
     * Defaults to short name if agentType is null.
     *
     * @return the agent type or fallback name
     */
    public String getAgentType() {
        return agentType != null ? agentType : getShortName();
    }

    /** Sets the agent type for this node. */
    public void setAgentType(String agentType) {
        this.agentType = agentType;
    }

    /**
     * Strips "T_" or "A_" prefixes from the node name for cleaner display.
     *
     * @return the cleaned short name
     */
    public String getShortName() {
        if (name == null) return "(null)";
        return name.replaceFirst("T_", "").replaceFirst("A_", "");
    }

    /**
     * Returns the textual representation of the node's value (used only for topics).
     *
     * @return the topic value as string or "0" if unavailable
     */
    public String getValueText() {
        if (!isTopic) return "";
        if (msg == null || msg.asText == null) return "0";
        return msg.asText;
    }
}
