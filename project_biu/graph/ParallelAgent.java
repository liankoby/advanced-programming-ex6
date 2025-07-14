package project_biu.graph;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * A decorator for an {@link Agent} that enables asynchronous execution.
 * <p>
 * This class wraps a regular agent and processes its callbacks on a separate thread using a blocking queue.
 * Useful for parallelizing graph computations and avoiding blocking in message propagation.
 */
public class ParallelAgent implements Agent {

    /** The wrapped agent that does the actual computation. */
    private final Agent agent;

    /** A queue of pending topic-message pairs to be processed by the agent. */
    private final BlockingQueue<Pair> queue;

    /** Worker thread that processes messages from the queue. */
    private final Thread thread;

    /** Flag used to gracefully stop the background thread. */
    private volatile boolean running = true;

    /**
     * Helper class to hold a pair of topic and message.
     */
    private static class Pair {
        final String topic;
        final Message msg;

        Pair(String topic, Message msg) {
            this.topic = topic;
            this.msg = msg;
        }
    }

    /**
     * Constructs a new ParallelAgent that wraps the given agent and processes messages asynchronously.
     *
     * @param agent the agent to wrap
     * @param capacity the maximum number of messages to buffer
     */
    public ParallelAgent(Agent agent, int capacity) {
        this.agent = agent;
        this.queue = new ArrayBlockingQueue<>(capacity);

        this.thread = new Thread(() -> {
            try {
                while (running || !queue.isEmpty()) {
                    Pair p = queue.take();
                    agent.callback(p.topic, p.msg);
                }
            } catch (InterruptedException e) {
                // Quietly exit the loop when interrupted
            }
        });

        thread.start();
    }

    /**
     * Returns the name of the wrapped agent.
     *
     * @return agent name
     */
    @Override
    public String getName() {
        return agent.getName();
    }

    /**
     * Resets the wrapped agent.
     */
    @Override
    public void reset() {
        agent.reset();
    }

    /**
     * Asynchronously schedules a message to be processed by the wrapped agent.
     *
     * @param topic the name of the topic the message came from
     * @param msg the message to process
     */
    @Override
    public void callback(String topic, Message msg) {
        try {
            queue.put(new Pair(topic, msg));
        } catch (InterruptedException e) {
            // Optional: log error or ignore
        }
    }

    /**
     * Gracefully shuts down the background thread and closes the wrapped agent.
     */
    @Override
    public void close() {
        running = false;
        thread.interrupt(); // unblock if waiting
        agent.close();
    }
}
