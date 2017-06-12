/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cytoscapeapp.cyaraproje.internal.algorithms;

import java.util.ArrayList;
import java.util.HashSet;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscapeapp.cyaraproje.internal.ProjectCore;

/**
 *
 * @author Ivan
 */
public class BarabasiAlbert extends AbstractModel{
    
    private int N;
    private int m;
    private int m0;

    public BarabasiAlbert(ProjectCore core, int N, int m) {
        super(core);
        this.N = N;
        this.m = m;
        this.m0 = 2*m;
        if(m0 < 3) m0 = 6;
    }
    
    public BarabasiAlbert(ProjectCore core) {
        super(core);
        this.N = core.getCurrentnetwork().getNodeCount();
        this.m = core.getCurrentnetwork().getEdgeCount()/N;
        this.m0 = 2*m;
        if(m0 < 3) m0 = 6;
    }

    @Override
    protected void initializeSpecifics() {
    }

    @Override
    public void Execute() {
        if(N < 0 || m >= N || m < 0){
            return;
        }
        CyNetwork net = generateEmptyNetwork(N);
        // array of all the nodes in the network
        ArrayList<CyNode> nodes = new ArrayList<>(net.getNodeList());
        
        // array of each edge-node incidence, saving nodes only
        // this makes preferential attachment O(1) per edge
        ArrayList<Integer> incidences = new ArrayList<>(2*N*m);
        
        // connect initial m0 nodes
        for (Integer i = 0; i < m0; i++) {
            Integer j = (i+1)%m0;
            incidences.add(i);
            incidences.add(j);
            CyEdge edge = net.addEdge(nodes.get(i), nodes.get(j), false);
            //String name = i.toString() + " pp " + j.toString();
            //net.getRow(edge).set(CyNetwork.NAME, name);
            net.getRow(edge).set(CyNetwork.NAME, net.getDefaultNodeTable().getRow(edge.getTarget().getSUID()).get("name", String.class) + " pp " + net.getDefaultNodeTable().getRow(edge.getSource().getSUID()).get("name", String.class));
            //net.getRow(edge).set("interaction", createInteraction(nodes.get(i), nodes.get(j), net));
            net.getRow(edge).set("interaction", "pp");
        }
        
        // preferential attachment
        for (Integer i = m0; i < N; i++) {
            HashSet<Integer> currentNodeNeighbours = new HashSet<>(m);
            while(currentNodeNeighbours.size() != m){
                int incPos = random.nextInt(incidences.size());
                int j = incidences.get(incPos);
                currentNodeNeighbours.add(j);
            }
            for (Integer j : currentNodeNeighbours) {
                incidences.add(i);
                incidences.add(j);
                CyEdge edge = net.addEdge(nodes.get(i), nodes.get(j), false);
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
        return "BarabasiAlbert";
    }
    
}
