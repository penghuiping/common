package com.php25.common.coresample;

import com.google.common.graph.GraphBuilder;
import com.google.common.graph.Graphs;
import com.google.common.graph.MutableGraph;
import org.junit.Test;

import java.util.Set;

/**
 * @author penghuiping
 * @date 2020/1/13 17:21
 */
public class GraphTest {

    /**           a
     *      b     c     d
     *    e  f   g h   j
     *   g
     */
    @Test
    public void test() {
        MutableGraph<String> tree = GraphBuilder.directed().allowsSelfLoops(false).build();

        tree.addNode("a");
        tree.putEdge("a","b");
        tree.putEdge("a","c");
        tree.putEdge("a","d");
        tree.putEdge("b","e");
        tree.putEdge("b","f");
        tree.putEdge("c","g");
        tree.putEdge("c","h");
        tree.putEdge("d","j");
        tree.putEdge("e","g");

        Set<String> result =tree.successors("c");
        System.out.println(Graphs.reachableNodes(tree,"e"));
    }
}
