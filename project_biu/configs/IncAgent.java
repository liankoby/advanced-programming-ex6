package project_biu.configs;

import project_biu.graph.Agent;
import project_biu.graph.Message;
import project_biu.graph.Topic;
import project_biu.graph.TopicManagerSingleton;

public class IncAgent implements Agent {
    private final String[] subs;
    private final String[] pubs;

    public IncAgent(String[] subs, String[] pubs) {
        this.subs = subs;
        this.pubs = pubs;

        TopicManagerSingleton.TopicManager tm = TopicManagerSingleton.get();

        // Ensure input topic exists
        if (!tm.hasTopic(subs[0])) {
            tm.addTopic(new Topic(subs[0]));
            System.out.println("➕ Created missing topic: " + subs[0]);
        }
        tm.getTopic(subs[0]).subscribe(this);

        // Ensure output topic exists
        if (!tm.hasTopic(pubs[0])) {
            tm.addTopic(new Topic(pubs[0]));
            System.out.println("➕ Created missing output topic: " + pubs[0]);
        }
        tm.getTopic(pubs[0]).addPublisher(this);

        System.out.println("🔧 IncAgent subscribed to " + subs[0] + " → " + pubs[0]);
    }

    @Override
    public void callback(String topic, Message msg) {
        double value = msg.asDouble + 1;
        System.out.println("📥 IncAgent incremented " + topic + " = " + msg.asDouble + " → " + value);
        TopicManagerSingleton.get().getTopic(pubs[0]).publish(new Message(value));
    }

    @Override public String getName() { return "inc"; }
    @Override public void reset() {}
    @Override public void close() {}
}
