package nl.valori.dashboard.model;

import java.util.Set;

public abstract class PropertyHolder extends AbstractPersistentEntity {

    public abstract Set<Property> getProperties();

    public String getProperty(String name) {
	for (Property property : getProperties()) {
	    if (property.getName().equals(name)) {
		return property.getValue();
	    }
	}
	return null;
    }

    public float getFloatProperty(String name) {
	String value = getProperty(name);
	if (value == null) {
	    return Float.NaN;
	} else {
	    return Float.parseFloat(value);
	}
    }
}