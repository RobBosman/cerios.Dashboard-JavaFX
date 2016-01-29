package nl.valori.space;

import junit.framework.TestCase;

public class SpaceIdTest extends TestCase {

    public void test() throws Exception {
	String id = "1";
	boolean isTemporary = false;
	String spaceIdToString = id;
	SpaceId spaceId = SpaceId.parse(spaceIdToString);
	assertEquals("SpaceId.getId() is not correct.", id, spaceId.getId());
	assertEquals("SpaceId.isTemporary() is not correct.", isTemporary, spaceId.isTemporary());
	assertEquals("SpaceId.toString() is not correct.", spaceIdToString, spaceId.toString());

	id = "1";
	isTemporary = true;
	spaceIdToString = "-" + id;
	spaceId = SpaceId.parse(spaceIdToString);
	assertEquals("SpaceId.getId() is not correct.", id, spaceId.getId());
	assertEquals("SpaceId.isTemporary() is not correct.", isTemporary, spaceId.isTemporary());
	assertEquals("SpaceId.toString() is not correct.", spaceIdToString, spaceId.toString());

	id = "-1";
	isTemporary = false;
	spaceIdToString = "\\" + id;
	spaceId = new SpaceId(id, isTemporary);
	assertEquals("SpaceId.getId() is not correct.", id, spaceId.getId());
	assertEquals("SpaceId.isTemporary() is not correct.", isTemporary, spaceId.isTemporary());
	assertEquals("SpaceId.toString() is not correct.", spaceIdToString, spaceId.toString());

	id = "-1";
	isTemporary = true;
	spaceIdToString = "-" + id;
	spaceId = new SpaceId(id, isTemporary);
	assertEquals("SpaceId.getId() is not correct.", id, spaceId.getId());
	assertEquals("SpaceId.isTemporary() is not correct.", isTemporary, spaceId.isTemporary());
	assertEquals("SpaceId.toString() is not correct.", spaceIdToString, spaceId.toString());
    }
}