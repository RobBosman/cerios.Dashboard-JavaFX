package nl.valori.space.serialize.testdata.race;

public class ExtendedRace extends Race {

    private boolean isEnabled;

    public void setEnabled(boolean isEnabled) {
	this.isEnabled = isEnabled;
    }

    public boolean isEnabled() {
	return isEnabled;
    }
}