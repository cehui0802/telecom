package com.telecom.ecloudframework.sys.api.model.calendar;

import java.util.Date;

public class ScheduleHistory{

    private String id;
    private String scheduleId;
    private int rateProgress;
    private String submit;
    private String submitName;
    private Date submitTime;

    public ScheduleHistory(){}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getScheduleId() {
        return scheduleId;
    }

    public void setScheduleId(String scheduleId) {
        this.scheduleId = scheduleId;
    }

    public int getRateProgress() {
        return rateProgress;
    }

    public void setRateProgress(int rateProgress) {
        this.rateProgress = rateProgress;
    }

    public String getSubmit() {
        return submit;
    }

    public void setSubmit(String submit) {
        this.submit = submit;
    }

    public String getSubmitName() {
        return submitName;
    }

    public void setSubmitName(String submitName) {
        this.submitName = submitName;
    }

    public Date getSubmitTime() {
        return submitTime;
    }

    public void setSubmitTime(Date submitTime) {
        this.submitTime = submitTime;
    }

    @Override
    public String toString() {
        return "ScheduleHistory{" +
                "id='" + id + '\'' +
                ", scheduleId='" + scheduleId + '\'' +
                ", submit='" + submit + '\'' +
                ", submitName='" + submitName + '\'' +
                ", submitTime=" + submitTime +
                '}';
    }
}
