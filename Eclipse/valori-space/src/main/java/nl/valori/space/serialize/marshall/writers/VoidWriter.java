package nl.valori.space.serialize.marshall.writers;

import java.io.IOException;

import nl.valori.space.serialize.Token;
import nl.valori.space.serialize.marshall.MarshallWriter;

public class VoidWriter implements MarshallWriter {

    public static final MarshallWriter INSTANCE = new VoidWriter();

    public void beginToken(Token token) throws IOException {
	// Do nothing
    }

    public void endToken(Token token) throws IOException {
	// Do nothing
    }

    public void write(String data) throws IOException {
	// Do nothing
    }

    public void close() throws IOException {
	// Do nothing
    }
}