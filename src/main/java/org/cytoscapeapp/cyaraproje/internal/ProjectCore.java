package org.cytoscapeapp.cyaraproje.internal;

import java.util.Properties;
import java.util.Random;
import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.events.SetCurrentNetworkListener;
import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.application.swing.CytoPanel;
import org.cytoscape.application.swing.CytoPanelComponent;
import org.cytoscape.application.swing.CytoPanelName;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNetworkFactory;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscapeapp.cyaraproje.internal.visuals.ChangeEdgeAttributeListener;

/**
 *
 * @author smd.faizan@gmail.com
 */
public class ProjectCore {

    public CyNetwork network;
    public CyNetworkView view;
    public CyApplicationManager cyApplicationManager;
    public CySwingApplication cyDesktopService;
    public CyServiceRegistrar cyServiceRegistrar;
    public CyActivator cyactivator;
    public CyNetworkFactory cyNetworkFactory;
    public CyNetworkManager cyNetworkManager;
    // random to be used throughout the app, so to avoid seed repetition
    public Random random;
    public static ProjectStartMenu spanningtreestartmenu;

    public ProjectCore(CyActivator cyactivator) {
        this.cyactivator = cyactivator;
        this.cyApplicationManager = cyactivator.cyApplicationManager;
        this.cyDesktopService = cyactivator.cyDesktopService;
        this.cyServiceRegistrar = cyactivator.cyServiceRegistrar;
        network = cyApplicationManager.getCurrentNetwork();
        view = cyApplicationManager.getCurrentNetworkView();
        cyNetworkFactory = cyactivator.getCyNetworkFactory();
        cyNetworkManager = cyactivator.getCyNetworkManager();
        spanningtreestartmenu = createSpanningTreeStartMenu();
        random = new Random();
        registerServices();
        updatecurrentnetwork();
    }

    public void updatecurrentnetwork() {
        //get the network view object
        if (view == null) {
            view = null;
            network = null;
        } else {
            view = cyApplicationManager.getCurrentNetworkView();
            //get the network object; this contains the graph  
            network = view.getModel();
        }
    }

    public void closecore() {
        network = null;
        view = null;
    }

    public ProjectStartMenu createSpanningTreeStartMenu() {
        ProjectStartMenu startmenu = new ProjectStartMenu(cyactivator, this);
        cyServiceRegistrar.registerService(startmenu, CytoPanelComponent.class, new Properties());
        CytoPanel cytopanelwest = cyDesktopService.getCytoPanel(CytoPanelName.WEST);
        int index = cytopanelwest.indexOfComponent(startmenu);
        cytopanelwest.setSelectedIndex(index);
        return startmenu;
    }

    public void closeSpanningTreeStartMenu() {
        cyServiceRegistrar.unregisterService(spanningtreestartmenu, CytoPanelComponent.class);
    }

    

    public CyApplicationManager getCyApplicationManager() {
        return this.cyApplicationManager;
    }

    public CySwingApplication getCyDesktopService() {
        return this.cyDesktopService;
    }
    
    public static ProjectStartMenu getSpanningTreeStartMenu(){
        return spanningtreestartmenu;
    }
    public CyNetwork getCurrentnetwork() {
        return network;
    }
    public void updateCurrentNetworks(){
        network = cyApplicationManager.getCurrentNetwork();
        view = cyApplicationManager.getCurrentNetworkView();
    }
    
    public CyNetworkView getCurrentnetworkView() {
        return view;
    }
    void registerServices(){
        ChangeEdgeAttributeListener changeEdgeAttributeListener = new ChangeEdgeAttributeListener();
        cyactivator.cyServiceRegistrar.registerService(changeEdgeAttributeListener, SetCurrentNetworkListener.class, new Properties());
        
    }
    
}
