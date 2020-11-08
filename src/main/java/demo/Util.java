package demo;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;
import org.jgrapht.nio.Attribute;
import org.jgrapht.nio.DefaultAttribute;
import org.jgrapht.nio.ExportException;
import org.jgrapht.nio.dot.DOTExporter;

import java.io.StringWriter;
import java.io.Writer;
import java.util.LinkedHashMap;
import java.util.Map;

public class Util {
    public static final String CENTER = "c";

    public static void renderGraph(Graph<String, NetworkEdge> graph)
            throws ExportException {

        DOTExporter<String, NetworkEdge> exporter =
                new DOTExporter<>(v -> v);
        exporter.setVertexAttributeProvider((v) -> {
            Map<String, Attribute> map = new LinkedHashMap<>();
            map.put("label", DefaultAttribute.createAttribute(v.toString()));
            return map;
        });

        exporter.setEdgeAttributeProvider((edge) -> {
            Map<String, Attribute> map = new LinkedHashMap<>();
            map.put("label", DefaultAttribute.createAttribute(edge.toString()));
            if (edge.getClass() == ReconfigurableEdge.class) {
                map.put("style", DefaultAttribute.createAttribute("dashed"));

            }
            return map;
        });
        Writer writer = new StringWriter();
        exporter.exportGraph(graph, writer);
        System.out.println(writer.toString());
    }

    public static void renderFlowGraph(Graph<String, DefaultWeightedEdge> graph)
            throws ExportException {

        DOTExporter<String, DefaultWeightedEdge> exporter =
                new DOTExporter<>(v -> v);
        exporter.setVertexAttributeProvider((v) -> {
            Map<String, Attribute> map = new LinkedHashMap<>();
            map.put("label", DefaultAttribute.createAttribute(v.toString()));
            return map;
        });

        exporter.setEdgeAttributeProvider((edge) -> {
            Map<String, Attribute> map = new LinkedHashMap<>();
            map.put("label", DefaultAttribute.createAttribute(edge.toString() + " - " + graph.getEdgeWeight(edge)));

            return map;
        });
        Writer writer = new StringWriter();
        exporter.exportGraph(graph, writer);
        System.out.println(writer.toString());
    }


    public static Graph<String, NetworkEdge> createGraph(int numVertex) {
        Graph<String, NetworkEdge> g = new SimpleDirectedWeightedGraph<>(NetworkEdge.class);

        g.addVertex(CENTER);

        for (int i = 0; i < numVertex; i++) {
            String v = "v" + i;
            g.addVertex(v);
            g.addEdge(v, CENTER, new StaticEdge(100));
            g.addEdge(CENTER, v, new StaticEdge(100));
        }

        for (String v1 : g.vertexSet()) {
            for (String v2 : g.vertexSet()) {
                if (!v1.equals(v2) && !v1.equals(CENTER) && !v2.equals(CENTER)) {
                    g.addEdge(v1, v2, new ReconfigurableEdge(100));
                    g.addEdge(v2, v1, new ReconfigurableEdge(100));
                }
            }
        }

        return g;
    }
}
