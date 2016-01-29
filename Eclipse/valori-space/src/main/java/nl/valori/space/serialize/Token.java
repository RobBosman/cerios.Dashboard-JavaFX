package nl.valori.space.serialize;

import java.util.HashSet;
import java.util.Set;

public enum Token {

    // Constructor(String beginTag, String endTag, boolean isNestable, boolean isSticky)
    SPACE_ID("`", "`", false, false), //
    CLASS("(", ")", false, true), //
    SIMPLE_VALUE("\"", "\"", false, false), //
    ARRAY("[", "]", true, false), //
    LIST("[", "]", true, false), //
    MAP("{", "}", true, false), //
    KEY("", "=", false, true), //
    PROPERTY_OWNER("{", "}", true, false), //
    COMMENT("/*", "*/", false, true), //
    COMMENT_LINE("//", "\n", false, false);

    private static final char ESCAPE = '\\';

    static {
	Set<String> endTagsOfNestableTokens = new HashSet<String>();
	// Sanity check 1: end-tags may not contain the ESCAPE code.
	for (Token token : Token.values()) {
	    // (Copy the end-tag for sanity check 2 later on.)
	    if (token.isNestable()) {
		endTagsOfNestableTokens.add(token.getEndTag());
	    }
	    if (token.getEndTag().contains(String.valueOf(ESCAPE))) {
		throw new ExceptionInInitializerError("### BUG: the end tag of Token " + token
			+ " contains the ESCAPE code '" + ESCAPE + "', which is not allowed.");
	    }
	}

	// Sanity check 2: begin-tags may not contain the end-tag of a nestable token.
	// This is to prevent such nested begin-tag to be recognized as the enclosing end-tag.
	for (Token token : Token.values()) {
	    if (endTagsOfNestableTokens.contains(token.beginTag)) {
		throw new ExceptionInInitializerError("### BUG: the begin-tag of " + token
			+ " is equal to the end-tag of a nestable token, which is not allowed.");
	    }
	}
    }

    public static Token peekNextToken(StringBuilder sb) {
	// Skip whitespace.
	int index = 0;
	while ((index < sb.length()) && (Character.isWhitespace(sb.charAt(index)))) {
	    index++;
	}

	Token defaultToken = null;
	for (Token token : Token.values()) {
	    String tag = token.getBeginTag();
	    if (tag.length() == 0) {
		// Use any token with an empty begin-tag as the default.
		defaultToken = token;
	    } else if (sb.substring(index, index + tag.length()).equals(tag)) {
		return token;
	    }
	}
	return defaultToken;
    }

    private String beginTag;
    private String endTag;
    private boolean isNestable;
    private boolean isSticky;

    Token(String beginTag, String endTag, boolean isNestable, boolean isSticky) {
	this.beginTag = beginTag;
	this.endTag = endTag;
	this.isNestable = isNestable;
	this.isSticky = isSticky;
    }

    public String getBeginTag() {
	return beginTag;
    }

    public String getEndTag() {
	return endTag;
    }

    public boolean isNestable() {
	return isNestable;
    }

    public boolean isSticky() {
	return isSticky;
    }

    /**
     * "He said: "here\there!"" => "He said: \"here\there!\""
     * 
     * @param orgString
     * @return
     */
    public String escape(String orgString) {
	return orgString.replace(endTag, ESCAPE + endTag);
    }

    /**
     * "He said: \"here\there!\"" => "He said: "here\there!""
     * 
     * @param orgString
     * @return
     */
    public String unescape(String orgString) {
	return orgString.replace(ESCAPE + endTag, endTag);
    }

    public void readBegin(StringBuilder sb) {
	int index = sb.indexOf(beginTag);
	if ((index < 0) || (sb.substring(0, index).trim().length() != 0)) {
	    throw new RuntimeException("Cannot find begin-tag for " + this + ".");
	}
	sb.delete(0, index + beginTag.length());
    }

    public boolean readEnd(StringBuilder sb) {
	int index = findEndTag(sb, 0);
	if (index < 0) {
	    throw new RuntimeException("Cannot find end of token " + this + ".");
	}
	if ((index >= 0) && (sb.substring(0, index).trim().length() == 0)) {
	    sb.delete(0, index + endTag.length());
	    return true;
	} else {
	    return false;
	}
    }

    public String readValue(StringBuilder sb, boolean isExpected) {
	int begin = sb.indexOf(beginTag);
	if ((begin < 0) || (sb.substring(0, begin).trim().length() != 0)) {
	    if (isExpected) {
		throw new RuntimeException("Expected token '" + this + "', but found " + sb.substring(0, begin) + "...");
	    } else {
		return null;
	    }
	}
	int end = findEndTag(sb, begin + beginTag.length());
	if (end < 0) {
	    throw new RuntimeException("Cannot find match for token " + this);
	}
	String match = sb.substring(begin + beginTag.length(), end).trim();
	sb.delete(0, end + endTag.length());
	return unescape(match);
    }

    private int findEndTag(StringBuilder sb, int fromIndex) {
	int index = -1;
	while (index < 0) {
	    index = sb.indexOf(endTag, fromIndex);
	    if (index < 0) {
		return index;
	    }
	    if (index > 0) {
		// Count the number of escape codes preceding the token.
		int numEscapes = 0;
		while (sb.charAt(index - 1 - numEscapes) == ESCAPE) {
		    numEscapes++;
		}
		// If the number of escape codes is odd...
		if (numEscapes % 2 != 0) {
		    // ...then the end tag that was effectively escaped and must be ignored.
		    fromIndex = index + 1;
		    index = -1;
		}
	    }
	}
	return index;
    }
}
