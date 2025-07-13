package project_biu.graph;

import java.util.ArrayList;
import java.util.List;

public class TopicManagerSingleton {
    private static final TopicManager manager = new TopicManager();

    public static TopicManager get() {
        return manager;
    }

    public static void reset() {
        manager.topics.clear();
        System.out.println("🔄 TopicManagerSingleton reset: cleared all topics.");
    }

    public static class TopicManager {
        private List<Topic> topics = new ArrayList<>();

        public List<Topic> getTopics() {
            return topics;
        }

        public boolean hasTopic(String name) {
            return topics.stream().anyMatch(t -> t.name.equals(name));
        }

        public Topic getTopic(String name) {
            return topics.stream().filter(t -> t.name.equals(name)).findFirst().orElse(null);
        }

        public void addTopic(Topic t) {
            topics.add(t);
        }
    }
}
