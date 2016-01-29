package nl.valori.space.serialize.testdata.race;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import nl.valori.space.SpaceSet;
import nl.valori.space.annotations.SerializableEntity;

@SerializableEntity
public class Race extends AbstractPersistentEntity {

    private String name;
    private Set<RaceTrack> raceTracks = new HashSet<RaceTrack>();
    private Collection<RacingTeam> participants = new SpaceSet<RacingTeam>();
    private Collection<String> sponsors = new ArrayList<String>();

    protected transient Collection<String> dummy;

    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
    }

    public Set<RaceTrack> getRaceTracks() {
	return raceTracks;
    }

    public void setRaceTracks(Set<RaceTrack> raceTracks) {
	this.raceTracks = raceTracks;
    }

    public Collection<RacingTeam> getParticipants() {
	return participants;
    }

    public Collection<String> getSponsors() {
	return sponsors;
    }

    public Collection<String> getDummy() {
	return dummy;
    }
}
