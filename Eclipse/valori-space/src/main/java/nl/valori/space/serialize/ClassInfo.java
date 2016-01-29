package nl.valori.space.serialize;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import nl.valori.space.SpaceRef;

public class ClassInfo {

    public static final Comparator<Class<?>> COMPARATOR = new Comparator<Class<?>>() {

	/**
	 * Compares two Classes. Class c1 < c2 if it is 'more specific' than the class c2. I.e. if c1 is derived from
	 * c2, or c2 is a base class of c1, or c2.isAssignableFrom(c1).
	 * 
	 * @param c1
	 * @param c2
	 * @return -1 if (c1 < c2), else 1
	 */
	public int compare(Class<?> c1, Class<?> c2) {
	    if (c1.equals(c2)) {
		return 0;
	    } else if (c1.isAssignableFrom(c2)) {
		return 1;
	    } else {
		return -1;
	    }
	}
    };

    private static final Set<String> defaultPackageAliases;

    static {
	defaultPackageAliases = new HashSet<String>();
	defaultPackageAliases.add("java.lang");
	defaultPackageAliases.add("java.util");

	defaultPackageAliases.add("nl.valori.space");
    }

    public static void addPackageAliase(String packageName) {
	defaultPackageAliases.add(packageName);
    }

    public static Class<?> classForName(String className) {
	try {
	    return instantiateClass(className);
	} catch (ClassNotFoundException e) {
	    for (String defaultPackage : defaultPackageAliases) {
		try {
		    return instantiateClass(defaultPackage + "." + className);
		} catch (ClassNotFoundException e1) {
		    // Ignore; just try the next package.
		}
	    }
	    throw new RuntimeException(e);
	}
    }

    public static String asString(Class<?> serializableClass, ClassInfo classInfo) {
	// Compose the full result text.
	String result = serializableClass.getName();
	if (classInfo != null) {
	    int index = classInfo.genericString.indexOf("<");
	    if (index >= 0) {
		result += classInfo.genericString.substring(index);
	    }
	}
	// Remove default package names.
	for (String defaultPackage : defaultPackageAliases) {
	    result = result.replace(defaultPackage + ".", "");
	}
	return result;
    }

    private static Class<?> instantiateClass(String className) throws ClassNotFoundException {
	if (className.endsWith("[]")) {
	    return Class.forName("[L" + className.replace("[]", ";"));
	} else {
	    return Class.forName(className);
	}
    }

    private Class<?> clazz;
    private String genericString;
    private ClassInfo[] genericTypes;

    public ClassInfo(Class<?> clazz, String genericString) {
	if (genericString == null) {
	    genericString = clazz.getName();
	} else {
	    // Remove the modifier.
	    genericString = genericString.replaceFirst("^private|^protected|^public", "").trim();
	    // Remove redundant type information.
	    genericString = genericString.replace("<?>", "");
	    // Strip-off the field name.
	    genericString = genericString.replace(", ", ",");
	    int index = genericString.lastIndexOf(" ");
	    if (index >= 0) {
		genericString = genericString.substring(0, index);
	    }
	}
	this.genericString = genericString;

	if (clazz == null) {
	    clazz = getClassFromGenericString();
	}
	this.clazz = clazz;
    }

    private Class<?> getClassFromGenericString() {
	String className = genericString;
	int index = className.indexOf("<");
	if (index >= 0) {
	    className = className.substring(0, index);
	}
	return classForName(className);
    }

    public boolean isSpaceRef() {
	return SpaceRef.class.isAssignableFrom(clazz);
    }

    public Class<?> getType() {
	return clazz;
    }

    public ClassInfo getArrayType() {
	if (clazz.isArray()) {
	    return new ClassInfo(null, genericString.replace("[]", ""));
	} else {
	    return null;
	}
    }

    public ClassInfo[] getGenericTypes() {
	if (genericTypes == null) {
	    List<String> subTypes = new ArrayList<String>();
	    int beginIndex = genericString.indexOf("<");
	    if (beginIndex >= 0) {
		// Map<String, Class<?>>
		beginIndex++;
		int endIndex = genericString.lastIndexOf(">");
		int level = 0;
		for (int i = beginIndex; i <= endIndex - 1; i++) {
		    char c = genericString.charAt(i);
		    if (c == '<') {
			level++;
		    } else if (c == '>') {
			level--;
		    } else if (c == ',') {
			if (level == 0) {
			    subTypes.add(genericString.substring(beginIndex, i));
			    beginIndex = i + 1;
			}
		    }
		}
		subTypes.add(genericString.substring(beginIndex, endIndex));
	    }

	    genericTypes = new ClassInfo[subTypes.size()];
	    for (int i = 0; i < subTypes.size(); i++) {
		String subType = subTypes.get(i);
		String className = subType;
		int index = className.indexOf("<");
		if (index >= 0) {
		    className = className.substring(0, index);
		}
		Class<?> clazz = classForName(className);
		genericTypes[i] = new ClassInfo(clazz, subType);
	    }
	}
	return genericTypes;
    }

    @Override
    public int hashCode() {
	return genericString.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
	boolean isEqual = false;
	if (obj == this) {
	    isEqual = true;
	} else if (obj instanceof ClassInfo) {
	    isEqual = this.genericString.equals(((ClassInfo) obj).genericString);
	} else {
	    isEqual = false;
	}
	return isEqual;
    }

    @Override
    public String toString() {
	return genericString;
    }
}