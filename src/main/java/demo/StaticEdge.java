package demo;

/* Non-reconfigurable link in the network */
public class StaticEdge extends NetworkEdge {

    protected StaticEdge(int capacity) {
        super(capacity);
    }

    @Override
    public String toString() {
        return "{(static)" + "capacity=" + getCapacity() + "}";
    }
}
