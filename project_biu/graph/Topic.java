package project_biu.graph;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a communication channel (topic) in the pub/sub system.
 * <p>
 * Topics can have multiple publishers and subscribers. When a message is published,
 * all subscribing agents receive it via their {@code callback()} method.
 */
public class Topic {
    /** The name (unique identifier) of the topic. */
    public final String name;

    /** List of agents subscribed to this topic (i.e., consumers). */
    public final List<Agent> subs = new ArrayList<>();

    /** List of agents that publish messages to this topic (i.e., producers). */
    public final List<Agent> pubs = new ArrayList<>();

    /** The most recently published message (used for display/debugging). */
    private Message lastMessage;

    /**
     * Constructs a topic with the given name.
     *
     * @param name the unique topic name
     */
    public Topic(String name) {
        this.name = name;
    }

    /**
     * Subscribes an agent to receive messages from this topic.
     *
     * @param a the agent to subscribe
     */
    public void subscribe(Agent a) {
        if (!subs.contains(a)) {
            subs.add(a);
        }
    }

    /**
     * Unsubscribes an agent from this topic.
     *
     * @param a the agent to remove
     */
    public void unsubscribe(Agent a) {
        subs.remove(a);
    }

    /**
     * Publishes a message to this topic.
     * All subscribed agents will receive the message via their callback.
     *
     * @param m the message to publish
     */
    public void publish(Message m) {
        this.lastMessage = m;
        for (Agent a : subs) {
            a.callback(this.name, m);
        }
    }

    /**
     * Registers an agent as a publisher of this topic.
     *
     * @param a the agent to add as a publisher
     */
    public void addPublisher(Agent a) {
        if (!pubs.contains(a)) {
            pubs.add(a);
        }
    }

    /**
     * Removes an agent from the list of publishers.
     *
     * @param a the agent to remove
     */
    public void removePublisher(Agent a) {
        pubs.remove(a);
    }
}
