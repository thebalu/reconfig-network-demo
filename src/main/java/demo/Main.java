package demo;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultDirectedWeightedGraph;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;
import org.jgrapht.nio.ExportException;

import java.util.*;

import static demo.Util.CENTER;

public final class Main {

    private Main() {
    }


    public static void main(String[] args) throws ExportException {

        Demands demands = new Demands(4);

        Graph<String, NetworkEdge> myGraph = Util.createGraph(4);



        // line 1
        Set<Double> thresholds = new HashSet<>();

        // line 2
        Graph<String, DefaultWeightedEdge> originalFlow = computeFlow(myGraph, demands);

        // line 3-4
        for (NetworkEdge e : myGraph.edgeSet()) {
            if(e.getClass() == StaticEdge.class) {
                DefaultWeightedEdge flowEdge = originalFlow.getEdge( myGraph.getEdgeSource(e), myGraph.getEdgeTarget(e));
                double load = originalFlow.getEdgeWeight(flowEdge) / e.getCapacity();
                thresholds.add(load);
            }
        }
        // line 5-7
        for (NetworkEdge e : myGraph.edgeSet()) {
            if(e.getClass() == ReconfigurableEdge.class) {
                preprocessTriangle(myGraph, (ReconfigurableEdge) e, demands);
                thresholds.add(((ReconfigurableEdge) e).getOptimalTriangleDemand());
            }
        }

        List<Double> orderedThresholds = new ArrayList<>(thresholds);
        Collections.sort(orderedThresholds);

        Util.renderGraph(myGraph);
    }

    public static Graph<String, DefaultWeightedEdge> computeFlow(Graph<String, NetworkEdge> g, Demands demands) {
        DefaultDirectedWeightedGraph<String, DefaultWeightedEdge> flowGraph = new DefaultDirectedWeightedGraph<>(DefaultWeightedEdge.class);

        g.vertexSet().forEach(flowGraph::addVertex);
        for(String v : flowGraph.vertexSet() ) {
            if(v.equals(CENTER)) continue;
            // Sum of demands to v is the flow on C->v
            flowGraph.setEdgeWeight(flowGraph.addEdge(CENTER, v), demands.getSumDemandTo(v));
            // Sum of demands from v is the flow on v->c
            flowGraph.setEdgeWeight(flowGraph.addEdge(v, CENTER), demands.getSumDemandFrom(v));
        }

        return flowGraph;
    }


    public static ReconfigurableEdge preprocessTriangle(Graph<String, NetworkEdge> g, ReconfigurableEdge recEdge, Demands demands) {
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

        int capIC = g.getEdge(vi, CENTER).getCapacity();
        int capJC = g.getEdge(vj, CENTER).getCapacity();
        int capCI = g.getEdge(CENTER, vi).getCapacity();
        int capCJ = g.getEdge(CENTER, vj).getCapacity();
        int capIJ = g.getEdge(vi, vj).getCapacity();
        int capJI = g.getEdge(vj, vi).getCapacity();

        double direct = Collections.max(Arrays.asList(
                (double) demIJ / capIJ,
                (double) demIC / capIC,
                (double) demCJ / capCJ,
                (double) demJI / capJI,
                (double) demJC / capJC,
                (double) demCI / capCI));
        double indirect = Collections.max(Arrays.asList(
                (double) (demIC + demIJ) / capIC,
                (double) (demCJ + demIJ) / capCJ,
                (double) (demJC + demJI) / capIC,
                (double) (demCI + demJI) / capCJ));

        Graph<String, DefaultWeightedEdge> optGraph = new SimpleDirectedWeightedGraph<>(DefaultWeightedEdge.class);
        optGraph.addVertex(vi);
        optGraph.addVertex(vj);
        optGraph.addVertex(CENTER);
        if (direct < indirect) {
            recEdge.setOptimalTriangleDemand(direct);
            optGraph.setEdgeWeight(optGraph.addEdge(vi, vj), demIJ);
            optGraph.setEdgeWeight(optGraph.addEdge(vi, CENTER), demIC);
            optGraph.setEdgeWeight(optGraph.addEdge(CENTER, vj), demCJ);

            optGraph.setEdgeWeight(optGraph.addEdge(vj, vi), demJI);
            optGraph.setEdgeWeight(optGraph.addEdge(vj, CENTER), demJC);
            optGraph.setEdgeWeight(optGraph.addEdge(CENTER, vi), demCI);

        } else {
            recEdge.setOptimalTriangleDemand(indirect);
            optGraph.setEdgeWeight(optGraph.addEdge(vi, CENTER), demIC + demIJ);
            optGraph.setEdgeWeight(optGraph.addEdge(CENTER, vj), demCJ + demIJ);
            optGraph.setEdgeWeight(optGraph.addEdge(vj, CENTER), demJC + demJI);
            optGraph.setEdgeWeight(optGraph.addEdge(CENTER, vi), demCI + demJI);
        }
        recEdge.setOptimalTriangleFlow(optGraph);

        return recEdge;
    }

}