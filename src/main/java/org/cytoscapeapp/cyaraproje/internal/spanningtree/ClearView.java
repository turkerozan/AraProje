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
public class ClearView extends Thread {

    public boolean stop;
    public CyNetwork currentnetwork;
    public CyNetworkView currentnetworkview;
    String edgeWeightAttribute;
    ProjectStartMenu menu;
    public CyNetwork STNetwork = null;
    public CyNetworkFactory netFactory;
    public CyNetworkManager netManager;
    boolean isStepped;
    public CyNetworkView defaultnetworkview;
    public ClearView(CyNetwork currentnetwork, CyNetworkView currentnetworkview, ProjectStartMenu menu) {
        this.currentnetwork = currentnetwork;
        this.currentnetworkview = currentnetworkview;
        this.netFactory = netFactory;
        this.netManager = netManager;      
        this.menu = menu;
    }

    @Override
    public void run() {
        CyTable nodeTable = currentnetwork.getDefaultNodeTable();
        String columnName1 = "Infection";
        String columnName2 = "When";
        List<CyNode> nodeList = currentnetwork.getNodeList();
        for(CyNode clearingNode : nodeList){
        currentnetworkview.getNodeView(clearingNode).setVisualProperty(BasicVisualLexicon.NODE_FILL_COLOR, new Color(137, 208, 245));
        currentnetworkview.getNodeView(clearingNode).setVisualProperty(BasicVisualLexicon.NODE_SHAPE, NodeShapeVisualProperty.ROUND_RECTANGLE);   
        }
        nodeTable.deleteColumn(columnName2);
        nodeTable.deleteColumn(columnName1);
        currentnetworkview.updateView();
        
       
        }
    public void end() {
        stop = true;
    }
}
