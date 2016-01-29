package nl.valori.space.serialize.testdata.race;

public class RacingTeam extends AbstractPersistentEntity {

    public enum Size {
	SMALL, MEDIUM, LARGE
    };

    private String name;
    private Size size;

    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
    }

    public Size getSize() {
	return size;
    }

    public void setSize(Size size) {
	this.size = size;
    }
}