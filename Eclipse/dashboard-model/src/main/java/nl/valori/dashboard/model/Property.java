package nl.valori.dashboard.model;

public class Property extends AbstractPersistentEntity {

    private String name;
    private String value;

    public String getName() {
	return this.name;
    }

    public void setName(String name) {
	this.name = name;
    }

    public String getValue() {
	return this.value;
    }

    public void setValue(String value) {
	this.value = value;
    }

    @Override
    public String toString() {
	return " name=" + name + " value=" + value;
    }
}