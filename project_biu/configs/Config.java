package project_biu.configs;

/**
 * Represents a configuration strategy for building a computational graph.
 * Implementations of this interface are responsible for instantiating agents,
 * wiring them to topics, and managing their lifecycle.
 *
 * <p>Typically used with {@code GenericConfig} to dynamically create agent-based
 * graphs based on a config file.</p>
 */
public interface Config {

    /**
     * Instantiates all agents and connects them to topics.
     * This method is typically called once after setting the config source (e.g., a file).
     */
    void create();

    /**
     * Returns the name of this configuration strategy.
     *
     * @return human-readable name (e.g., "GenericConfig")
     */
    String getName();

    /**
     * Returns the version of this configuration strategy.
     *
     * @return integer version (e.g., 1)
     */
    int getVersion();

    /**
     * Closes and releases all agents and associated resources.
     * Called when the configuration is being unloaded or reset.
     */
    void close();
}
