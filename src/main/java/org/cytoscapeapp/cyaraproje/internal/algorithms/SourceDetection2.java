package org.cytoscapeapp.cyaraproje.internal.algorithms;
import java.io.IOException;
import org.cytoscape.model.CyRow;
import org.cytoscape.model.CyTable;
import org.cytoscape.model.CyTableFactory;
import org.cytoscape.task.edit.MapTableToNetworkTablesTaskFactory;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.TaskMonitor;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;
import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

import javax.swing.*;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import org.cytoscape.model.CyEdge;
import org.cytoscape.util.swing.AbstractTreeTableModel;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNetworkFactory;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyRow;
import org.cytoscape.model.CyTable;
import org.cytoscape.model.CyTableUtil;
import org.cytoscape.model.subnetwork.CyRootNetwork;
import org.cytoscape.model.subnetwork.CySubNetwork;
import org.cytoscape.task.create.NewNetworkSelectedNodesAndEdgesTaskFactory;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.presentation.property.BasicVisualLexicon;
import org.cytoscape.view.presentation.property.NodeShapeVisualProperty;
import org.cytoscape.work.TaskIterator;
import java.util.Random;
import org.cytoscapeapp.cyaraproje.internal.ProjectStartMenu;
import org.cytoscapeapp.cyaraproje.internal.CyActivator;
import org.cytoscapeapp.cyaraproje.internal.cycle.ConnectedComponents;
import org.cytoscapeapp.cyaraproje.internal.visuals.SpanningTreeUpdateView;
import org.cytoscape.view.model.View;
import java.util.concurrent.ThreadLocalRandom;
public class SourceDetection2 implements Runnable {
    private static final double INFINITY = Integer.MAX_VALUE;
    public boolean stop;
    public CyNetwork currentnetwork;
    public CyNetworkView currentnetworkview;
    boolean isMinimum;
    CyNode rootNode;
    String edgeWeightAttribute;
    ProjectStartMenu menu;
    public CyNetwork STNetwork = null;
    private double q;
    private int f;
    public int stepcounter;
    public CyNetworkFactory netFactory;
    public CyNetworkManager netManager;
    boolean isStepped;
    public CyNetworkView defaultnetworkview;

    public SourceDetection2(CyNetwork currentnetwork, CyNetworkView currentnetworkview, ProjectStartMenu menu) {
        this.currentnetwork = currentnetwork;
        this.currentnetworkview = currentnetworkview;
        this.netFactory = netFactory;
        this.netManager = netManager;
        //this.rootNode = rootNode;       
        this.menu = menu;
    }

    @Override
    public void run() {
        int infectedVal = 1;
        f = 0;
        Set<CyNode> nodeWithValue = getNodesWithValue(currentnetwork, currentnetwork.getDefaultNodeTable(), "Infection", infectedVal);
        JOptionPane.showMessageDialog(null, "Number of infected nodes are " + nodeWithValue.size());
        Map<Long, List<CyNode>> centerss = new HashMap<Long, List<CyNode>>();
        //Map<Long, List<CyNode>> signals = new HashMap<Long, List<CyNode>>();
        
        
        int i;
        List<CyNode> nodeList = new ArrayList<CyNode>() ;
        List<CyNode> centers = new ArrayList<CyNode>() ;
        
        for (CyNode nodeIterator : nodeWithValue) {
        nodeList.add(nodeIterator);
        }
        ArrayList colors = new ArrayList(); 
        colors.add(Color.BLUE);
        colors.add(Color.MAGENTA);
        colors.add(Color.LIGHT_GRAY);
        int j = 0;
        int size = nodeList.size();
     
        while(centers.size()!=3){
            int x = ThreadLocalRandom.current().nextInt(0, size + 1);
            rootNode = nodeList.get(x);
            if(centers.contains(rootNode)){
                
            }
            else{
            centers.add(nodeList.get(x));
            nodeList.remove(x);
            size--;
            Color mc = (Color) colors.get(j);
            currentnetworkview.getNodeView(rootNode).setVisualProperty(BasicVisualLexicon.NODE_FILL_COLOR, mc);
            currentnetworkview.getNodeView(centers.get(j++)).setVisualProperty(BasicVisualLexicon.NODE_SHAPE, NodeShapeVisualProperty.ELLIPSE); 
            }
        }
        for (CyNode nodeIterator : centers) {//initiliaze node maps for infected ones
            centerss.put(nodeIterator.getSUID(), new ArrayList<CyNode>());
            centerss.get(nodeIterator.getSUID()).add(nodeIterator);
            
        }
        j=0;
        int il = 0;
        while(!nodeList.isEmpty()){            
            for (CyNode nodeIterator : centers) {
                il = centerss.get(nodeIterator.getSUID()).size();
                for(i=0;i<il;i++){
                //JOptionPane.showMessageDialog(null, "i   " + i);
                List<CyNode> neighbors = currentnetwork.getNeighborList(centerss.get(nodeIterator.getSUID()).get(i), CyEdge.Type.ANY);
                for (CyNode neighbor : neighbors) {
                         if(nodeList.contains(neighbor)){centerss.get(nodeIterator.getSUID()).add(neighbor);
                         //JOptionPane.showMessageDialog(null, "Center :  " + currentnetwork.getRow(nodeIterator).get(CyNetwork.NAME, String.class) 
                         //+"has : " + currentnetwork.getRow(neighbor).get(CyNetwork.NAME, String.class));
                         Color mc = (Color) colors.get(j);
                         currentnetworkview.getNodeView(neighbor).setVisualProperty(BasicVisualLexicon.NODE_FILL_COLOR, mc);
                         nodeList.remove(neighbor);}
                    }
                
                }
                j++;
            }
            j = 0;
            
        }
        getNewRoot((centerss.get(centers.get(0).getSUID())),currentnetwork);
        
    }
    private static void getNewRoot( List<CyNode> subNetwork, CyNetwork currentnetwork){
        int infectedVal = 1;
        int f = 0;
        
        Set<CyNode> nodeWithValue = new HashSet<CyNode>(subNetwork);
        JOptionPane.showMessageDialog(null, "Number of infected nodes are " + nodeWithValue.size());
        Map<Long, List<CyNode>> received = new HashMap<Long, List<CyNode>>();
        Map<Long, List<CyNode>> signals = new HashMap<Long, List<CyNode>>();
        Map<Long, List<CyNode>> pending = new HashMap<Long, List<CyNode>>();
        int sizem = nodeWithValue.size();
        
        for (CyNode nodeIterator : nodeWithValue) {//initiliaze node maps for infected ones
            received.put(nodeIterator.getSUID(), new ArrayList<CyNode>());
            signals.put(nodeIterator.getSUID(), new ArrayList<CyNode>());
            pending.put(nodeIterator.getSUID(),new ArrayList<CyNode>());
        }

        for (CyNode nodeIterator : nodeWithValue) {
            signals.get(nodeIterator.getSUID()).add(nodeIterator);
        }
        int i = 0;
        int q = 0;
        int j = 0;
        while (f == 0) {
            //JOptionPane.showMessageDialog(null, "DB 1 : ");
            for (CyNode nodeIterator : nodeWithValue) {
                //JOptionPane.showMessageDialog(null, "DB 2 : ");
                for(CyNode signalIterator : signals.get(nodeIterator.getSUID())){
                   // JOptionPane.showMessageDialog(null, "DB 3 : ");
                    for(CyNode neighborIterator : currentnetwork.getNeighborList(nodeIterator, CyEdge.Type.ANY)){
                      // JOptionPane.showMessageDialog(null, "DB 4 : ");
                        if(currentnetwork.getRow(neighborIterator).isSet("Infection")==true || nodeWithValue.contains(neighborIterator) ){
                        pending.get(neighborIterator.getSUID()).add(signalIterator);
                        JOptionPane.showMessageDialog(null, "node : " + currentnetwork.getRow(neighborIterator).get(CyNetwork.NAME, String.class)+ "pending list" 
                        + currentnetwork.getRow(signalIterator).get(CyNetwork.NAME, String.class));
                        }
                    }
                }
                JOptionPane.showMessageDialog(null, "DB 1 : ");
                signals.get(nodeIterator.getSUID()).clear();
            }
            for(CyNode nodeIterator: nodeWithValue){
                JOptionPane.showMessageDialog(null, "Node" + currentnetwork.getRow(nodeIterator).get(CyNetwork.NAME, String.class));
               
                for(CyNode pendingIterator : pending.get(nodeIterator.getSUID())){
                    
                    if(received.get(nodeIterator.getSUID()).contains(pendingIterator)){
                    JOptionPane.showMessageDialog(null,"we have this node in rec.");
                    }
                    else{
                        JOptionPane.showMessageDialog(null, "Node:" + currentnetwork.getRow(nodeIterator).get(CyNetwork.NAME, String.class)
                        +   " Has sig and rec " + currentnetwork.getRow(pendingIterator).get(CyNetwork.NAME, String.class));
                        signals.get(nodeIterator.getSUID()).add(pendingIterator);
                        received.get(nodeIterator.getSUID()).add(pendingIterator);
                    }
                    
                }
                
                pending.get(nodeIterator.getSUID()).clear();
                JOptionPane.showMessageDialog(null, "Node:" + currentnetwork.getRow(nodeIterator).get(CyNetwork.NAME, String.class)+"PEnding List deleted");
                        
            }
            for(CyNode nodeIterator: nodeWithValue){
                if(received.get(nodeIterator.getSUID()).size()==sizem){
                    JOptionPane.showMessageDialog(null, "root  : " + currentnetwork.getRow(nodeIterator).get(CyNetwork.NAME, String.class));
                    f=1;
                    JOptionPane.showMessageDialog(null,"Algorithm Bitti");
                }
               
                
            }
            
        }
    }
    public void setStep(int stepcounter) {
        this.stepcounter = stepcounter;
    }

    public int getStep() {
        return stepcounter;
    }

    private static Set<CyNode> getNodesWithValue(
            final CyNetwork net, final CyTable table,
            final String colname, final Object value) {
        final Collection<CyRow> matchingRows = table.getMatchingRows(colname, value);
        final Set<CyNode> nodes = new HashSet<CyNode>();
        final String primaryKeyColname = table.getPrimaryKey().getName();
        for (final CyRow row : matchingRows) {
            final Long nodeId = row.get(primaryKeyColname, Long.class);
            if (nodeId == null) {
                continue;
            }
            final CyNode node = net.getNode(nodeId);
            if (node == null) {
                continue;
            }
            nodes.add(node);
        }
        return nodes;
    }

    public void end() {
        stop = true;
    }
}
