package org.cytoscapeapp.cyaraproje.internal;

import java.util.Properties;

import org.cytoscape.model.CyTableFactory;

import org.cytoscapeapp.cyaraproje.internal.algorithms.CreateTableTaskFactory;

import org.cytoscape.work.TaskFactory;


import org.osgi.framework.BundleContext;

import org.cytoscape.service.util.AbstractCyActivator;
import org.cytoscape.task.edit.MapTableToNetworkTablesTaskFactory;

import java.util.Properties;
import org.cytoscape.app.CyAppAdapter;
import org.cytoscape.app.swing.CySwingAppAdapter;
import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.model.CyNetworkFactory;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.model.CyTableFactory;
import org.cytoscape.service.util.AbstractCyActivator;
import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.task.edit.MapTableToNetworkTablesTaskFactory;
import org.cytoscape.view.model.CyNetworkViewFactory;
import org.cytoscape.view.model.CyNetworkViewManager;
import org.cytoscape.view.vizmap.VisualMappingFunctionFactory;
import org.cytoscape.view.vizmap.VisualMappingManager;
import org.cytoscape.view.vizmap.VisualStyleFactory;
import org.osgi.framework.BundleContext;

public class CyActivator extends AbstractCyActivator {
    private static CyAppAdapter appAdapter;
    public CyApplicationManager cyApplicationManager;
    public CySwingApplication cyDesktopService;
    public CyServiceRegistrar cyServiceRegistrar;
    public ProjectMenuAction menuaction;
    public static CyNetworkFactory networkFactory;
    public static CyNetworkManager networkManager;
    public static CyNetworkViewFactory networkViewFactory;
    public static CyNetworkViewManager networkViewManager;
    public static VisualStyleFactory visualStyleFactoryServiceRef;
    public static VisualMappingFunctionFactory vmfFactoryP;
    public static VisualMappingManager vmmServiceRef;
    public static VisualMappingFunctionFactory vmfFactoryC;
    public static VisualMappingFunctionFactory vmfFactoryD;
    public static CySwingAppAdapter adapter;
    @Override
    public void start(BundleContext context) throws Exception {
        String version = new String("1.0");
        this.appAdapter = getService(context, CyAppAdapter.class);
        this.networkViewManager = getService(context, CyNetworkViewManager.class);
        this.networkViewFactory = getService(context, CyNetworkViewFactory.class);
        this.networkFactory = getService(context, CyNetworkFactory.class);
        this.networkManager = getService(context, CyNetworkManager.class);
        this.cyApplicationManager = getService(context, CyApplicationManager.class);
        this.cyDesktopService = getService(context, CySwingApplication.class);
        this.cyServiceRegistrar = getService(context, CyServiceRegistrar.class);
        this.visualStyleFactoryServiceRef = getService(context,VisualStyleFactory.class);
        this.vmfFactoryP = getService(context,VisualMappingFunctionFactory.class, "(mapping.type=passthrough)");
        this.vmmServiceRef = getService(context,VisualMappingManager.class);
        this.vmfFactoryC = getService(context,VisualMappingFunctionFactory.class, "(mapping.type=continuous)");
        this.vmfFactoryD = getService(context,VisualMappingFunctionFactory.class, "(mapping.type=discrete)");
        this.adapter = getService(context,CySwingAppAdapter.class);
  
        menuaction = new ProjectMenuAction(cyApplicationManager, "InformationAnalyzer " + version, this);
        //SpanningTreeStartMenu panel = new SpanningTreeStartMenu(this);
        //registerService(context, panel, CytoPanelComponent.class, new Properties());
        CyTableFactory tableFactory = getService(context,CyTableFactory.class);
	MapTableToNetworkTablesTaskFactory mapTableToNetworkTablesTaskFactory = getService(context,MapTableToNetworkTablesTaskFactory.class);
	CreateTableTaskFactory createTableTaskFactory = new CreateTableTaskFactory(tableFactory,mapTableToNetworkTablesTaskFactory);
        Properties createTableTaskFactoryProps = new Properties();
	createTableTaskFactoryProps.setProperty("preferredMenu","Apps");
	createTableTaskFactoryProps.setProperty("title","Create Table");
	registerService(context,createTableTaskFactory,TaskFactory.class, createTableTaskFactoryProps);
        registerAllServices(context, menuaction, new Properties());
    }

    public CyServiceRegistrar getcyServiceRegistrar() {
        return cyServiceRegistrar;
    }

    public CyApplicationManager getcyApplicationManager() {
        return cyApplicationManager;
    }
    public CyNetworkManager getcyNetworkManager() {
        return networkManager;
    }
    public CyNetworkFactory getcyNetworkFactory() {
        return networkFactory;
    }
    public CySwingApplication getcytoscapeDesktopService() {
        return cyDesktopService;
    }

    public ProjectMenuAction getmenuaction() {
        return menuaction;
    }
    
    public static CyAppAdapter getCyAppAdapter(){
        return appAdapter;
    }

    CyNetworkManager getCyNetworkManager() {
        return networkManager;
    }

    CyNetworkFactory getCyNetworkFactory() {
        return networkFactory;
    }
}
