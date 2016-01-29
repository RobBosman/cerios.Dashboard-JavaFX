package nl.valori.space;

import java.util.Set;

import junit.framework.TestCase;

public class IdentitySetTest extends TestCase {

    public void test() throws Exception {
	String ref1 = new String("X");
	String ref2 = new String("X");
	String ref3 = new String("X");
	assertEquals("Test set is not correct.", ref1, ref2);
	assertNotSame("Test set is not correct.", ref1, ref2);
	assertEquals("Test set is not correct.", ref1, ref2);
	assertNotSame("Test set is not correct.", ref1, ref3);

	Set<String> set = new IdentitySet<String>();
	set.add(ref1);
	set.add(ref1);
	set.add(ref1);
	assertEquals("IdentitySet.add() or size() is not correct.", 1, set.size());
	set.add(ref2);
	set.add(ref3);
	assertEquals("IdentitySet.add() is not correct.", 3, set.size());
	set.remove(ref1);
	assertEquals("IdentitySet.remove() is not correct.", 2, set.size());
    }
}
