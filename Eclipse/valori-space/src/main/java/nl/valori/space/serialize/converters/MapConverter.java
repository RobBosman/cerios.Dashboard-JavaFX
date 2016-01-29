package nl.valori.space.serialize.converters;

import java.io.IOException;
import java.util.Map;

import nl.valori.space.serialize.ClassInfo;
import nl.valori.space.serialize.Converter;
import nl.valori.space.serialize.Token;
import nl.valori.space.serialize.marshall.MarshallWriter;
import nl.valori.space.serialize.marshall.Processor;

public class MapConverter extends Converter {

    @Override
    public Token getToken() {
	return Token.MAP;
    }

    @Override
    public Class<?>[] getSupportedClasses() {
	return new Class<?>[] { Map.class };
    }

    @Override
    public boolean serialize(Object object, Class<?> serializableClass, ClassInfo classInfo, Processor processor,
	    MarshallWriter writer) throws IOException {
	Map<?, ?> map = (Map<?, ?>) object;
	ClassInfo[] entryClassInfo = classInfo.getGenericTypes();
	if (entryClassInfo.length != 2) {
	    throw new RuntimeException("Expected ClassInfo with 2 generic types.");
	}
	boolean hasSerializedData = false;
	for (Map.Entry<?, ?> entry : map.entrySet()) {
	    if (entry.getValue() != null) {
		writer.beginToken(Token.KEY);
		processor.process(entry.getKey(), entryClassInfo[0], entryClassInfo[0].isSpaceRef());
		writer.endToken(Token.KEY);
		processor.process(entry.getValue(), entryClassInfo[1], entryClassInfo[1].isSpaceRef());
		hasSerializedData = true;
	    }
	}
	return hasSerializedData;
    }
}