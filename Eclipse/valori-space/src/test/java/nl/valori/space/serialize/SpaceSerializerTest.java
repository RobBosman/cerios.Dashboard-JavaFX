package nl.valori.space.serialize;

import java.io.StringReader;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;

import junit.framework.TestCase;
import nl.valori.space.Space;
import nl.valori.space.SpaceId;
import nl.valori.space.SpaceSet;
import nl.valori.space.serialize.marshall.ChainableMarshallWriter;
import nl.valori.space.serialize.marshall.Marshaller;
import nl.valori.space.serialize.marshall.writers.DefaultWriter;
import nl.valori.space.serialize.marshall.writers.IndentingWriter;
import nl.valori.space.serialize.testdata.race.ExtendedRace;
import nl.valori.space.serialize.testdata.race.Position;
import nl.valori.space.serialize.testdata.race.Race;
import nl.valori.space.serialize.testdata.race.RaceTrack;
import nl.valori.space.serialize.testdata.race.RacingTeam;
import nl.valori.space.serialize.unmarshall.Unmarshaller;

import com.thoughtworks.xstream.XStream;

public class SpaceSerializerTest extends TestCase {

    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss,SSS");

    private Space space;
    private Race race;
    private RaceTrack track1;
    private RaceTrack track2;
    private RacingTeam teamA;
    private RacingTeam teamB;

    @Override
    protected void setUp() throws Exception {
	super.setUp();

	space = Space.getInstance();
	space.clear();

	// Create objects
	race = new ExtendedRace();
	race.setId(101L);
	race.setName("Volvo Ocean Race");
	race.getSponsors().add("BCG- Boston Consulting Group");
	race.getSponsors().add("Wallenius Wilhelmsen Logistics");
	race.getSponsors().add("Thrane & Thrane");
	race.getSponsors().add("Stratos");
	race.getSponsors().add("Inmarsat");

	track1 = new RaceTrack();
	track1.setId(101L);
	track1.setName("track_1");
	track1.setTrackData("map of 'track_1'");
	track1.setTransientStuff("THIS IS TRANSIENT STUFF'");
	track2 = new RaceTrack();
	track2.setId(102L);
	track2.setName("track_2");
	track2.setTrackData("map of 'track_2'");

	teamA = new RacingTeam();
	teamA.setId(101L);
	teamA.setName("Ericsson 4");
	teamA.setSize(RacingTeam.Size.LARGE);
	teamB = new RacingTeam();
	teamB.setId(102L);
	teamB.setName("Telefonica Blue");
	teamB.setSize(RacingTeam.Size.MEDIUM);

	// Create object associations
	track1.setRace(race);
	race.getRaceTracks().add(track1);
	track2.setRace(race);
	race.getRaceTracks().add(track2);
	// race.setParticipants(Arrays.asList(teamA, teamB));
	race.getParticipants().clear();
	race.getParticipants().addAll(Arrays.asList(teamA, teamB));

	Date timestampT = DATE_FORMAT.parse("01-01-2009 12:34:56,789");
        Date timestampU = DATE_FORMAT.parse("02-01-2009 12:34:43,210");
	Date timestampV = DATE_FORMAT.parse("03-01-2009 13:57:21,301");

	track1.getAchievementMap().put(teamA, new Position[] {
	//
		newLocation(101L, timestampT, "location of team A track 1 at timestamp T"), //
		newLocation(102L, timestampU, "location of team A track 1 at timestamp U"), //
		newLocation(103L, timestampV, "location of team A track 1 at timestamp V") });
	track1.getAchievementMap().put(teamB, new Position[] {
	//
		newLocation(104L, timestampT, "location of team B track 1 at timestamp T"), //
		newLocation(105L, timestampU, "location of team B track 1 at timestamp U"), //
		newLocation(106L, timestampV, "location of team B track 1 at timestamp V") });
	track2.getAchievementMap().put(teamA, new Position[] {
	//
		newLocation(107L, timestampT, "location of team A track 2 at timestamp T"), //
		newLocation(108L, timestampU, "location of team A track 2 at timestamp U"), //
		newLocation(109L, timestampV, "location of team A track 2 at timestamp V") });
    }

    private Position newLocation(Long id, Date timestamp, String data) {
	Position location = new Position();
	location.setId(id);
	location.setTimestamp(timestamp);
	location.setLocation(data);
	return location;
    }

    public void test() throws Exception {
	XStream xstream = new XStream();

	// Create a test reference of the object model.
	String refData = xstream.toXML(race);

	// Serialize
	StringWriter stringWriter = new StringWriter();
	ChainableMarshallWriter chainableWriter = new DefaultWriter(stringWriter);
	chainableWriter = new IndentingWriter(chainableWriter);
	new Marshaller().marshall(chainableWriter, track1, track2);

	String serializedData = stringWriter.toString();
	System.out.println(serializedData);
	System.out.println();

	String testData = xstream.toXML(race);
	if (!testData.equals(refData)) {
	    System.out.println(refData);
	    System.out.println();
	    System.out.println(testData);
	}
	assertEquals("Object model has changed during serialization.", refData, testData);

	System.out.println("Space content before clearing:\n" + space);
	System.gc();
	System.out.println("Space content before clearing, but after garbage collection:\n" + space);

	space.clear();

	// Deserialize
	StringReader stringReader = new StringReader(stringWriter.toString());
	Set<SpaceId> deserializedSpaceIds = new Unmarshaller().unmarshall(stringReader);

	// Find the 'race' root object.
	Race deserializedRace = null;
	for (SpaceId spaceId : deserializedSpaceIds) {
	    Object object = space.read(spaceId);
	    if (object instanceof Race) {
		deserializedRace = (Race) object;
	    }
	}

	assertTrue("Root object was not deserialized properly.", deserializedRace != null);
	assertTrue("Deserialize == serialized.", race != deserializedRace);

	testData = xstream.toXML(deserializedRace);

	assertTrue("Deserialized result contains wrong class(es).", !testData.equals(refData));

	refData = refData.replace(ExtendedRace.class.getName(), Race.class.getName());
	refData = refData.replace(" class=\"nl.valori.space.serialize.testdata.race.Race\"", "");
	refData = refData.replace("  <isEnabled>false</isEnabled>\n", "");

	if (!testData.equals(refData)) {
	    System.out.println(refData);
	    System.out.println();
	    System.out.println(testData);
	}
	String[] refDataLines = refData.split("\n");
	String[] testDataLines = testData.split("\n");
	int minLength = Math.min(refDataLines.length, testDataLines.length);
	for (int i = 0; i < minLength; i++) {
	    assertEquals("Difference after serialize+deserialize at line " + i, refDataLines[i], testDataLines[i]);
	}

	// Test Space content.
	SpaceSet<RacingTeam> participants = (SpaceSet<RacingTeam>) deserializedRace.getParticipants();
	Iterator<RacingTeam> participantIterA = participants.iterator();
	Iterator<RacingTeam> participantIterB = participants.iterator();
	RacingTeam team1_beforeReset = participantIterA.next();

	System.out.println("Space content after unmarshalling:\n" + space);
	deserializedSpaceIds.clear();
	System.gc();
	System.out.println("Space content after garbage collection:\n" + space);

	RacingTeam team1_afterReset = participantIterB.next();
	assertSame("Garbage collection cleared the Space too good...", team1_beforeReset, team1_afterReset);
    }
}
