package nl.valori.space.serialize.marshall;

import java.io.Closeable;

public interface ChainableMarshallWriter extends MarshallWriter, Closeable {

    public ChainableMarshallWriter setNextWriter(ChainableMarshallWriter nextWriter);
}