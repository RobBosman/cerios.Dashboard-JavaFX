package nl.valori.space;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import junit.framework.TestCase;

public class SpaceCollectionTest extends TestCase {

    public void testSpaceSet_set() throws Exception {
	List<Integer> list = Arrays.asList(3, 2, 1, 3, 2, 1);
	Set<Integer> set = new HashSet<Integer>(list);
	// Note that the Set doesn't add duplicate values and sorts its items differently.
	assertTrue("Testcase incorrect", !set.toString().equals(list.toString()));

	SpaceSet<Integer> spaceSet = new SpaceSet<Integer>();
	spaceSet.set(set);
	assertEquals("SpaceSet.set()", set, spaceSet);
	assertTrue("SpaceSet.set()", !list.equals(spaceSet));
	assertTrue("SpaceSet.set()", !spaceSet.equals(list));
    }

    public void testSpaceSet_addAll() throws Exception {
	List<Integer> list = Arrays.asList(3, 2, 1, 3, 2, 1);
	Set<Integer> set = new HashSet<Integer>(list);
	// Note that the Set doesn't add duplicate values and sorts its items differently.
	assertTrue("Testcase incorrect", !set.toString().equals(list.toString()));

	SpaceSet<Integer> spaceSet = new SpaceSet<Integer>();
	spaceSet.addAll(set);
	assertEquals("SpaceSet.addAll()", set, spaceSet);

	spaceSet = new SpaceSet<Integer>();
	spaceSet.addAll(list);
	assertEquals("SpaceSet.addAll()", set, spaceSet);
    }
}