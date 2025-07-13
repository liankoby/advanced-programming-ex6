package project_biu.graph;

import project_biu.graph.Agent;
import project_biu.graph.Message;
import project_biu.graph.TopicManagerSingleton;

import java.util.function.BinaryOperator;

public class BinOpAgent implements Agent {

    private final String name;
    private final String topic1, topic2, outputTopic;
    private final BinaryOperator<Double> op;
    private Double v1 = null, v2 = null;

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

    @Override
    public void reset() {
        v1 = v2 = null;
    }

    @Override
    public void close() {}

    @Override
    public String getName() {
        return name;
    }
}
