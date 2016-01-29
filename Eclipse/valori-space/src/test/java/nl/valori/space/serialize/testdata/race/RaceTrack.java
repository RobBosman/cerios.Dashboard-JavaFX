package nl.valori.space.serialize.testdata.race;

import java.util.HashMap;
import java.util.Map;

import nl.valori.space.SpaceRef;

public class RaceTrack {

    private long id;
    private String name;
    private Race race;
    private SpaceRef<String> trackData = new SpaceRef<String>();
    private transient String transientStuff;
    private Map<RacingTeam, Position[]> achievementMap = new HashMap<RacingTeam, Position[]>();

    public long getId() {
	return id;
    }

    public void setId(long id) {
	this.id = id;
    }

    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
    }

    public Race getRace() {
	return race;
    }

    public void setRace(Race race) {
	this.race = race;
    }

    public String getTrackData() {
	return trackData.get();
    }

    public void setTrackData(String trackData) {
	this.trackData.set(trackData);
    }

    public String getTransientStuff() {
	return transientStuff;
    }

    public void setTransientStuff(String transientStuff) {
	this.transientStuff = transientStuff;
    }

    public Map<RacingTeam, Position[]> getAchievementMap() {
	return achievementMap;
    }

    @Override
    public int hashCode() {
	return (int) id;
    }

    @Override
    public boolean equals(Object obj) {
	if (obj instanceof RaceTrack) {
	    RaceTrack other = (RaceTrack) obj;
	    if (this.id == 0) {
		return other.id == 0;
	    } else {
		return this.id == other.id;
	    }
	} else {
	    return false;
	}
    }

    @Override
    public String toString() {
	return "RaceTrack{ id = " + id + " }";
    }
}