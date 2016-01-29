package nl.valori.space.serialize.converters;

import java.io.IOException;
import java.lang.reflect.Array;

import nl.valori.space.serialize.ClassInfo;
import nl.valori.space.serialize.Converter;
import nl.valori.space.serialize.Token;
import nl.valori.space.serialize.marshall.MarshallWriter;
import nl.valori.space.serialize.marshall.Processor;

public class ArrayConverter extends Converter {

    @Override
    public Token getToken() {
	return Token.ARRAY;
    }

    @Override
    public Class<?>[] getSupportedClasses() {
	return new Class<?>[] { Object[].class };
    }

    @Override
    public boolean canConvert(Class<?> clazz) {
	return clazz.isArray();
    }

    @Override
    public boolean serialize(Object object, Class<?> serializableClass, ClassInfo classInfo, Processor processor,
	    MarshallWriter writer) throws IOException {
	Object[] array = (Object[]) object;
	ClassInfo elementClassInfo = classInfo.getArrayType();
	boolean hasSerializedData = false;
	for (Object element : array) {
	    hasSerializedData |= processor.process(element, elementClassInfo, elementClassInfo.isSpaceRef());
	}
	return hasSerializedData;
    }

    @Override
    public Object instantiate(Class<?> clazz, Object... args) {
	try {
	    if (args.length != 1) {
		throw new RuntimeException("Expected only one argument to instantiate an array of class "
			+ clazz.getName() + ", not '" + args + "'.");
	    }
	    int size;
	    if (args[0] instanceof Integer) {
		size = (Integer) args[0];
	    } else {
		size = Integer.parseInt(args[0].toString());
	    }
	    return Array.newInstance(clazz, size);
	} catch (SecurityException e) {
	    throw new RuntimeException(e);
	} catch (IllegalArgumentException e) {
	    throw new RuntimeException(e);
	}
    }
}