package nl.valori.dashboard.model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class GuiComponent extends PropertyHolder {

    public enum Type {
	BUSINESS_CASE_GRAPH(0), ADD_SUBTRACT_GRAPH(1), PROGRESS_BAR(2), RISKS_VIEW(3), SPEED_O_METER(4), SPIDER_GRAPH(5), TRAFFIC_LIGHT(
		6);

	private int code;

	private Type(int code) {
	    this.code = code;
	    assert (typeEnumMap.put(code, this) == null);
	}

	public int getCode() {
	    return code;
	}

	public static Type valueOf(int code) {
	    return typeEnumMap.get(code);
	}
    }

    private static final Map<Integer, Type> typeEnumMap = new HashMap<Integer, Type>();

    private String type;
    private String title;
    private Set<GuiElement> guiElements;
    private String layout;
    private Set<Property> properties;

    public Set<Property> getProperties() {
	if (properties == null) {
	    properties = new HashSet<Property>();
	}
	return properties;
    }

    public void setProperties(Set<Property> properties) {
	this.properties = properties;
    }

    public String getType() {
	return this.type;
    }

    public void setType(String type) {
	this.type = type;
    }

    public String getTitle() {
	return this.title;
    }

    public void setTitle(String title) {
	this.title = title;
    }

    public Set<GuiElement> getGuiElements() {
	if (guiElements == null) {
	    guiElements = new HashSet<GuiElement>();
	}
	return guiElements;
    }

    public void setGuiElements(Set<GuiElement> guiElements) {
	this.guiElements = guiElements;
    }

    public String getLayout() {
	return this.layout;
    }

    public void setLayout(String layout) {
	this.layout = layout;
    }

    @Override
    public String toString() {
	return " type=" + type + " title=" + title + " layout=" + layout;
    }

    public GuiElement getGuiElement(String represents) {
	for (GuiElement guiElement : getGuiElements()) {
	    if (guiElement.getRepresents().equals(represents)) {
		return guiElement;
	    }
	}
	return null;
    }
}