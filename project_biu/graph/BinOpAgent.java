package project_biu.graph;

import project_biu.graph.Agent;
import project_biu.graph.Message;
import project_biu.graph.TopicManagerSingleton;

import java.util.function.BinaryOperator;

/**
 * Represents a binary operation agent that listens to two input topics and publishes
 * the result to an output topic after applying a specified binary operation.
 * <p>
 * Once both input values are received, the agent computes {@code result = op(v1, v2)}
 * and publishes it to the output topic.
 */
public class BinOpAgent implements Agent {

    private final String name;
    private final String topic1, topic2, outputTopic;
    private final BinaryOperator<Double> op;
    private Double v1 = null, v2 = null;

    /**
     * Constructs a new binary operation agent.
     *
     * @param name         the unique name of the agent
     * @param topic1       the name of the first input topic
     * @param topic2       the name of the second input topic
     * @param outputTopic  the name of the topic to publish the result to
     * @param op           the binary operation (e.g. sum, multiplication, minimum)
     */
    public BinOpAgent(String name, String topic1, String topic2, String outputTopic, BinaryOperator<Double> op) {
        this.name = name;
        this.topic1 = topic1;
        this.topic2 = topic2;
        this.outputTopic = outputTopic;
        this.op = op;

        TopicManagerSingleton.TopicManager tm = TopicManagerSingleton.get();
        tm.getTopic(topic1).subscribe(this);
        tm.getTopic(topic2).subscribe(this);
        tm.getTopic(outputTopic).addPublisher(this);
    }

    /**
     * Called when a subscribed topic publishes a new message.
     * <p>
     * The agent waits for both inputs to arrive, then applies the operation and
     * publishes the result. Inputs are reset after each computation.
     *
     * @param topic the topic name
     * @param msg   the message received
     */
    @Override
    public void callback(String topic, Message msg) {
        if (topic.equals(topic1)) {
            v1 = msg.asDouble;
        } else if (topic.equals(topic2)) {
            v2 = msg.asDouble;
        }

        if (v1 != null && v2 != null) {
            double result = op.apply(v1, v2);
            TopicManagerSingleton.get().getTopic(outputTopic).publish(new Message(result));
            v1 = null;
            v2 = null;
        }
    }

    /**
     * Resets the stored input values.
     */
    @Override
    public void reset() {
        v1 = v2 = null;
    }

    /**
     * Closes the agent (no-op for this implementation).
     */
    @Override
    public void close() {}

    /**
     * Returns the agent's name.
     *
     * @return the agent's unique name
     */
    @Override
    public String getName() {
        return name;
    }
}
