package project_biu.graph;

import java.util.ArrayList;
import java.util.List;

public class Topic {
    public final String name;
    public final List<Agent> subs = new ArrayList<>();
    public final List<Agent> pubs = new ArrayList<>();

    private Message lastMessage;  // ✅ Add this field

    public Topic(String name) {
        this.name = name;
    }

    public void subscribe(Agent a) {
        if (!subs.contains(a)) {
            subs.add(a);
        }
    }

    public void unsubscribe(Agent a) {
        subs.remove(a);
    }

    public void publish(Message m) {
        this.lastMessage = m;  // ✅ Store the last message
        for (Agent a : subs) {
            a.callback(this.name, m);
        }
    }

    public void addPublisher(Agent a) {
        if (!pubs.contains(a)) {
            pubs.add(a);
        }
    }

    public void removePublisher(Agent a) {
        pubs.remove(a);
    }
}
