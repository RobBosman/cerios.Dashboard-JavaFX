package nl.valori.space.serialize.marshall;

import java.io.IOException;

import nl.valori.space.serialize.ClassInfo;

public interface Processor {

    public boolean process(Object object, ClassInfo classInfo, boolean isSpaceIdAllowed) throws IOException;
}