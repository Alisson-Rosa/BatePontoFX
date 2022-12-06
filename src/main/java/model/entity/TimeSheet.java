package model.entity;

import model.enumeration.Role;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class TimeSheet {
    private int id;
    private LocalDate datePoint;
    private LocalTime startTime;
    private LocalTime startTimeLunch;
    private LocalTime endTimeLunch;
    private LocalTime endTime;
    private String dateStr;

    private User user;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDate getDatePoint() {
        return datePoint;
    }

    public void setDatePoint(LocalDate datePoint) {
        this.datePoint = datePoint;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getStartTimeLunch() {
        return startTimeLunch;
    }

    public void setStartTimeLunch(LocalTime startTimeLunch) {
        this.startTimeLunch = startTimeLunch;
    }

    public LocalTime getEndTimeLunch() {
        return endTimeLunch;
    }

    public void setEndTimeLunch(LocalTime endTimeLunch) {
        this.endTimeLunch = endTimeLunch;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getDateStr() {
        dateStr = DateTimeFormatter.ofPattern("dd-MM-yyyy").format(datePoint);
        return dateStr;
    }

    public void setDateStr(String dateStr) {
        this.dateStr = dateStr;
    }
}
