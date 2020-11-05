package demo;

/* Reconfigurable link in the network */
public class ReconfigurableEdge extends NetworkEdge {

    private boolean isEnabled;

    protected ReconfigurableEdge(int capacity) {
        super(capacity);
        isEnabled = false;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public void enable() {
        isEnabled = true;
    }

    public void disable() {
        isEnabled = false;
    }

    @Override
    public String toString() {
        return "{(reconf)" + "capacity=" + getCapacity() + "}";
    }
}
