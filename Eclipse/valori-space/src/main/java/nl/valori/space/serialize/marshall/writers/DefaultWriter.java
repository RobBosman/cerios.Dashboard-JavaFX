package nl.valori.space.serialize.marshall.writers;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.Stack;

import nl.valori.space.serialize.Token;
import nl.valori.space.serialize.marshall.ChainableMarshallWriter;

public class DefaultWriter implements ChainableMarshallWriter {

    private Writer writer;
    private Stack<Token> tokenStack;

    public DefaultWriter(Writer writer) {
	this.writer = writer;
	this.tokenStack = new Stack<Token>();
    }

    public DefaultWriter(OutputStream outputStream, Charset charset) {
	this(new OutputStreamWriter(outputStream, charset));
    }

    public ChainableMarshallWriter setNextWriter(ChainableMarshallWriter nextWriter) {
	throw new UnsupportedOperationException(
		"This is an end point of a chain of MarschallWriters. Setting a 'nextWriter' is not supported.");
    }

    public void beginToken(Token token) throws IOException {
	writer.write(token.getBeginTag());
	tokenStack.push(token);
    }

    public void endToken(Token token) throws IOException {
	Token poppedToken = tokenStack.pop();
	if (poppedToken != token) {
	    throw new RuntimeException("Expected end of token " + poppedToken + ", not " + token + ".");
	}
	writer.write(token.getEndTag());
    }

    public void write(String data) throws IOException {
	if (!tokenStack.isEmpty()) {
	    data = tokenStack.peek().escape(data);
	}
	writer.write(data);
    }

    public void close() throws IOException {
	writer.close();
    }
}