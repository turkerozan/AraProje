package org.cytoscapeapp.cyaraproje.internal.algorithms;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.SortedSet;
import java.util.TreeSet;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscapeapp.cyaraproje.internal.ProjectCore;
import org.cytoscapeapp.cyaraproje.internal.algorithms.AbstractModel;

/**
 *
 * @author Ivan
 */
public class WattsStrogatz extends AbstractModel{

    private int N;
    private int K;
    private float beta;
    
    
    public WattsStrogatz(ProjectCore core, int N, int K, float beta) {
        super(core);
        this.N = N;
        this.K = K;
        this.beta = beta;
    }

    @Override
    protected void initializeSpecifics() {
    }

    @Override
    public void Execute() {
        if(N < 0 || K >= N || K < 0 || beta < 0 || beta > 1){
            return;
        }
        CyNetwork net = generateEmptyNetwork(N);
        // array of all the nodes in the network
        ArrayList<CyNode> nodes = new ArrayList<>(net.getNodeList());
        int Khalf = K/2;
        // array of sets of neighbours, one for each node
        ArrayList<TreeSet<Integer>> neighbourhood = new ArrayList<>(N);
        // array of initial lattice edges
        LinkedList<Edge> edges = new LinkedList<>();
        
        for (int i = 0; i < N; i++) {
            neighbourhood.add(new TreeSet<Integer>());
        }
        
        
        // generate lattice
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < Khalf; j++) {
                int a = i;
                int b = (i+j+1)%N;
                neighbourhood.get(a).add(b);
                neighbourhood.get(b).add(a);
                edges.add(new Edge(a, b));
            }
        }
        
        // rewire
        for (Edge edge : edges) {
            if(randomBoolean(beta)){
                int i = edge.a;
                int j = edge.b;
                SortedSet<Integer> neighbours = neighbourhood.get(i);
                // temporarily add node to its neighbours list, to avoid adding a self-loop
                neighbours.add(i);
                int numOfNeighbours = neighbours.size();
                int newJ = random.nextInt(N - numOfNeighbours);
                for (Integer neighbour : neighbours) {
                    if(newJ >= neighbour){
                        newJ++;
                    }
                    else break;
                }
                // remove the node itself from its list of neighbours
                neighbours.remove(i);
                // remove the old edge info from the neighbours list
                neighbours.remove(j);
                neighbourhood.get(j).remove(i);
                // add the new edge info to the neighbours list
                neighbours.add(newJ);
                neighbourhood.get(newJ).add(i);
            }
        }
        
        // construct a network from neighbourhood data
        for (Integer i = 0; i < N; i++) {
            for (Integer j : neighbourhood.get(i).tailSet(i)) {
                CyEdge edge = net.addEdge(nodes.get(i), nodes.get(j), false);
                // Not sure about this naming!
                //net.getRow(edge).set(CyNetwork.NAME, getEdgeName(nodes.get(i), nodes.get(j), net));
                net.getRow(edge).set(CyNetwork.NAME, net.getDefaultNodeTable().getRow(edge.getTarget().getSUID()).get("name", String.class) + " pp " + net.getDefaultNodeTable().getRow(edge.getSource().getSUID()).get("name", String.class));
                //net.getRow(edge).set("interaction", createInteraction(nodes.get(i), nodes.get(j), net));
                net.getRow(edge).set("interaction", "pp");
            }
        }
        
        // send network to cytoscape
        pushNetwork(net);
        
    }

    @Override
    protected String getModelName() {
        return "WattsStrogatz";
    }
    
}