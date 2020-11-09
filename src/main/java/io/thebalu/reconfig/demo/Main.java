package io.thebalu.reconfig.demo;

import org.jgrapht.Graph;
import org.jgrapht.alg.interfaces.MatchingAlgorithm;
import org.jgrapht.alg.matching.GreedyWeightedMatching;
import org.jgrapht.graph.DefaultDirectedWeightedGraph;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;
import org.jgrapht.graph.SimpleWeightedGraph;
import org.jgrapht.nio.ExportException;

import java.util.*;

import static io.thebalu.reconfig.demo.Util.CENTER;

public final class Main {

    public static void main(String[] args) throws ExportException {

        // For now, the program can be configured from here. In a future version, these will be taken as parameters
        Demands demands = new Demands(new int[][]{
                {0,3,4,5},
                {4,0,8,10},
                {6,9,0,15},
                {8,12,16,0}
        });

        System.out.println("Demands: ");
        System.out.println(demands);


        // Fill graph with data for demo. Later it will be taken as parameter
        Graph<String, NetworkEdge> myGraph = Util.createGraph(4);

        // Main algorithm is Algorithm 2 from the paper
        // line 1
        Set<Double> thresholds = new HashSet<>();

        // line 2
        Graph<String, DefaultWeightedEdge> originalFlow = computeFlow(myGraph, demands);
        Graph<String, DefaultWeightedEdge> newFlow = computeFlow(myGraph, demands);

        // line 3-4
        for (NetworkEdge e : myGraph.edgeSet()) {
            if (e.getClass() == StaticEdge.class) {
                DefaultWeightedEdge flowEdge = originalFlow.getEdge(myGraph.getEdgeSource(e), myGraph.getEdgeTarget(e));
                double load = originalFlow.getEdgeWeight(flowEdge) / e.getCapacity();
                thresholds.add(load);
            }
        }
        // line 5-7
        for (NetworkEdge e : myGraph.edgeSet()) {
            if (e.getClass() == ReconfigurableEdge.class) {
                preprocessTriangle(myGraph, (ReconfigurableEdge) e, demands);
                thresholds.add(((ReconfigurableEdge) e).getOptimalTriangleDemand());
            }
        }

        System.out.println("Original graph:");
        Util.renderGraph(myGraph);
        System.out.println("Original optimal flow:");
        Util.renderFlowGraph(originalFlow);


        List<Double> orderedThresholds = new ArrayList<>(thresholds);
        Collections.sort(orderedThresholds);
        System.out.println("Threshold levels to check: " + orderedThresholds);

        MatchingAlgorithm.Matching<ColoredVertex, DefaultWeightedEdge> matchingResult = determineReconfiguration(myGraph, demands, originalFlow, orderedThresholds);

        if (matchingResult == null) {
            System.out.println("No solution found");
        } else {

            Graph<String, NetworkEdge> resultGraph = new DefaultDirectedWeightedGraph<>(NetworkEdge.class);

            for (String v : myGraph.vertexSet()) {
                resultGraph.addVertex(v);
            }

            for (NetworkEdge e : myGraph.edgeSet()) {
                if (e.getClass() == StaticEdge.class) {
                    resultGraph.addEdge(myGraph.getEdgeSource(e), myGraph.getEdgeTarget(e), new StaticEdge(e.getCapacity()));
                }
            }

            for (DefaultWeightedEdge e : matchingResult.getEdges()) {
                String edgeSource = matchingResult.getGraph().getEdgeSource(e).name;
                String edgeTarget = matchingResult.getGraph().getEdgeTarget(e).name;
                resultGraph.addEdge(edgeSource, edgeTarget, myGraph.getEdge(edgeSource, edgeTarget));
                resultGraph.addEdge(edgeTarget, edgeSource, myGraph.getEdge(edgeTarget, edgeSource));

                newFlow.setEdgeWeight(newFlow.getEdge(edgeSource, CENTER), newFlow.getEdgeWeight(newFlow.getEdge(edgeSource, CENTER)) - demands.getDemand(edgeSource, edgeTarget));
                newFlow.setEdgeWeight(newFlow.getEdge(CENTER, edgeTarget), newFlow.getEdgeWeight(newFlow.getEdge(CENTER, edgeTarget)) - demands.getDemand(edgeSource, edgeTarget));

                newFlow.setEdgeWeight(newFlow.getEdge(edgeTarget, CENTER), newFlow.getEdgeWeight(newFlow.getEdge(edgeTarget, CENTER)) - demands.getDemand(edgeTarget, edgeSource));
                newFlow.setEdgeWeight(newFlow.getEdge(CENTER, edgeSource), newFlow.getEdgeWeight(newFlow.getEdge(CENTER, edgeSource)) - demands.getDemand(edgeTarget, edgeSource));

                Graph<String, DefaultWeightedEdge> optimalTriangleFlow = ((ReconfigurableEdge) myGraph.getEdge(edgeSource, edgeTarget)).getOptimalTriangleFlow();
                newFlow.setEdgeWeight(newFlow.addEdge(edgeSource, edgeTarget), optimalTriangleFlow.getEdgeWeight(optimalTriangleFlow.getEdge(edgeSource, edgeTarget)));
                newFlow.setEdgeWeight(newFlow.addEdge(edgeTarget, edgeSource), optimalTriangleFlow.getEdgeWeight(optimalTriangleFlow.getEdge(edgeTarget, edgeSource)));
            }

            System.out.println("Result graph:");
            Util.renderGraph(resultGraph);
            System.out.println("Resulting optimal flow:");
            Util.renderFlowGraph(newFlow);
        }

    }

    public static MatchingAlgorithm.Matching<ColoredVertex, DefaultWeightedEdge> determineReconfiguration(Graph<String, NetworkEdge> network, Demands demands,
                                                                                                          Graph<String, DefaultWeightedEdge> originalFlow, List<Double> thresholds) {

        for (double currentThreshold : thresholds) {

            Graph<ColoredVertex, DefaultWeightedEdge> currentNetwork = new SimpleWeightedGraph<>(DefaultWeightedEdge.class);
            int redCount = 0;
            for (String v : network.vertexSet()) {
                if (!v.equals(CENTER)) {

                    if (originalFlow.getEdgeWeight(originalFlow.getEdge(CENTER, v)) / network.getEdge(CENTER, v).getCapacity() > currentThreshold ||
                            originalFlow.getEdgeWeight(originalFlow.getEdge(v, CENTER)) / network.getEdge(v, CENTER).getCapacity() > currentThreshold) {

                        currentNetwork.addVertex(new ColoredVertex(v, true));
                        redCount++;
                    } else {
                        currentNetwork.addVertex(new ColoredVertex(v, false));
                    }
                }
            }

            for (NetworkEdge e : network.edgeSet()) {

                if (e.getClass() == ReconfigurableEdge.class &&
                        (((ReconfigurableEdge) e).getOptimalTriangleFlow().containsEdge(network.getEdgeSource(e), network.getEdgeTarget(e)) ||
                                ((ReconfigurableEdge) e).getOptimalTriangleFlow().containsEdge(network.getEdgeTarget(e), network.getEdgeSource(e))) &&
                        ((ReconfigurableEdge) e).getOptimalTriangleDemand() <= currentThreshold) {

                    ColoredVertex source = null;
                    ColoredVertex target = null;
                    int w = 0;

                    for (ColoredVertex cv : currentNetwork.vertexSet()) {
                        if (cv.name.equals(network.getEdgeSource(e))) {
                            source = cv;
                            if (cv.isRed) w++;
                        }
                        if (cv.name.equals(network.getEdgeTarget(e))) {
                            target = cv;
                            if (cv.isRed) w++;
                        }
                    }
                    DefaultWeightedEdge newEdge = currentNetwork.addEdge(source, target);
                    if (newEdge != null) {
                        currentNetwork.setEdgeWeight(newEdge, w);

                        if (w == 0) {
                            currentNetwork.removeEdge(newEdge);
                        }
                    }


                }
            }

            GreedyWeightedMatching<ColoredVertex, DefaultWeightedEdge> matching = new GreedyWeightedMatching<>(currentNetwork, false);

            MatchingAlgorithm.Matching<ColoredVertex, DefaultWeightedEdge> matchingResult = matching.getMatching();

            if (matchingResult.getWeight() > (double) redCount - 0.1) {
                return matchingResult;
            }
        }


        return null;
    }

    public static Graph<String, DefaultWeightedEdge> computeFlow(Graph<String, NetworkEdge> g, Demands demands) {
        DefaultDirectedWeightedGraph<String, DefaultWeightedEdge> flowGraph = new DefaultDirectedWeightedGraph<>(DefaultWeightedEdge.class);

        g.vertexSet().forEach(flowGraph::addVertex);
        for (String v : flowGraph.vertexSet()) {
            if (v.equals(CENTER)) continue;
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