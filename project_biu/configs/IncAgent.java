package project_biu.configs;

import project_biu.graph.Agent;
import project_biu.graph.Message;
import project_biu.graph.Topic;
import project_biu.graph.TopicManagerSingleton;

/**
 * IncAgent is a simple agent that subscribes to a single input topic,
 * increments each incoming value by 1, and publishes the result to a single output topic.
 * <p>
 * Example: if topic A sends 7.0, this agent publishes 8.0 to topic B.
 */
public class IncAgent implements Agent {
    private final String[] subs;
    private final String[] pubs;

    /**
     * Constructs a new IncAgent that listens to one input topic and publishes to one output topic.
     *
     * @param subs an array with one input topic name
     * @param pubs an array with one output topic name
     */
    public IncAgent(String[] subs, String[] pubs) {
        this.subs = subs;
        this.pubs = pubs;

        TopicManagerSingleton.TopicManager tm = TopicManagerSingleton.get();

        // Ensure input topic exists and subscribe to it
        if (!tm.hasTopic(subs[0])) {
            tm.addTopic(new Topic(subs[0]));
            System.out.println("➕ Created missing topic: " + subs[0]);
        }
        tm.getTopic(subs[0]).subscribe(this);

        // Ensure output topic exists and register as publisher
        if (!tm.hasTopic(pubs[0])) {
            tm.addTopic(new Topic(pubs[0]));
            System.out.println("➕ Created missing output topic: " + pubs[0]);
        }
        tm.getTopic(pubs[0]).addPublisher(this);

        System.out.println("🔧 IncAgent subscribed to " + subs[0] + " → " + pubs[0]);
    }

    /**
     * Called when a message arrives on the subscribed topic.
     * Increments the numeric value and publishes the result.
     *
     * @param topic the input topic name
     * @param msg   the message to process
     */
    @Override
    public void callback(String topic, Message msg) {
        double value = msg.asDouble + 1;
        System.out.println("📥 IncAgent incremented " + topic + " = " + msg.asDouble + " → " + value);
        TopicManagerSingleton.get().getTopic(pubs[0]).publish(new Message(value));
    }

    /**
     * Returns the name of the agent ("inc").
     */
    @Override public String getName() { return "inc"; }

    /**
     * No internal state to reset.
     */
    @Override public void reset() {}

    /**
     * No resources to release.
     */
    @Override public void close() {}
}
