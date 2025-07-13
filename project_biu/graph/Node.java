package project_biu.graph;

import java.util.*;

public class Node {
    private String name;
    private List<Node> edges;
    private Message msg;

    private boolean isTopic;         // ✅ Distinguish topic vs agent
    private String agentType;        // ✅ Store agent type (e.g. PlusAgent, MulAgent)

    public Node(String name) {
        this.name = name;
        this.edges = new ArrayList<>();
    }

    public void addEdge(Node node) {
        edges.add(node);
    }

    public boolean hasCycles() {
        return hasCycles(new HashSet<>(), new HashSet<>());
    }

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

    public String getName() {
        return name;
    }

    public List<Node> getEdges() {
        return edges;
    }

    public Message getMsg() {
        return msg;
    }

    public void setMsg(Message msg) {
        this.msg = msg;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEdges(List<Node> edges) {
        this.edges = edges;
    }

    public boolean isTopic() {
        return isTopic;
    }

    public void setIsTopic(boolean isTopic) {
        this.isTopic = isTopic;
    }

    public String getAgentType() {
        return agentType != null ? agentType : getShortName();
    }

    public void setAgentType(String agentType) {
        this.agentType = agentType;
    }

    public String getShortName() {
        if (name == null) return "(null)";
        return name.replaceFirst("T_", "").replaceFirst("A_", "");
    }

    public String getValueText() {
        if (!isTopic) return "";  // Only topics show value
        if (msg == null || msg.asText == null) return "0";
        return msg.asText;
    }
}
