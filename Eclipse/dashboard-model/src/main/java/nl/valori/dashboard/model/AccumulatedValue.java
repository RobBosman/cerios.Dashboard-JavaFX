package nl.valori.dashboard.model;

import java.util.Date;

public class AccumulatedValue {

    public Date date;
    public float value;

    @Override
    public String toString() {
	return " date=" + date + " value=" + value;
    }
}