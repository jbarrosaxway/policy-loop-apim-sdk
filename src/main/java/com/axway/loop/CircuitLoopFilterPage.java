package com.axway.loop;

import com.vordel.client.manager.wizard.VordelPage;
import org.eclipse.swt.widgets.Composite;

public class CircuitLoopFilterPage extends VordelPage {

    public CircuitLoopFilterPage() {
        super("CircuitLoopPage");
        setTitle(resolve("CIRCUIT_LOOP_PAGE_TITLE"));
        setDescription(resolve("CIRCUIT_LOOP_PAGE_DESCRIPTION"));
        setPageComplete(true);
    }

    public String getHelpID() {
        return "circuitloop.help";
    }

    public boolean performFinish() {
        return true;
    }

    public void createControl(Composite parent) {
        Composite panel = render(parent, getClass().getResourceAsStream("circuit_loop.xml"));
        setControl(panel);
        setPageComplete(true);
    }
}
