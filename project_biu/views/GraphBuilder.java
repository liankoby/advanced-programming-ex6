package project_biu.views;

import project_biu.graph.Graph;
import project_biu.graph.Topic;
import project_biu.graph.TopicManagerSingleton;

public class GraphBuilder {
    public static Graph buildGraph() {
        Graph g = new Graph();
        g.createFromTopics(TopicManagerSingleton.get().getTopics());
        return g;
    }
}
