package org.cytoscapeapp.cyaraproje.internal.algorithms;

import java.util.ArrayList;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscapeapp.cyaraproje.internal.ProjectCore;

/**
 *
 * @author Ivan
 */
public class ErdosRenyi extends AbstractModel{

    public enum ERType{ nM, np }
    
    private int n;
    private int M;
    private float p;
    ERType type;

    /**
     * @param n number of nodes
     * @param M number of edges in G(n,M) model type
     * @param p probability of an edge between each pair of nodes in G(n,p) model type
     * @param type type of model used: G(n,P) or G(n,M).
     * @param core randomizerCore
     */
    public ErdosRenyi(int n, int M, float p, ERType type, ProjectCore core) {
        super(core);
        this.n = n;
        this.M = M;
        this.p = p;
        this.type = type;
    }
    
    /**
     * Construct a randomized version of a given network preserving the number of nodes and edges using the Erdos-Renyi G(n,M) model.
     * @param core randomizerCore from which a current network will be used to determine 
     * parameters for G(n,M) model (number of nodes and edges respectively).
     */
    public ErdosRenyi(ProjectCore core) {
        super(core);
        type = ERType.nM;
        n = core.getCurrentnetwork().getNodeCount();
        M = core.getCurrentnetwork().getEdgeCount();
    }

    @Override
    protected void initializeSpecifics() {
        // nothing to do here
    }

    @Override
    public void Execute() {
        CyNetwork net = generateEmptyNetwork(n);
        ArrayList<CyNode> nodes = new ArrayList<>(net.getNodeList());
        
        
        // if type to be used id G(n,M)
        if(type == ERType.nM){
            int maxEdges = n*(n-1)/2;
            if(M > maxEdges) return;

            // reservoir sampling of edges
            ArrayList<Integer> edges = new ArrayList<>(M);
            for (int i = 0; i < M; i++) {
                edges.add(i);
            }
            for (int i = M; i < maxEdges; i++) {
                int j = random.nextInt(i+1);
                if(j < M){
                    edges.set(j, i);
                }
            }
            
            for (Integer edge : edges) {
                Integer i = (int)(0.5 + 0.5 * Math.sqrt(1 + 8*edge));
                Integer j = edge - i*(i-1)/2;
                CyEdge addedEdge = net.addEdge(nodes.get(i), nodes.get(j), false);
                // Not sure about this naming!
                //String name = "Edge_"+i.toString() + "_" + j.toString();
                //net.getRow(addedEdge).set(CyNetwork.NAME, name);
                net.getRow(addedEdge).set(CyNetwork.NAME, net.getDefaultNodeTable().getRow(addedEdge.getTarget().getSUID()).get("name", String.class) + " pp " + net.getDefaultNodeTable().getRow(addedEdge.getSource().getSUID()).get("name", String.class));
                //net.getRow(addedEdge).set("interaction", createInteraction(nodes.get(i), nodes.get(j), net));
                net.getRow(addedEdge).set("interaction", "pp");
            }
        } 
        
        // if type to be used id G(n,p)
        else{
            for (Integer i = 0; i < n-1; i++) {
                for (Integer j = i+1; j < n; j++) {
                    if(randomBoolean(p)){
                        CyEdge edge = net.addEdge(nodes.get(i), nodes.get(j), false);
                        //net.getRow(edge).set(CyNetwork.NAME, getEdgeName(nodes.get(i), nodes.get(j), net));
                        net.getRow(edge).set(CyNetwork.NAME, net.getDefaultNodeTable().getRow(edge.getTarget().getSUID()).get("name", String.class) + " pp " + net.getDefaultNodeTable().getRow(edge.getSource().getSUID()).get("name", String.class));
                        //net.getRow(edge).set("interaction", createInteraction(nodes.get(i), nodes.get(j), net));
                        net.getRow(edge).set("interaction", "pp");
                    }
                }
            }
        }
        pushNetwork(net);
    }
    
    
    @Override
    protected String getModelName() {
        return "ErdosRenyi";
    }
}
