package nl.valori.space.serialize;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Comparator;

import nl.valori.space.SpaceId;
import nl.valori.space.serialize.marshall.MarshallWriter;
import nl.valori.space.serialize.marshall.Processor;

public abstract class Converter {

    public static final Comparator<Converter> COMPARATOR = new Comparator<Converter>() {

	/**
	 * Compares two Converter instances. The sorting order of two converters is only significant if they support
	 * classes that are related to each other.
	 * <p>
	 * Let converter1 support class A and converter2 support class B and let class A be a super class of B. Since
	 * converter2 'knows' about derived class B, it can serialize objects of that class more efficiently than
	 * converter1, which only 'knows' about the base class A. Converter2 must be used for serialization and must
	 * therefore be sorted in front of converter2.
	 * 
	 * @param c1
	 * @param c2
	 * @return -1 if (c1 < c2), else 1
	 */
	public int compare(Converter c1, Converter c2) {
	    for (Class<?> clazz1 : c1.getSupportedClasses()) {
		for (Class<?> clazz2 : c2.getSupportedClasses()) {
		    // If clazz1 is a base class of clazz2...
		    if (ClassInfo.COMPARATOR.compare(clazz1, clazz2) > 0) {
			// ...then return 1.
			return 1;
		    }
		}
	    }
	    // Don't return 0, because that makes a SortedSet think that c1 and c2 are equal and discard one of them.
	    return -1;
	}
    };

    private static final Class<?>[] NON_REFERABLE_CLASSES = { SpaceId.class, Boolean.class, Character.class,
	    Number.class, String.class };

    public static boolean isReferable(Class<?> clazz) {
	if ((clazz.isPrimitive()) || (clazz.isEnum())) {
	    return false;
	}
	for (Class<?> nonReferableClass : NON_REFERABLE_CLASSES) {
	    if (nonReferableClass.isAssignableFrom(clazz)) {
		return false;
	    }
	}
	return true;
    }

    public abstract Token getToken();

    public abstract Class<?>[] getSupportedClasses();

    public abstract boolean serialize(Object object, Class<?> serializableClass, ClassInfo classInfo,
	    Processor processor, MarshallWriter writer) throws IOException;

    public boolean canConvert(Class<?> clazz) {
	for (Class<?> targetClass : getSupportedClasses()) {
	    if (targetClass.isAssignableFrom(clazz)) {
		return true;
	    }
	}
	return false;
    }

    public Object instantiate(Class<?> clazz, Object... args) {
	try {
	    Class<?>[] parameterTypes = new Class<?>[args.length];
	    for (int i = 0; i < args.length; i++) {
		parameterTypes[i] = args[i].getClass();
	    }
	    Constructor<?> constructor = clazz.getConstructor(parameterTypes);
	    return constructor.newInstance(args);
	} catch (SecurityException e) {
	    throw new RuntimeException(e);
	} catch (NoSuchMethodException e) {
	    throw new RuntimeException(e);
	} catch (IllegalArgumentException e) {
	    throw new RuntimeException(e);
	} catch (InstantiationException e) {
	    throw new RuntimeException(e);
	} catch (IllegalAccessException e) {
	    throw new RuntimeException(e);
	} catch (InvocationTargetException e) {
	    throw new RuntimeException(e);
	}
    }
}