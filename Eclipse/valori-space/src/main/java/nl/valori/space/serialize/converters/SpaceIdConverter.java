package nl.valori.space.serialize.converters;

import java.io.IOException;

import nl.valori.space.SpaceId;
import nl.valori.space.serialize.ClassInfo;
import nl.valori.space.serialize.Converter;
import nl.valori.space.serialize.Token;
import nl.valori.space.serialize.marshall.MarshallWriter;
import nl.valori.space.serialize.marshall.Processor;

public class SpaceIdConverter extends Converter {

    @Override
    public Token getToken() {
	return Token.SPACE_ID;
    }

    @Override
    public Class<?>[] getSupportedClasses() {
	return new Class<?>[] { SpaceId.class };
    }

    @Override
    public boolean serialize(Object object, Class<?> serializableClass, ClassInfo classInfo, Processor processor,
	    MarshallWriter writer) throws IOException {
	SpaceId spaceId = (SpaceId) object;
	writer.write(spaceId.toString());
	return true;
    }
}