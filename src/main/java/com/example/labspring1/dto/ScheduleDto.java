package com.example.labspring1.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public class ScheduleDto {
    private Long id;

    @NotBlank(message = "Subject cannot be blank")
    private String subject;

    @NotBlank(message = "Lesson type cannot be blank")
    private String lessonType;

    @NotBlank(message = "Time cannot be blank")
    private String time;

    @NotBlank(message = "Auditorium cannot be blank")
    private String auditorium;

    @NotNull(message = "Group ID cannot be null")
    private Long groupId;

    private String groupNumber;
    private String date;
    private List<ScheduleDto> schedules;

    public ScheduleDto() {
    }

    public ScheduleDto(Long id, String subject, String lessonType, String time, String auditorium, Long groupId) {
        this.id = id;
        this.subject = subject;
        this.lessonType = lessonType;
        this.time = time;
        this.auditorium = auditorium;
        this.groupId = groupId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getLessonType() {
        return lessonType;
    }

    public void setLessonType(String lessonType) {
        this.lessonType = lessonType;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getAuditorium() {
        return auditorium;
    }

    public void setAuditorium(String auditorium) {
        this.auditorium = auditorium;
    }

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    public String getGroupNumber() {
        return groupNumber;
    }

    public void setGroupNumber(String groupNumber) {
        this.groupNumber = groupNumber;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public List<ScheduleDto> getSchedules() {
        return schedules;
    }

    public void setSchedules(List<ScheduleDto> schedules) {
        this.schedules = schedules;
    }
}