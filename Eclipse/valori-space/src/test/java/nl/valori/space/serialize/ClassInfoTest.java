package nl.valori.space.serialize;

import java.util.Map;

import junit.framework.TestCase;

public class ClassInfoTest extends TestCase {

    public void test() throws Exception {
	ClassInfo classInfo = new ClassInfo(Map.class,
		"java.util.Map<java.lang.String, java.util.Map<java.lang.Class<?>, java.lang.Object[]>>");
	ClassInfo[] genericTypes = classInfo.getGenericTypes();
	assertEquals("Number of ClassInfo objects is not correct.", 2, genericTypes.length);
	assertEquals("ClassInfo[0] is not correct.", String.class, genericTypes[0].getType());
	assertEquals("ClassInfo[1] is not correct.", Map.class, genericTypes[1].getType());

	assertEquals("ClassInfo[0].getGenericTypes() is not correct.", 0, genericTypes[0].getGenericTypes().length);

	ClassInfo[] genericTypes_1 = genericTypes[1].getGenericTypes();
	assertEquals("ClassInfo[1].getGenericTypes() is not correct.", 2, genericTypes_1.length);
	assertEquals("ClassInfo[1].getGenericTypes()[0] is not correct.", Class.class, genericTypes_1[0].getType());
	assertEquals("ClassInfo[1].getGenericTypes()[0] is not correct.", 0, genericTypes_1[0].getGenericTypes().length);
	assertEquals("ClassInfo[1].getGenericTypes()[1] is not correct.", Object[].class, genericTypes_1[1].getType());
	assertEquals("ClassInfo[1].getGenericTypes()[1] is not correct.", 0, genericTypes_1[1].getGenericTypes().length);
    }
}