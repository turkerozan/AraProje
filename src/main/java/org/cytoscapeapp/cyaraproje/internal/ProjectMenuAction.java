package org.cytoscapeapp.cyaraproje.internal;

import java.awt.event.ActionEvent;
import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.swing.AbstractCyAction;
import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscapeapp.cyaraproje.internal.CyActivator;
import org.cytoscapeapp.cyaraproje.internal.ProjectCore;

/**
 * Creates a new menu item under Apps menu section.
 *
 */
public class ProjectMenuAction extends AbstractCyAction {

    public CyApplicationManager cyApplicationManager;
    public CySwingApplication cyDesktopService;
    public CyActivator cyactivator;

    public ProjectMenuAction(CyApplicationManager cyApplicationManager, final String menuTitle, CyActivator cyactivator) {
        super(menuTitle, cyApplicationManager, null, null);
        setPreferredMenu("Apps");
        this.cyactivator = cyactivator;
        this.cyApplicationManager = cyApplicationManager;
        this.cyDesktopService = cyactivator.getcytoscapeDesktopService();
    }

    public void actionPerformed(ActionEvent e) {
        System.out.println("Starting PROJE menu in control panel");
        ProjectCore spanningtreecore = new ProjectCore(cyactivator);
    }
}
