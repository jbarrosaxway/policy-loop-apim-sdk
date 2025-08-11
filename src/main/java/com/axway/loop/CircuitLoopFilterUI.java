package com.axway.loop;

import com.vordel.client.manager.filter.DefaultGUIFilter;
import com.vordel.client.manager.filter.log.LogPage;
import com.vordel.client.manager.wizard.VordelPage;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import com.vordel.client.manager.Images;

import java.util.Vector;

public class CircuitLoopFilterUI extends DefaultGUIFilter {
    public Vector<VordelPage> getPropertyPages() {
        Vector<VordelPage> pages = new Vector<>();
        pages.add(new CircuitLoopFilterPage());
        pages.add(createLogPage());
        return pages;
    }

    public LogPage createLogPage() {
        return new LogPage();
    }

    public String[] getCategories() {
        return new String[] { resolve("FILTER_GROUP_CIRCUIT_LOOP") };
    }

    private static final String IMAGE_KEY = "filter_small"; // Usando um ícone padrão

    public String getSmallIconId() {
        return IMAGE_KEY;
    }

    public Image getSmallImage() {
        return Images.getImageRegistry().get(getSmallIconId());
    }

    public ImageDescriptor getSmallIcon() {
        return Images.getImageDescriptor(getSmallIconId());
    }

    public String getTypeName() {
        return resolve("CIRCUIT_LOOP_FILTER_NAME");
    }
}
