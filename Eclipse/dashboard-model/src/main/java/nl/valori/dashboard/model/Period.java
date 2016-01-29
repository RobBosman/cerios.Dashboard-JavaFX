package nl.valori.dashboard.model;

import java.util.Calendar;
import java.util.Date;

public class Period {

    public static final int PERIOD_IN_DAYS = -7;
    public static final long MILLIS_PER_DAY = 1000 * 60 * 60 * 24;

    public static int getNumDays(Date begin, Date end) {
	if ((begin == null) || (end == null)) {
	    return 0;
	} else {
	    return (int) Math.abs(Math.round((double) (end.getTime() - begin.getTime()) / MILLIS_PER_DAY));
	}
    }

    public Date begin;
    public Date end;

    public Period(Date begin, Date end) {
	if (begin == null) {
	    throw new IllegalArgumentException("BeginDate may not be null.");
	}
	this.begin = begin;
	this.end = end;
    }

    @Override
    public String toString() {
	return " begin=" + begin + " end=" + end;
    }

    public void clip(Period otherPeriod) {
	if (otherPeriod == null) {
	    return;
	}
	if ((begin == null) || (begin.getTime() < otherPeriod.begin.getTime())) {
	    begin = otherPeriod.begin;
	}
	if (otherPeriod.end != null) {
	    if ((end == null) || (end.getTime() >= otherPeriod.end.getTime())) {
		end = otherPeriod.end;
	    }
	}
    }

    public void span(Period otherPeriod) {
	if (otherPeriod == null) {
	    return;
	}
	span(otherPeriod.begin);
	span(otherPeriod.end);
    }

    public void span(Date date) {
	if (date == null) {
	    return;
	}
	if (begin.getTime() >= date.getTime()) {
	    begin = date;
	}
	if ((end == null) || (end.getTime() < date.getTime())) {
	    end = date;
	}
    }

    public boolean spans(Date date) {
	if (date == null) {
	    return false;
	} else if (date.getTime() < begin.getTime()) {
	    return false;
	} else if ((end != null) && (date.getTime() >= end.getTime())) {
	    return false;
	} else {
	    return true;
	}
    }

    public int getNumDays() {
	return getNumDays(begin, end);
    }

    public Period getPeriodTill(Date endDate) {
	if (endDate == null) {
	    return null;
	}
	// Set the start date to PERIOD_IN_DAYS days earlier.
	Calendar cal = Calendar.getInstance();
	cal.setTime(endDate);
	cal.add(Calendar.DAY_OF_MONTH, PERIOD_IN_DAYS);
	// ...and set end date of period to current date.
	Period period = new Period(cal.getTime(), endDate);
	// Clip the dates, so they won't exceed the full period (i.e. 'this').
	period.clip(this);
	return period;
    }
}