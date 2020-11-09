package io.thebalu.reconfig.demo;

import org.jgrapht.graph.DefaultWeightedEdge;

public abstract class NetworkEdge extends DefaultWeightedEdge {
    private int capacity;

    protected NetworkEdge(int capacity) {
        this.capacity = capacity;
    }

    public int getCapacity() {
        return capacity;
    }


}
