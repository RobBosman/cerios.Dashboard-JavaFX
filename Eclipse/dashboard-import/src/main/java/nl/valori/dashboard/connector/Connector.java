/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.valori.dashboard.connector;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Rob
 */
public class Connector {

    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy");
    private Reader dateReader;
    private Reader valueReader;
    private File destFile;

    public Connector(Reader dateReader, Reader valueReader, String destination) {
	this.dateReader = dateReader;
	this.valueReader = valueReader;
	this.destFile = new File(destination);
    }

    public void update(String name, String dateLocation, String valueLocation) {
	Date date = new Date();
	if (dateReader != null) {
	    date = dateReader.getDate(dateLocation);
	}
	String value = valueReader.getValue(valueLocation);
	try {
	    BufferedWriter out = null;
	    try {
		out = new BufferedWriter(new FileWriter(destFile, true));
		String line = name + "\t" + DATE_FORMAT.format(date) + "\t" + value;
		out.write(line + "\n");
	    } finally {
		if (out != null) {
		    out.flush();
		    out.close();
		}
	    }
	} catch (Exception e) {
	    throw new RuntimeException(e);
	}
    }
}