package com.axway.aws.loop;

import com.vordel.client.manager.wizard.VordelPage;
import org.eclipse.swt.widgets.Composite;

public class AWSLoopFilterPage extends VordelPage {

    public AWSLoopFilterPage() {
        super("AWSLoopPage");
        setTitle(resolve("AWS_LOOP_PAGE_TITLE"));
        setDescription(resolve("AWS_LOOP_PAGE_DESCRIPTION"));
        setPageComplete(true);
    }

    public String getHelpID() {
        return "circuitloop.help";
    }

    public boolean performFinish() {
        return true;
    }

    public void createControl(Composite parent) {
        Composite panel = render(parent, getClass().getResourceAsStream("aws_loop.xml"));
        setControl(panel);
        setPageComplete(true);
    }
}
