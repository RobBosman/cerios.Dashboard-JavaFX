package nl.valori.space;

import java.util.Date;

import junit.framework.TestCase;

public class SpaceTest extends TestCase {

    private Space space;

    protected void setUp() throws Exception {
	super.setUp();
	space = Space.getInstance();
	space.clear();
    }

    public void testWeakness() throws Exception {
	Object x = new Long(42);
	SpaceId spaceId1 = space.put(x);

	// Create new instance of SpaceId that refers to the same spaceObject.
	SpaceId spaceId2 = new SpaceId(spaceId1);
	assertNotSame("SpaceId's are the same.", spaceId1, spaceId2);
	assertEquals("SpaceId's are not equal.", spaceId1, spaceId2);

	System.gc();

	assertTrue("SpaceObject disappeared.", space.read(spaceId1) != null);
	assertTrue("SpaceObject disappeared.", space.read(spaceId2) != null);

	System.gc();

	assertTrue("SpaceObject disappeared.", space.read(spaceId1) != null);
	assertTrue("SpaceObject disappeared.", space.read(spaceId2) != null);

	// Now remove the only strong reference.
	spaceId1 = null;
	System.gc();

	try {
	    space.read(spaceId2);
	    fail("SpaceObject is still alive.");
	} catch (RuntimeException e) {
	    // Expect an exception to be thrown.
	}
    }

    private class SelfReference {
	private SpaceRef<SelfReference> self;

	public SelfReference() {
	    self = new SpaceRef<SelfReference>();
	    self.set(this);
	}
    }

    public void testWeaknessCyclic() throws Exception {
	Object x = new SelfReference();
	SpaceId spaceId1 = space.put(x);

	// Create new instance of SpaceId that refers to the same spaceObject.
	SpaceId spaceId2 = new SpaceId(spaceId1);
	assertNotSame("SpaceId's are the same.", spaceId1, spaceId2);
	assertEquals("SpaceId's are not equal.", spaceId1, spaceId2);

	System.gc();

	assertTrue("SpaceObject disappeared.", space.read(spaceId1) != null);
	assertTrue("SpaceObject disappeared.", space.read(spaceId2) != null);

	System.gc();

	assertTrue("SpaceObject disappeared.", space.read(spaceId1) != null);
	assertTrue("SpaceObject disappeared.", space.read(spaceId2) != null);

	// Now remove the only strong reference.
	spaceId1 = null;
	System.gc();

	// TODO - assertTrue("Circular referencing SpaceObject is still alive.", space.read(spaceId2) == null);
    }

    public void testToString() throws Exception {
	SpaceId[] ids = new SpaceId[4];
	ids[0] = space.put(new Date(1253619313940L));
	ids[1] = space.put(1.23D);
	ids[2] = space.put(4.56F);
	ids[3] = space.put("XYZ");
	space.take(ids[1]);

	StringBuilder ref = new StringBuilder();
	ref.append("  `1`(java.util.Date)\tTue Sep 22 13:35:13 CEST 2009");
	ref.append("\n");
	ref.append("  `3`(java.lang.Float)\t4.56");
	ref.append("\n");
	ref.append("  `4`(java.lang.String)\tXYZ");
	assertEquals("Space.toString() is not correct.", ref.toString(), space.toString());
    }
}
