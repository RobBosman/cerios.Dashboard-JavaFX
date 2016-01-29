package nl.valori.space.serialize.marshall.writers;

import java.io.IOException;
import java.util.Stack;

import nl.valori.space.serialize.Token;
import nl.valori.space.serialize.marshall.ChainableMarshallWriter;

public class IndentingWriter implements ChainableMarshallWriter {

    private static final String TAB = "  ";

    private ChainableMarshallWriter nextWriter;
    private StringBuilder indent;
    private Stack<Token> tokenStack;
    private Token lastEndedToken;

    public IndentingWriter(ChainableMarshallWriter nextWriter) {
	this.nextWriter = nextWriter;
	this.indent = new StringBuilder("\n");
	this.tokenStack = new Stack<Token>();
    }

    public ChainableMarshallWriter setNextWriter(ChainableMarshallWriter nextWriter) {
	this.nextWriter = nextWriter;
	return this;
    }

    public void beginToken(Token token) throws IOException {
	// Ensure that new SpaceObjects (starting with a SpaceId) start on a new line.
	if ((tokenStack.isEmpty()) && (token == Token.SPACE_ID)) {
	    writeIndent();
	}

        // If we're inside a nestable token (a Map, List or other container)...
        if ((!tokenStack.isEmpty()) && (tokenStack.peek().isNestable())) {
            // ...and if the previously written token is not 'sticky' (not a class name or Map key)...
            if ((lastEndedToken == null) || (!lastEndedToken.isSticky())) {
                // ...then indent the current token.
                writeIndent();
            }
        }

	// Write the begin-tag of the token.
	nextWriter.beginToken(token);
	tokenStack.push(token);

	// If we're starting a nestable token...
        if (token.isNestable()) {
            // ...then increase the indentation. Also reset the lastEndedToken to prevent the previously ended token
            // from messing-up the indentation of nested elements that are to come.
            increaseIndent();
            lastEndedToken = null;
        }
    }

    public void endToken(Token token) throws IOException {
	if ((token.isNestable()) ) {
	    decreaseIndent();
	    if (lastEndedToken != null) {
		writeIndent();
	    }
	}

	tokenStack.pop();
	lastEndedToken = token;

	// Write the end-tag of the token.
	nextWriter.endToken(token);
    }

    public void write(String data) throws IOException {
	nextWriter.write(data);
    }

    public void close() throws IOException {
	nextWriter.close();
    }

    private void increaseIndent() {
	indent.append(TAB);
    }

    private void decreaseIndent() {
	indent.setLength(indent.length() - TAB.length());
    }

    private void writeIndent() throws IOException {
	nextWriter.write(indent.toString());
    }
}
