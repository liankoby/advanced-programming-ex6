package project_biu.graph;

import java.util.ArrayList;
import java.util.List;

/**
 * Singleton wrapper for managing all {@link Topic} instances in the system.
 * <p>
 * Provides global access to the {@link TopicManager} instance that tracks topics used in the pub/sub graph.
 */
public class TopicManagerSingleton {

    private static final TopicManager manager = new TopicManager();

    /**
     * Returns the shared TopicManager instance.
     *
     * @return the global {@link TopicManager}
     */
    public static TopicManager get() {
        return manager;
    }

    /**
     * Resets the topic manager by clearing all registered topics.
     */
    public static void reset() {
        manager.topics.clear();
        System.out.println("🔄 TopicManagerSingleton reset: cleared all topics.");
    }

    /**
     * Inner class representing the actual topic manager logic.
     * Manages the list of topics and provides utilities to query or register them.
     */
    public static class TopicManager {
        private List<Topic> topics = new ArrayList<>();

        /**
         * Returns all currently registered topics.
         *
         * @return list of topics
         */
        public List<Topic> getTopics() {
            return topics;
        }

        /**
         * Checks if a topic with the given name exists.
         *
         * @param name the topic name to check
         * @return true if a topic with the given name exists
         */
        public boolean hasTopic(String name) {
            return topics.stream().anyMatch(t -> t.name.equals(name));
        }

        /**
         * Returns the topic with the given name, or null if not found.
         *
         * @param name the name of the topic
         * @return the topic object, or null if not found
         */
        public Topic getTopic(String name) {
            return topics.stream().filter(t -> t.name.equals(name)).findFirst().orElse(null);
        }

        /**
         * Adds a new topic to the list of managed topics.
         *
         * @param t the topic to register
         */
        public void addTopic(Topic t) {
            topics.add(t);
        }
    }
}
