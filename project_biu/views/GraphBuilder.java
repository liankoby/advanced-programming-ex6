package project_biu.views;

import project_biu.graph.Graph;
import project_biu.graph.Topic;
import project_biu.graph.TopicManagerSingleton;

/**
 * Utility class for constructing a {@link Graph} object from the currently registered topics.
 * <p>
 * Used to regenerate the full graph structure whenever configuration is reloaded or modified.
 * </p>
 */
public class GraphBuilder {

    /**
     * Builds and returns a {@link Graph} using all topics registered in {@link TopicManagerSingleton}.
     *
     * @return a {@link Graph} instance representing the current topic-agent structure
     */
    public static Graph buildGraph() {
        Graph g = new Graph();
        g.createFromTopics(TopicManagerSingleton.get().getTopics());
        return g;
    }
}
