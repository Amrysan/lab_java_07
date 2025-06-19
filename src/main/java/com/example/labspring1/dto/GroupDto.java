package com.example.labspring1.dto;

import jakarta.validation.constraints.NotBlank;
import java.util.List;

public class GroupDto {
    private Long id;

    @NotBlank(message = "Group number cannot be blank")
    private String groupNumber;

    private List<ScheduleDto> schedules;

    public GroupDto() { }

    public GroupDto(Long id, String groupNumber, List<ScheduleDto> schedules) {
        this.id = id;
        this.groupNumber = groupNumber;
        this.schedules = schedules;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getGroupNumber() {
        return groupNumber;
    }

    public void setGroupNumber(String groupNumber) {
        this.groupNumber = groupNumber;
    }

    public List<ScheduleDto> getSchedules() {
        return schedules;
    }

    public void setSchedules(List<ScheduleDto> schedules) {
        this.schedules = schedules;
    }
}