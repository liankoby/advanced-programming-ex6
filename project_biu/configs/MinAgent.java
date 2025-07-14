package project_biu.configs;

import project_biu.graph.Agent;
import project_biu.graph.Message;
import project_biu.graph.Topic;
import project_biu.graph.TopicManagerSingleton;

import java.util.HashMap;
import java.util.Map;

/**
 * MinAgent is an agent that subscribes to multiple input topics,
 * maintains the latest values from each, and publishes the minimum
 * value among all of them to a single output topic.
 * <p>
 * Whenever a new message is received, the minimum is recomputed and published.
 */
public class MinAgent implements Agent {
    private final String[] subs;
    private final String[] pubs;
    private final Map<String, Double> values = new HashMap<>();

    /**
     * Constructs a new MinAgent with input and output topics.
     *
     * @param subs an array of topic names to subscribe to (inputs)
     * @param pubs an array containing exactly one topic name for the output
     * @throws IllegalArgumentException if {@code pubs} does not contain exactly one topic
     */
    public MinAgent(String[] subs, String[] pubs) {
        this.subs = subs;
        this.pubs = pubs;

        if (pubs.length != 1)
            throw new IllegalArgumentException("MinAgent must have exactly 1 output");

        TopicManagerSingleton.TopicManager tm = TopicManagerSingleton.get();

        for (String s : subs) {
            if (!tm.hasTopic(s)) {
                tm.addTopic(new Topic(s));
                System.out.println("➕ Created missing topic: " + s);
            }
            tm.getTopic(s).subscribe(this);
            values.put(s, Double.MAX_VALUE);
        }

        if (!tm.hasTopic(pubs[0])) {
            tm.addTopic(new Topic(pubs[0]));
            System.out.println("➕ Created missing output topic: " + pubs[0]);
        }
        tm.getTopic(pubs[0]).addPublisher(this);

        System.out.println("🔧 MinAgent subscribed to " + String.join(",", subs) + " → " + pubs[0]);
    }

    /**
     * Handles incoming messages from subscribed topics, updates the stored value,
     * and publishes the minimum of all values to the output topic.
     *
     * @param topic the topic name that sent the message
     * @param msg   the message containing a double value
     */
    @Override
    public void callback(String topic, Message msg) {
        values.put(topic, msg.asDouble);
        double min = values.values().stream().mapToDouble(Double::doubleValue).min().orElse(Double.MAX_VALUE);

        System.out.println("📥 MinAgent updated " + topic + " = " + msg.asDouble + ", min = " + min);
        TopicManagerSingleton.get().getTopic(pubs[0]).publish(new Message(min));
    }

    /**
     * Returns the agent's name (used as an identifier).
     *
     * @return the string "min"
     */
    @Override public String getName() { return "min"; }

    /**
     * Resets all stored values to {@code Double.MAX_VALUE}.
     */
    @Override public void reset() { values.replaceAll((k, v) -> Double.MAX_VALUE); }

    /**
     * Closes the agent. No resources to release in this implementation.
     */
    @Override public void close() {}
}
