package com.ar.sgt.mastersorpresas.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;

import java.io.Serializable;
import java.util.Date;

@Entity
public class Reminder implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	@Id(autoincrement = true)
	private Long id;

	@Index(unique = true)
	private Long parentKey;

    private String title;

    private String percentage;

    private Date dateFrom;

    private Date dateTo;

    private Long nextSchedule;

    @Generated(hash = 138840458)
    public Reminder(Long id, Long parentKey, String title, String percentage,
            Date dateFrom, Date dateTo, Long nextSchedule) {
        this.id = id;
        this.parentKey = parentKey;
        this.title = title;
        this.percentage = percentage;
        this.dateFrom = dateFrom;
        this.dateTo = dateTo;
        this.nextSchedule = nextSchedule;
    }

    @Generated(hash = 4427342)
    public Reminder() {
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getParentKey() {
        return parentKey;
    }

    public void setParentKey(Long parentKey) {
        this.parentKey = parentKey;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPercentage() {
        return percentage;
    }

    public void setPercentage(String percentage) {
        this.percentage = percentage;
    }

    public Long getNextSchedule() {
        return nextSchedule;
    }

    public void setNextSchedule(Long nextSchedule) {
        this.nextSchedule = nextSchedule;
    }

    public Date getDateFrom() {
        return dateFrom;
    }

    public void setDateFrom(Date dateFrom) {
        this.dateFrom = dateFrom;
    }

    public Date getDateTo() {
        return dateTo;
    }

    public void setDateTo(Date dateTo) {
        this.dateTo = dateTo;
    }

    @Override
    public boolean equals(Object obj) {
        if (Reminder.class.isAssignableFrom(obj.getClass()) && obj != null) {
            return getParentKey().equals(((Reminder) obj).getParentKey());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return 1080031436;
    }

    @Override
	public String toString() {
		StringBuilder b = new StringBuilder("Reminder [");
        b.append("id=").append(getId()).append(";");
        b.append("parentKey=").append(getParentKey()).append(";");
        b.append("title=").append(title).append(";");
        b.append("dateFrom=").append(dateFrom).append(";");
        b.append("dateTo=").append(dateTo).append(";");
        b.append("nextSchedule=").append(nextSchedule).append(";");
        b.append("percentage=").append(percentage).append("]");
		return b.toString();
	}
	
}
