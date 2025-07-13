package project_biu.configs;

import project_biu.graph.Agent;
import project_biu.graph.Message;
import project_biu.graph.Topic;
import project_biu.graph.TopicManagerSingleton;

import java.util.HashMap;
import java.util.Map;

public class MinAgent implements Agent {
    private final String[] subs;
    private final String[] pubs;
    private final Map<String, Double> values = new HashMap<>();

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

    @Override
    public void callback(String topic, Message msg) {
        values.put(topic, msg.asDouble);
        double min = values.values().stream().mapToDouble(Double::doubleValue).min().orElse(Double.MAX_VALUE);

        System.out.println("📥 MinAgent updated " + topic + " = " + msg.asDouble + ", min = " + min);
        TopicManagerSingleton.get().getTopic(pubs[0]).publish(new Message(min));
    }

    @Override public String getName() { return "min"; }
    @Override public void reset() { values.replaceAll((k, v) -> Double.MAX_VALUE); }
    @Override public void close() {}
}
