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
import java.util.Map;

import javax.swing.*;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import org.cytoscape.model.CyEdge;
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

public class SourceDetection implements Runnable {

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

    public SourceDetection(CyNetwork currentnetwork, CyNetworkView currentnetworkview, ProjectStartMenu menu) {
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
        Map<Long, List<CyNode>> received = new HashMap<Long, List<CyNode>>();
        Map<Long, List<CyNode>> signals = new HashMap<Long, List<CyNode>>();
        int sizem = nodeWithValue.size();

        for (CyNode nodeIterator : nodeWithValue) {//initiliaze node maps for infected ones
            received.put(nodeIterator.getSUID(), new ArrayList<CyNode>());
            signals.put(nodeIterator.getSUID(), new ArrayList<CyNode>());
        }

        for (CyNode nodeIterator : nodeWithValue) {
            List<CyNode> neighbors = currentnetwork.getNeighborList(nodeIterator, CyEdge.Type.ANY);
            for (CyNode neighbor : neighbors) {
                if (received.get(neighbor.getSUID()) != null) {
                    received.get(neighbor.getSUID()).add(nodeIterator);

                    //JOptionPane.showMessageDialog(null, "node : " + currentnetwork.getRow(neighbor).get(CyNetwork.NAME, String.class)
                    // + " has " + currentnetwork.getRow(nodeIterator).get(CyNetwork.NAME, String.class) );
                }
            }
        }
        int i = 0;
        int q = 0;
        int j = 0;
        while (f == 0) {
            for (CyNode nodeIterator : nodeWithValue) {
                for (i = 0; i < received.get(nodeIterator.getSUID()).size(); i++) {
                    if (signals.get(nodeIterator.getSUID()).contains(received.get(nodeIterator.getSUID()).get(i))) {

                    } else {
                        signals.get(nodeIterator.getSUID()).add(received.get(nodeIterator.getSUID()).get(i)); 
                        //JOptionPane.showMessageDialog(null, "node : " + currentnetwork.getRow(nodeIterator).get(CyNetwork.NAME, String.class)+ "signal list" 
                        //+ currentnetwork.getRow(received.get(nodeIterator.getSUID()).get(i)).get(CyNetwork.NAME, String.class));
                        List<CyNode> neighbors = currentnetwork.getNeighborList(nodeIterator, CyEdge.Type.ANY);
                        for (CyNode neighbor : neighbors) {
                            if (received.get(neighbor.getSUID()) != null) {
                                if(received.get(neighbor.getSUID()).contains(received.get(nodeIterator.getSUID()).get(i))){
                                //JOptionPane.showMessageDialog(null, "node : " + currentnetwork.getRow(neighbor).get(CyNetwork.NAME, String.class)
                                // + " contains  " + currentnetwork.getRow(received.get(nodeIterator.getSUID()).get(i)).get(CyNetwork.NAME, String.class) );
                                }
                                else {received.get(neighbor.getSUID()).add(received.get(nodeIterator.getSUID()).get(i));
                               // JOptionPane.showMessageDialog(null, "node : " + currentnetwork.getRow(neighbor).get(CyNetwork.NAME, String.class)
                                 //+ " has " + currentnetwork.getRow(received.get(nodeIterator.getSUID()).get(i)).get(CyNetwork.NAME, String.class) );
                                }
                            }
                        }

                    }

                }
                for (CyNode nodeIterator1 : nodeWithValue) {
                    if (received.get(nodeIterator1.getSUID()).size() == sizem) {
                        //JOptionPane.showMessageDialog(null, "root is " + currentnetwork.getRow(nodeIterator1).get(CyNetwork.NAME, String.class));
                        currentnetworkview.getNodeView(nodeIterator1).setVisualProperty(BasicVisualLexicon.NODE_FILL_COLOR, Color.pink);
                        
                        q = 1;
                        for (CyNode temp : received.get(nodeIterator1.getSUID())) {
			//JOptionPane.showMessageDialog(null, "root list  " + currentnetwork.getRow(temp).get(CyNetwork.NAME, String.class));
		}
                    }
                }
                if (q == 1) {
                    f = 1;
                    break;
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
