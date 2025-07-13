package project_biu.configs;

import project_biu.graph.Agent;
import project_biu.graph.ParallelAgent;

import java.io.BufferedReader;
import java.io.FileReader;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

public class GenericConfig implements Config {

    private final List<ParallelAgent> agents = new ArrayList<>();
    private String confFile;

    public void setConfFile(String filename) {
        this.confFile = filename;
    }

    @Override
    public void create() {
        try (BufferedReader reader = new BufferedReader(new FileReader(confFile))) {
            List<String> lines = new ArrayList<>();
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    lines.add(line.trim());
                    System.out.println("📄 Line: " + line.trim());
                }
            }

            if (lines.size() % 3 != 0) {
                System.err.println("❗ Invalid config file: Each agent must be defined by 3 lines.");
                return;
            }

            for (int i = 0; i < lines.size(); i += 3) {
                String className = lines.get(i);
                String[] subs = lines.get(i + 1).split(",");
                String[] pubs = lines.get(i + 2).split(",");

                Class<?> cls = Class.forName(className);
                Constructor<?> ctor = cls.getConstructor(String[].class, String[].class);
                Agent a = (Agent) ctor.newInstance((Object) subs, (Object) pubs);

                ParallelAgent pa = new ParallelAgent(a, 10);
                agents.add(pa);
            }

        } catch (Exception e) {
            System.err.println("❗ Exception while creating config: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void close() {
        for (ParallelAgent pa : agents) {
            pa.close();
        }
    }

    @Override
    public String getName() {
        return "GenericConfig";
    }

    @Override
    public int getVersion() {
        return 1;
    }
}
