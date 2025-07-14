package project_biu.configs;

import project_biu.graph.Agent;
import project_biu.graph.ParallelAgent;

import java.io.BufferedReader;
import java.io.FileReader;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

/**
 * GenericConfig loads a configuration file that defines a list of agents using reflection.
 * Each agent is defined by 3 lines:
 * <pre>
 * Line 1: Fully-qualified class name of the agent (e.g., project_biu.configs.PlusAgent)
 * Line 2: Comma-separated list of input topics (e.g., A,B)
 * Line 3: Comma-separated list of output topics (e.g., C)
 * </pre>
 * Each agent is wrapped in a {@link ParallelAgent} for asynchronous processing.
 */
public class GenericConfig implements Config {

    private final List<ParallelAgent> agents = new ArrayList<>();
    private String confFile;

    /**
     * Sets the path to the configuration file.
     *
     * @param filename path to a config file
     */
    public void setConfFile(String filename) {
        this.confFile = filename;
    }

    /**
     * Parses the configuration file and dynamically instantiates agents using reflection.
     * Each group of 3 lines defines one agent:
     * - class name
     * - input topics
     * - output topics
     *
     * Example:
     * <pre>
     * project_biu.configs.PlusAgent
     * A,B
     * R1
     * </pre>
     */
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

    /**
     * Closes all agents by calling {@link ParallelAgent#close()}.
     */
    @Override
    public void close() {
        for (ParallelAgent pa : agents) {
            pa.close();
        }
    }

    /**
     * Returns the name of this config strategy.
     */
    @Override
    public String getName() {
        return "GenericConfig";
    }

    /**
     * Returns the version of this config strategy.
     */
    @Override
    public int getVersion() {
        return 1;
    }
}
