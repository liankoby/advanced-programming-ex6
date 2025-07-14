package project_biu.graph;

import java.util.Date;

/**
 * Represents a message that can be published to a topic and delivered to agents.
 * <p>
 * Stores multiple representations of the message: raw bytes, string, parsed number, and timestamp.
 */
public class Message {
    /** The raw byte content of the message. */
    public final byte[] data;

    /** The message as a string. */
    public final String asText;

    /** The message parsed as a double (or NaN if not a valid number). */
    public final double asDouble;

    /** The timestamp when the message was created. */
    public final Date date;

    /**
     * Constructs a Message from a string.
     * Initializes {@code data}, {@code asText}, {@code asDouble}, and {@code date}.
     *
     * @param s the message content as a string
     */
    public Message(String s) {
        this.date = new Date();
        this.asText = s;
        this.data = s.getBytes();
        double val;
        try {
            val = Double.parseDouble(s);
        } catch (NumberFormatException e) {
            val = Double.NaN;
        }
        this.asDouble = val;
    }

    /**
     * Constructs a Message from a byte array.
     * Converts the byte array to string and delegates to the string constructor.
     *
     * @param arr the message content as bytes
     */
    public Message(byte[] arr) {
        this(new String(arr));
    }

    /**
     * Constructs a Message from a double.
     * Converts the double to string and delegates to the string constructor.
     *
     * @param d the numeric message content
     */
    public Message(double d) {
        this(Double.toString(d));
    }
}
