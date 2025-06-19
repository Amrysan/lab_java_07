package com.example.labspring1.controller;

import com.example.labspring1.dto.ScheduleDto;
import com.example.labspring1.service.ScheduleService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/schedules")
public class ScheduleController {

    private final ScheduleService scheduleService;

    public ScheduleController(ScheduleService scheduleService) {
        this.scheduleService = scheduleService;
    }

    @GetMapping
    public List<ScheduleDto> getAllSchedules() {
        return scheduleService.findAll();
    }

    @GetMapping("/{id}")
    public ScheduleDto getScheduleById(@PathVariable Long id) {
        return scheduleService.findById(id);
    }

    @GetMapping("/group/{groupId}")
    public List<ScheduleDto> getSchedulesByGroupId(@PathVariable Long groupId) {
        return scheduleService.findByGroupId(groupId);
    }

    @GetMapping("/api")
    public List<ScheduleDto> getScheduleFromApi(@RequestParam String group, @RequestParam String date) {
        return scheduleService.getSchedule(group, date);
    }

    @GetMapping("/by-group-and-date")
    public List<ScheduleDto> getSchedulesByGroupAndDate(@RequestParam String groupNumber, @RequestParam String date) {
        return scheduleService.findByGroupNumberAndDate(groupNumber, date);
    }

    @PostMapping
    public ScheduleDto createSchedule(@Valid @RequestBody ScheduleDto scheduleDto) {
        return scheduleService.create(scheduleDto);
    }

    @PutMapping("/{id}")
    public ScheduleDto updateSchedule(@PathVariable Long id, @Valid @RequestBody ScheduleDto scheduleDto) {
        return scheduleService.update(id, scheduleDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSchedule(@PathVariable Long id) {
        scheduleService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/bulk")
    public ResponseEntity<List<ScheduleDto>> createSchedulesBulk(@Valid @RequestBody List<ScheduleDto> scheduleDtos) {
        List<ScheduleDto> createdSchedules = scheduleService.createBulk(scheduleDtos);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdSchedules);
    }

    @PostMapping("/bulk-update")
    public ResponseEntity<List<ScheduleDto>> updateSchedulesBulk(@Valid @RequestBody List<ScheduleDto> scheduleDtos) {
        List<ScheduleDto> updatedSchedules = scheduleService.updateBulk(scheduleDtos);
        return ResponseEntity.ok(updatedSchedules);
    }
}