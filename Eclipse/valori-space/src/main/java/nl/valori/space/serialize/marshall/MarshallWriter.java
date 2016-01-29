package nl.valori.space.serialize.marshall;

import java.io.IOException;

import nl.valori.space.serialize.Token;

public interface MarshallWriter {

    public void beginToken(Token token) throws IOException;

    public void endToken(Token token) throws IOException;

    public void write(String data) throws IOException;
}