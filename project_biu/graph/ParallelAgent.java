package project_biu.graph;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class ParallelAgent implements Agent {

    private final Agent agent; // הסוכן האמיתי (wrapped agent)
    private final BlockingQueue<Pair> queue;
    private final Thread thread;
    private volatile boolean running = true;

    // זוג של topic ו־message
    private static class Pair {
        final String topic;
        final Message msg;

        Pair(String topic, Message msg) {
            this.topic = topic;
            this.msg = msg;
        }
    }

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
                // יציאה שקטה מהלולאה אם נקטע
            }
        });

        thread.start();
    }

    @Override
    public String getName() {
        return agent.getName();
    }

    @Override
    public void reset() {
        agent.reset();
    }

    @Override
    public void callback(String topic, Message msg) {
        try {
            queue.put(new Pair(topic, msg));
        } catch (InterruptedException e) {
            // אפשר להתעלם או להדפיס שגיאה
        }
    }

    @Override
    public void close() {
        running = false;
        thread.interrupt(); // כדי שלא ייתקע ב־take()
        agent.close();
    }
}
