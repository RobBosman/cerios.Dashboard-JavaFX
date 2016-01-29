package nl.valori.dashboard.model;

import java.util.HashSet;
import java.util.Set;

public class GuiLayout extends PropertyHolder {

    private String name;
    private Set<GuiComponent> guiComponents;
    private Set<Property> properties;

    public String getName() {
	return this.name;
    }

    public void setName(String name) {
	this.name = name;
    }

    public Set<GuiComponent> getGuiComponents() {
	if (guiComponents == null) {
	    guiComponents = new HashSet<GuiComponent>();
	}
	return guiComponents;
    }

    public void setGuiComponents(Set<GuiComponent> guiComponents) {
	this.guiComponents = guiComponents;
    }

    public Set<Property> getProperties() {
	if (properties == null) {
	    properties = new HashSet<Property>();
	}
	return properties;
    }

    public void setProperties(Set<Property> properties) {
	this.properties = properties;
    }

    @Override
    public String toString() {
	return " name=" + name;
    }
}