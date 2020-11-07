package demo;

import org.jgrapht.Graph;

/* Reconfigurable link in the network */
public class ReconfigurableEdge extends NetworkEdge {

    private boolean isEnabled;

    private Double optimalTriangleDemand;
    private Graph<String, NetworkEdge> optimalTriangleFlow;

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

    public void setOptimalTriangleFlow(Graph g) {
        optimalTriangleFlow = g;
    }
    public void setOptimalTriangleDemand(double d) {
        optimalTriangleDemand = d;
    }

    public Double getOptimalTriangleDemand() {
        return optimalTriangleDemand;
    }

    public Graph<String, NetworkEdge> getOptimalTriangleFlow() {
        return optimalTriangleFlow;
    }

    @Override
    public String toString() {
        return "{(reconf)" + "capacity=" + getCapacity() + ", OPTIMAL="+ optimalTriangleDemand + " } ";
    }
}
