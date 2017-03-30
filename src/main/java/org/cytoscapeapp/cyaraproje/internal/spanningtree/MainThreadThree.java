package org.cytoscapeapp.cyaraproje.internal.spanningtree;

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

public class MainThreadThree implements Runnable {

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
    private double qtmp;
    public int stepcounter;
    public CyNetworkFactory netFactory;
    public CyNetworkManager netManager;
    boolean isStepped;
    public CyNetworkView defaultnetworkview;

    public MainThreadThree(CyNetwork currentnetwork, CyNetworkView currentnetworkview, boolean isStepped, ProjectStartMenu menu, double q, int f) {
        this.currentnetwork = currentnetwork;
        this.currentnetworkview = currentnetworkview;
        this.netFactory = netFactory;
        this.netManager = netManager;
        this.q = q;
        this.f = f;
        this.isStepped = isStepped;
        //this.rootNode = rootNode;       
        this.menu = menu;
    }

    @Override
    public void run() {
        defaultnetworkview = currentnetworkview;
        String columnName1 = "Infection";
        String columnName2 = "When";
        int infectedVal = 1;
        qtmp = 1;
        if (stepcounter == 0) {
            List<CyNode> nodes = CyTableUtil.getNodesInState(currentnetwork, "selected", true);
            //JOptionPane.showMessageDialog(null, "Number of selected nodes are " + nodes.size());
            CyTable nodeTable = currentnetwork.getDefaultNodeTable();
            if (nodeTable.getColumn("Infection") == null) {
                nodeTable.createColumn("Infection", Integer.class, false);
            }

            if (nodeTable.getColumn("When") == null) {
                nodeTable.createColumn("When", Integer.class, false);
            }

            List<CyNode> nodeList = currentnetwork.getNodeList();
            CyNode rootNode = nodes.get(0);
            currentnetwork.getRow(rootNode).set(columnName1, new Integer(1));
            JOptionPane.showMessageDialog(null, "Root Infected");
            currentnetworkview.getNodeView(rootNode).setVisualProperty(BasicVisualLexicon.NODE_FILL_COLOR, Color.RED);
            currentnetwork.getRow(rootNode).set(columnName2, new Integer(0));
        }
        int step = stepcounter;
        if (isStepped) {
            
                Set<CyNode> nodeWithValue = getNodesWithValue(currentnetwork, currentnetwork.getDefaultNodeTable(), "When", step);
                for (CyNode nodeIterator : nodeWithValue) {
                    List<CyNode> neighbors = currentnetwork.getNeighborList(nodeIterator, CyEdge.Type.ANY);
                    for (CyNode neighbor : neighbors) {
                        List<CyNode> neighborsOfNeighbors = currentnetwork.getNeighborList(neighbor, CyEdge.Type.ANY);
                        for(CyNode neighborOfNeighbor : neighborsOfNeighbors){
                        if(currentnetwork.getRow(neighborOfNeighbor).isSet(columnName1)==true){
                        qtmp=qtmp*(1-q);
                        
                        }                        
                        }
                        qtmp = 1 - qtmp;
                        if (Math.random() <= qtmp) {
                            if (currentnetwork.getRow(neighbor).isSet(columnName1) == false) {
                                currentnetworkview.getNodeView(neighbor).setVisualProperty(BasicVisualLexicon.NODE_FILL_COLOR, Color.ORANGE);
                                currentnetworkview.getNodeView(neighbor).setVisualProperty(BasicVisualLexicon.NODE_SHAPE, NodeShapeVisualProperty.OCTAGON);
                                currentnetwork.getRow(neighbor).set(columnName2, step + 1);
                                currentnetwork.getRow(neighbor).set(columnName1, 1);
                            }
                        }
                        qtmp = 1;
                    }
                }

        } else {
            for (step = stepcounter; step < f; step++) {

                Set<CyNode> nodeWithValue = getNodesWithValue(currentnetwork, currentnetwork.getDefaultNodeTable(), "When", step);
                for (CyNode nodeIterator : nodeWithValue) {
                    List<CyNode> neighbors = currentnetwork.getNeighborList(nodeIterator, CyEdge.Type.ANY);
                    for (CyNode neighbor : neighbors) {
                        List<CyNode> neighborsOfNeighbors = currentnetwork.getNeighborList(neighbor, CyEdge.Type.ANY);
                        for(CyNode neighborOfNeighbor : neighborsOfNeighbors){
                        if(currentnetwork.getRow(neighborOfNeighbor).isSet(columnName1)==true){
                        qtmp=qtmp*(1-q);
                        
                        }                        
                        }
                        qtmp = 1 - qtmp;
                        if (Math.random() <= qtmp) {
                            if (currentnetwork.getRow(neighbor).isSet(columnName1) == false) {
                                currentnetworkview.getNodeView(neighbor).setVisualProperty(BasicVisualLexicon.NODE_FILL_COLOR, Color.ORANGE);
                                currentnetworkview.getNodeView(neighbor).setVisualProperty(BasicVisualLexicon.NODE_SHAPE, NodeShapeVisualProperty.OCTAGON);
                                currentnetwork.getRow(neighbor).set(columnName2, step + 1);
                                currentnetwork.getRow(neighbor).set(columnName1, 1);
                            }
                        }
                        qtmp = 1;
                    }
                }
            }
        }
        currentnetworkview.updateView();

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
