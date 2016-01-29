package nl.valori.dashboard.dataloader;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class XmlUtil {

    private static final DocumentBuilder DOCUMENT_BUILDER;
    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    static {
	DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	dbf.setNamespaceAware(true);
	try {
	    DOCUMENT_BUILDER = dbf.newDocumentBuilder();
	} catch (ParserConfigurationException e) {
	    throw new ExceptionInInitializerError(e);
	}
    }

    protected static DocumentBuilder getDocumentBuilder() {
	return DOCUMENT_BUILDER;
    }

    protected boolean toBoolean(String value, boolean fallBack) {
	if (value == null) {
	    return fallBack;
	} else {
	    return Boolean.parseBoolean(value);
	}
    }

    protected Date toDate(String value) {
	if ((value == null) || (value.length() == 0)) {
	    return null;
	}
	try {
	    return DATE_FORMAT.parse(value);
	} catch (ParseException e) {
	    throw new RuntimeException(e);
	}
    }

    protected float toFloat(String value) {
	return toFloat(value, Float.NaN);
    }

    protected float toFloat(String value, float fallBack) {
	if ((value == null) || (value.length() == 0)) {
	    return fallBack;
	} else {
	    return Float.parseFloat(value);
	}
    }

    protected int toInt(String value, int fallBack) {
	if ((value == null) || (value.length() == 0)) {
	    return fallBack;
	} else {
	    return Integer.parseInt(value);
	}
    }
}