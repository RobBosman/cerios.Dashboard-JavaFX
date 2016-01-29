package nl.valori.space.serialize.testdata.race;

import java.util.Date;

public class Position extends AbstractPersistentEntity {

    private Date timestamp;
    private String location;

    public Date getTimestamp() {
	return timestamp;
    }

    public void setTimestamp(Date timestamp) {
	this.timestamp = timestamp;
    }

    public String getLocation() {
	return location;
    }

    public void setLocation(String location) {
	this.location = location;
    }
}