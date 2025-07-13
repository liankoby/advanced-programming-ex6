package project_biu.graph;

import java.util.Date;

public class Message {
    public final byte[] data;
    public final String asText;
    public final double asDouble;
    public final Date date;

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

    public Message(byte[] arr) {
        this(new String(arr));
    }

    public Message(double d) {
        this(Double.toString(d));
    }

}
