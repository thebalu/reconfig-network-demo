package demo;

import org.jgrapht.Graph;
import org.jgrapht.nio.ExportException;

import java.util.ArrayList;
import java.util.Arrays;

public final class Main {

    private Main() {
    }


    public static void main(String[] args) throws ExportException {

        Demands demands = new Demands(4);

        Graph<String, NetworkEdge> myGraph = Util.createGraph(4);

//        preprocessTriangle(myGraph, someEdge, demands);
        Util.renderGraph(myGraph);
    }


    public static int preprocessTriangle(Graph<String, NetworkEdge> g, ReconfigurableEdge recEdge, Demands demands) {
        String vi = g.getEdgeSource(recEdge);
        String vj = g.getEdgeTarget(recEdge);
        int demIJ = demands.getDemand(vi, vj);
        int demJI = demands.getDemand(vj, vi);

        int demIC = 0, demCI = 0, demJC = 0, demCJ = 0;
        for (String v : g.vertexSet()) {
            if (!v.equals(Util.CENTER) && !v.equals(vi) && !v.equals(vj)) {
                demIC += demands.getDemand(vi, v);
                demCI += demands.getDemand(v, vi);
                demJC += demands.getDemand(vj, v);
                demCJ += demands.getDemand(v, vj);
            }
        }
        // TODO
        //for a routing model 𝜏 ∈ {US,SS,SN},
        //by Lemma 4.3, compute a load-otimization flow 𝑓 Δ in
        //the triangle {𝑣𝑖 ,𝑣 𝑗 ,𝑐 } for demands 𝐷 ′ in a constant time;

        //  compute the minimized maximum load: 𝜇Δ :=𝐿 (𝑓 Δ); 𝑖𝑗 𝑚𝑎𝑥 𝑖𝑗
        return 0;
    }

}