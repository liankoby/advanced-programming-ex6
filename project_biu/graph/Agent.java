package project_biu.graph;

/**
 * Represents a reactive agent in a publisher/subscriber graph system.
 * <p>
 * Agents subscribe to topics, receive messages via {@code callback}, and may also publish
 * messages to other topics.
 * <p>
 * Implementations typically represent logic units that react to input data and propagate results.
 */
public interface Agent {

    /**
     * Returns the unique name of the agent.
     *
     * @return the agent's name
     */
    String getName();

    /**
     * Resets the agent's internal state.
     * <p>
     * This may include clearing input buffers or temporary values.
     */
    void reset();

    /**
     * Callback method invoked when a subscribed topic publishes a message.
     *
     * @param topic the name of the topic
     * @param msg   the message received from the topic
     */
    void callback(String topic, Message msg);

    /**
     * Performs any necessary cleanup when the agent is no longer needed.
     */
    void close();
}
