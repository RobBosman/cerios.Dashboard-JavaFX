package nl.valori.dashboard.model;

import java.util.Comparator;
import java.util.Date;

import nl.valori.space.annotations.SerializableTransient;

public class DatedValue extends AbstractPersistentEntity {

    public static final Comparator<DatedValue> COMPARATOR_BEGIN_DATE = new Comparator<DatedValue>() {

	public int compare(DatedValue obj1, DatedValue obj2) {
	    if ((obj1.beginDate != null) && (obj2.beginDate != null)) {
		return obj1.beginDate.compareTo(obj2.beginDate);
	    } else if ((obj1.beginDate != null) && (obj2.beginDate == null)) {
		return -1;
	    } else if ((obj1.beginDate == null) && (obj2.beginDate != null)) {
		return 1;
	    } else {
		return 0;
	    }
	}
    };

    public static final Comparator<DatedValue> COMPARATOR_END_DATE = new Comparator<DatedValue>() {

	public int compare(DatedValue obj1, DatedValue obj2) {
	    if ((obj1.endDate != null) && (obj2.endDate != null)) {
		return obj1.endDate.compareTo(obj2.endDate);
	    } else if ((obj1.endDate != null) && (obj2.endDate == null)) {
		return 1;
	    } else if ((obj1.endDate == null) && (obj2.endDate != null)) {
		return -1;
	    } else {
		return 0;
	    }
	}
    };

    private Date beginDate;
    private Date endDate;
    private float value;
    @SerializableTransient
    private ImportHistory importHistory;

    public Date getBeginDate() {
	return this.beginDate;
    }

    public void setBeginDate(Date beginDate) {
	this.beginDate = beginDate;
    }

    public java.util.Date getEndDate() {
	return this.endDate;
    }

    public void setEndDate(Date endDate) {
	this.endDate = endDate;
    }

    public float getValue() {
	return this.value;
    }

    public void setValue(float value) {
	this.value = value;
    }

    public ImportHistory getImportHistory() {
	return this.importHistory;
    }

    public void setImportHistory(ImportHistory importHistory) {
	this.importHistory = importHistory;
    }

    @Override
    public String toString() {
	return " beginDate=" + beginDate + " endDate=" + endDate + " value=" + value;
    }

    protected float interpolateValueAt(Date date) {
	if (beginDate == null) {
	    return value;
	} else if (date.getTime() < beginDate.getTime()) {
	    return 0;
	} else if ((endDate == null) || (date.getTime() >= endDate.getTime())) {
	    return value;
	} else {
	    // Interpolate
	    return value * (date.getTime() - beginDate.getTime()) / (endDate.getTime() - beginDate.getTime());
	}
    }
}