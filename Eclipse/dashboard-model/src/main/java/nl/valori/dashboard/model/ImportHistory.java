package nl.valori.dashboard.model;

import java.util.Date;

public class ImportHistory extends AbstractPersistentEntity {

    private Date date;
    private String remark;

    public Date getDate() {
	return this.date;
    }

    public void setDate(Date date) {
	this.date = date;
    }

    public String getRemark() {
	return this.remark;
    }

    public void setRemark(String remark) {
	this.remark = remark;
    }

    @Override
    public String toString() {
	return " date=" + date + " remark=" + remark;
    }
}