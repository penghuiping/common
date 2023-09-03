package com.php25.common.coresample;

import com.google.common.graph.GraphBuilder;
import com.google.common.graph.MutableGraph;
import com.google.common.graph.Traverser;
import org.junit.Test;

import java.util.List;

/**
 * @author penghuiping
 * @date 2023/9/3 15:37
 */
public class GraphTest {

    @Test
    public void test() {
        String node1 = "1";
        String node11 = "11";
        String node12 = "12";
        String node2 = "2";
        String node21 = "21";
        String node22 = "22";
        MutableGraph<String> graph = GraphBuilder.undirected().build();
        graph.addNode(node1);
        graph.addNode(node11);
        graph.addNode(node12);
        graph.putEdge(node1, node11);
        graph.putEdge(node1, node12);
        graph.addNode(node2);
        graph.addNode(node21);
        graph.addNode(node22);
        graph.putEdge(node2, node21);
        graph.putEdge(node2, node22);
//        graph.putEdge(node1,node2);

        var iter = Traverser.forGraph(graph).breadthFirst(List.of(node1, node2));
        var iter1 = iter.iterator();
        while (iter1.hasNext()) {
            String val = iter1.next();
            System.out.println(val);
        }

    }
}
