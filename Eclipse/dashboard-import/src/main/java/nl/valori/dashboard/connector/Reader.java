/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.valori.dashboard.connector;

import java.util.Date;

/**
 * 
 * @author Rob
 */
public interface Reader {

    public Date getDate(String dateLocation);

    public String getValue(String valueLocation);
}