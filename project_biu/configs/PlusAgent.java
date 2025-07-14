package project_biu.configs;

import project_biu.graph.Agent;
import project_biu.graph.Message;
import project_biu.graph.Topic;
import project_biu.graph.TopicManagerSingleton;

import java.util.HashMap;
import java.util.Map;

/**
 * A configurable agent that listens to multiple input topics,
 * sums their values, and publishes the result to a single output topic.
 * <p>
 * The agent maintains the last value received from each subscribed topic,
 * and every time a new value is received, it recomputes the total sum and
 * publishes it to the output.
 */
public class PlusAgent implements Agent {
    private final String[] subs;
    private final String[] pubs;
    private final Map<String, Double> values = new HashMap<>();

    /**
     * Constructs a PlusAgent with the given input and output topic names.
     *
     * @param subs the input topic names (to be summed)
     * @param pubs the output topic name (must be exactly one)
     * @throws IllegalArgumentException if {@code pubs} does not contain exactly one topic
     */
    public PlusAgent(String[] subs, String[] pubs) {
        this.subs = subs;
        this.pubs = pubs;

        if (pubs.length != 1)
            throw new IllegalArgumentException("PlusAgent must have exactly 1 output");

        TopicManagerSingleton.TopicManager tm = TopicManagerSingleton.get();

        for (String s : subs) {
            if (!tm.hasTopic(s)) {
                tm.addTopic(new Topic(s));
                System.out.println("➕ Created missing topic: " + s);
            }
            tm.getTopic(s).subscribe(this);
            values.put(s, 0.0);
        }

        if (!tm.hasTopic(pubs[0])) {
            tm.addTopic(new Topic(pubs[0]));
            System.out.println("➕ Created missing output topic: " + pubs[0]);
        }
        tm.getTopic(pubs[0]).addPublisher(this);

        System.out.println("🔧 PlusAgent subscribed to " + String.join(",", subs) + " → " + pubs[0]);
    }

    /**
     * Receives an update from a subscribed topic and recomputes the total sum.
     * Publishes the new sum to the output topic.
     *
     * @param topic the topic name that published the message
     * @param msg   the message received from the topic
     */
    @Override
    public void callback(String topic, Message msg) {
        values.put(topic, msg.asDouble);
        double sum = values.values().stream().mapToDouble(Double::doubleValue).sum();

        System.out.println("📥 PlusAgent updated " + topic + " = " + msg.asDouble + ", total sum = " + sum);
        TopicManagerSingleton.get().getTopic(pubs[0]).publish(new Message(sum));
    }

    /**
     * Returns the name of the agent.
     *
     * @return the string "plus"
     */
    @Override public String getName() { return "plus"; }

    /**
     * Resets all stored input values to 0.0.
     */
    @Override public void reset() { values.replaceAll((k, v) -> 0.0); }

    /**
     * Closes the agent. No resources to clean up in this implementation.
     */
    @Override public void close() {}
}
