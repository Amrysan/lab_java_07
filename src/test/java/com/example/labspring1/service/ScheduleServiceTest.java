package com.example.labspring1.service;

import com.example.labspring1.cache.CacheManager;
import com.example.labspring1.dto.ScheduleDto;
import com.example.labspring1.model.Group;
import com.example.labspring1.model.Schedule;
import com.example.labspring1.repository.GroupRepository;
import com.example.labspring1.repository.ScheduleRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ScheduleServiceTest {

    @InjectMocks
    private ScheduleService scheduleService;

    @Mock
    private GroupRepository groupRepository;

    @Mock
    private ScheduleRepository scheduleRepository;

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private CacheManager cacheManager;

    @Mock
    private Group group;

    @Mock
    private Schedule schedule;

    @Mock
    private ScheduleDto scheduleDto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        // Настройка поведения моков
        when(group.getId()).thenReturn(1L);
        when(group.getGroupNumber()).thenReturn("12345");

        when(schedule.getId()).thenReturn(1L);
        when(schedule.getSubject()).thenReturn("Math");
        when(schedule.getLessonType()).thenReturn("Lecture");
        when(schedule.getTime()).thenReturn("10:00-11:30");
        when(schedule.getAuditorium()).thenReturn("101");
        when(schedule.getGroup()).thenReturn(group);

        when(scheduleDto.getId()).thenReturn(1L);
        when(scheduleDto.getSubject()).thenReturn("Math");
        when(scheduleDto.getLessonType()).thenReturn("Lecture");
        when(scheduleDto.getTime()).thenReturn("10:00-11:30");
        when(scheduleDto.getAuditorium()).thenReturn("101");
        when(scheduleDto.getGroupId()).thenReturn(1L);
    }

    @Test
    @DisplayName("should return all schedules from cache when cache contains data")
    void shouldReturnAllSchedulesFromCache() {
        String cacheKey = "findAll";
        List<ScheduleDto> cachedSchedules = List.of(scheduleDto);
        when(cacheManager.containsScheduleListKey(cacheKey)).thenReturn(true);
        when(cacheManager.getScheduleList(cacheKey)).thenReturn(cachedSchedules);

        List<ScheduleDto> result = scheduleService.findAll();

        assertEquals(cachedSchedules, result);
        verify(scheduleRepository, never()).findAll();
    }

    @Test
    @DisplayName("should return all schedules from repository when cache is empty")
    void shouldReturnAllSchedulesFromRepository() {
        String cacheKey = "findAll";
        when(cacheManager.containsScheduleListKey(cacheKey)).thenReturn(false);
        when(scheduleRepository.findAll()).thenReturn(List.of(schedule));

        List<ScheduleDto> result = scheduleService.findAll();

        assertEquals(1, result.size());
        assertEquals(scheduleDto.getId(), result.get(0).getId());
        assertEquals(scheduleDto.getSubject(), result.get(0).getSubject());
        verify(cacheManager).putScheduleList(cacheKey, result);
    }

    @Test
    @DisplayName("should return schedule by id from cache when cache contains data")
    void shouldReturnScheduleByIdFromCache() {
        when(cacheManager.containsScheduleKey(1L)).thenReturn(true);
        when(cacheManager.getSchedule(1L)).thenReturn(scheduleDto);

        ScheduleDto result = scheduleService.findById(1L);

        assertEquals(scheduleDto, result);
        verify(scheduleRepository, never()).findById(any());
    }

    @Test
    @DisplayName("should return schedule by id from repository when cache is empty")
    void shouldReturnScheduleByIdFromRepository() {
        when(cacheManager.containsScheduleKey(1L)).thenReturn(false);
        when(scheduleRepository.findById(1L)).thenReturn(Optional.of(schedule));

        ScheduleDto result = scheduleService.findById(1L);

        assertEquals(scheduleDto.getId(), result.getId());
        assertEquals(scheduleDto.getSubject(), result.getSubject());
        verify(cacheManager).putSchedule(1L, result);
    }

    @Test
    @DisplayName("should throw exception when schedule by id not found")
    void shouldThrowExceptionWhenScheduleNotFoundById() {
        when(cacheManager.containsScheduleKey(1L)).thenReturn(false);
        when(scheduleRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> scheduleService.findById(1L));
    }

    @Test
    @DisplayName("should return schedules by group id from cache when cache contains data")
    void shouldReturnSchedulesByGroupIdFromCache() {
        String cacheKey = "findByGroupId:1";
        List<ScheduleDto> cachedSchedules = List.of(scheduleDto);
        when(cacheManager.containsScheduleListKey(cacheKey)).thenReturn(true);
        when(cacheManager.getScheduleList(cacheKey)).thenReturn(cachedSchedules);

        List<ScheduleDto> result = scheduleService.findByGroupId(1L);

        assertEquals(cachedSchedules, result);
        verify(scheduleRepository, never()).findByGroupId(any());
    }

    @Test
    @DisplayName("should return schedules by group id from repository when cache is empty")
    void shouldReturnSchedulesByGroupIdFromRepository() {
        String cacheKey = "findByGroupId:1";
        when(cacheManager.containsScheduleListKey(cacheKey)).thenReturn(false);
        when(scheduleRepository.findByGroupId(1L)).thenReturn(List.of(schedule));

        List<ScheduleDto> result = scheduleService.findByGroupId(1L);

        assertEquals(1, result.size());
        assertEquals(scheduleDto.getId(), result.get(0).getId());
        verify(cacheManager).putScheduleList(cacheKey, result);
    }

    @Test
    @DisplayName("should return schedules by group number and date from cache when cache contains data")
    void shouldReturnSchedulesByGroupNumberAndDateFromCache() {
        String cacheKey = "findByGroupNumberAndDate:12345:2025-05-23";
        List<ScheduleDto> cachedSchedules = List.of(scheduleDto);
        when(cacheManager.containsScheduleListKey(cacheKey)).thenReturn(true);
        when(cacheManager.getScheduleList(cacheKey)).thenReturn(cachedSchedules);

        List<ScheduleDto> result = scheduleService.findByGroupNumberAndDate("12345", "2025-05-23");

        assertEquals(cachedSchedules, result);
        verify(scheduleRepository, never()).findByGroupNumberAndDate(any(), any());
    }

    @Test
    @DisplayName("should return schedules by group number and date from repository when cache is empty")
    void shouldReturnSchedulesByGroupNumberAndDateFromRepository() {
        String cacheKey = "findByGroupNumberAndDate:12345:2025-05-23";
        when(cacheManager.containsScheduleListKey(cacheKey)).thenReturn(false);
        when(scheduleRepository.findByGroupNumberAndDate("12345", "2025-05-23")).thenReturn(List.of(schedule));

        List<ScheduleDto> result = scheduleService.findByGroupNumberAndDate("12345", "2025-05-23");

        assertEquals(1, result.size());
        assertEquals(scheduleDto.getId(), result.get(0).getId());
        verify(cacheManager).putScheduleList(cacheKey, result);
    }

    @Test
    @DisplayName("should create schedule successfully")
    void shouldCreateScheduleSuccessfully() {
        when(groupRepository.findById(1L)).thenReturn(Optional.of(group));
        when(scheduleRepository.save(any(Schedule.class))).thenReturn(schedule);

        ScheduleDto result = scheduleService.create(scheduleDto);

        assertEquals(scheduleDto.getId(), result.getId());
        assertEquals(scheduleDto.getSubject(), result.getSubject());
        verify(cacheManager).clearScheduleCache();
    }

    @Test
    @DisplayName("should throw exception when creating schedule with non-existent group")
    void shouldThrowExceptionWhenCreatingScheduleWithNonExistentGroup() {
        when(groupRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> scheduleService.create(scheduleDto));
    }

    @Test
    @DisplayName("should update schedule successfully")
    void shouldUpdateScheduleSuccessfully() {
        Schedule updatedSchedule = mock(Schedule.class);
        ScheduleDto updatedDto = mock(ScheduleDto.class);

        when(updatedSchedule.getId()).thenReturn(1L);
        when(updatedSchedule.getSubject()).thenReturn("Physics");
        when(updatedSchedule.getLessonType()).thenReturn("Lab");
        when(updatedSchedule.getTime()).thenReturn("12:00-13:30");
        when(updatedSchedule.getAuditorium()).thenReturn("102");
        when(updatedSchedule.getGroup()).thenReturn(group);

        when(updatedDto.getId()).thenReturn(1L);
        when(updatedDto.getSubject()).thenReturn("Physics");
        when(updatedDto.getLessonType()).thenReturn("Lab");
        when(updatedDto.getTime()).thenReturn("12:00-13:30");
        when(updatedDto.getAuditorium()).thenReturn("102");
        when(updatedDto.getGroupId()).thenReturn(1L);

        when(scheduleRepository.findById(1L)).thenReturn(Optional.of(schedule));
        when(groupRepository.findById(1L)).thenReturn(Optional.of(group));
        when(scheduleRepository.save(any(Schedule.class))).thenReturn(updatedSchedule);

        ScheduleDto result = scheduleService.update(1L, updatedDto);

        assertEquals(updatedDto.getId(), result.getId());
        assertEquals(updatedDto.getSubject(), result.getSubject());
        verify(cacheManager).clearScheduleCache();
    }

    @Test
    @DisplayName("should throw exception when updating non-existent schedule")
    void shouldThrowExceptionWhenUpdatingNonExistentSchedule() {
        when(scheduleRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> scheduleService.update(1L, scheduleDto));
    }

    @Test
    @DisplayName("should delete schedule successfully")
    void shouldDeleteScheduleSuccessfully() {
        when(scheduleRepository.existsById(1L)).thenReturn(true);

        scheduleService.delete(1L);

        verify(scheduleRepository).deleteById(1L);
        verify(cacheManager).clearScheduleCache();
    }

    @Test
    @DisplayName("should throw exception when deleting non-existent schedule")
    void shouldThrowExceptionWhenDeletingNonExistentSchedule() {
        when(scheduleRepository.existsById(1L)).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> scheduleService.delete(1L));
    }

    @Test
    @DisplayName("should create multiple schedules successfully")
    void shouldCreateMultipleSchedulesSuccessfully() {
        ScheduleDto scheduleDto1 = mock(ScheduleDto.class);
        ScheduleDto scheduleDto2 = mock(ScheduleDto.class);
        Schedule schedule1 = mock(Schedule.class);
        Schedule schedule2 = mock(Schedule.class);

        when(scheduleDto1.getSubject()).thenReturn("Math");
        when(scheduleDto1.getLessonType()).thenReturn("Lecture");
        when(scheduleDto1.getTime()).thenReturn("10:00-11:30");
        when(scheduleDto1.getAuditorium()).thenReturn("101");
        when(scheduleDto1.getGroupId()).thenReturn(1L);

        when(scheduleDto2.getSubject()).thenReturn("Physics");
        when(scheduleDto2.getLessonType()).thenReturn("Lab");
        when(scheduleDto2.getTime()).thenReturn("12:00-13:30");
        when(scheduleDto2.getAuditorium()).thenReturn("102");
        when(scheduleDto2.getGroupId()).thenReturn(1L);

        when(schedule1.getId()).thenReturn(1L);
        when(schedule1.getSubject()).thenReturn("Math");
        when(schedule1.getLessonType()).thenReturn("Lecture");
        when(schedule1.getTime()).thenReturn("10:00-11:30");
        when(schedule1.getAuditorium()).thenReturn("101");
        when(schedule1.getGroup()).thenReturn(group);

        when(schedule2.getId()).thenReturn(2L);
        when(schedule2.getSubject()).thenReturn("Physics");
        when(schedule2.getLessonType()).thenReturn("Lab");
        when(schedule2.getTime()).thenReturn("12:00-13:30");
        when(schedule2.getAuditorium()).thenReturn("102");
        when(schedule2.getGroup()).thenReturn(group);

        List<ScheduleDto> scheduleDtos = List.of(scheduleDto1, scheduleDto2);
        List<Schedule> schedules = List.of(schedule1, schedule2);

        when(groupRepository.findById(1L)).thenReturn(Optional.of(group));
        when(scheduleRepository.saveAll(any())).thenReturn(schedules);
        when(scheduleRepository.findAll()).thenReturn(schedules);

        List<ScheduleDto> result = scheduleService.createBulk(scheduleDtos);

        assertEquals(2, result.size());
        assertEquals("Math", result.get(0).getSubject());
        assertEquals("Physics", result.get(1).getSubject());
        verify(cacheManager, times(2)).putSchedule(anyLong(), any(ScheduleDto.class));
        verify(cacheManager).putScheduleList(eq("findAll"), any());
    }

    @Test
    @DisplayName("should update multiple schedules successfully")
    void shouldUpdateMultipleSchedulesSuccessfully() {
        ScheduleDto scheduleDto1 = mock(ScheduleDto.class);
        ScheduleDto scheduleDto2 = mock(ScheduleDto.class);
        Schedule schedule1 = mock(Schedule.class);
        Schedule schedule2 = mock(Schedule.class);

        when(scheduleDto1.getId()).thenReturn(1L);
        when(scheduleDto1.getSubject()).thenReturn("Math");
        when(scheduleDto1.getLessonType()).thenReturn("Lecture");
        when(scheduleDto1.getTime()).thenReturn("10:00-11:30");
        when(scheduleDto1.getAuditorium()).thenReturn("101");
        when(scheduleDto1.getGroupId()).thenReturn(1L);

        when(scheduleDto2.getId()).thenReturn(2L);
        when(scheduleDto2.getSubject()).thenReturn("Physics");
        when(scheduleDto2.getLessonType()).thenReturn("Lab");
        when(scheduleDto2.getTime()).thenReturn("12:00-13:30");
        when(scheduleDto2.getAuditorium()).thenReturn("102");
        when(scheduleDto2.getGroupId()).thenReturn(1L);

        when(schedule1.getId()).thenReturn(1L);
        when(schedule1.getSubject()).thenReturn("Math");
        when(schedule1.getLessonType()).thenReturn("Lecture");
        when(schedule1.getTime()).thenReturn("10:00-11:30");
        when(schedule1.getAuditorium()).thenReturn("101");
        when(schedule1.getGroup()).thenReturn(group);

        when(schedule2.getId()).thenReturn(2L);
        when(schedule2.getSubject()).thenReturn("Physics");
        when(schedule2.getLessonType()).thenReturn("Lab");
        when(schedule2.getTime()).thenReturn("12:00-13:30");
        when(schedule2.getAuditorium()).thenReturn("102");
        when(schedule2.getGroup()).thenReturn(group);

        List<ScheduleDto> scheduleDtos = List.of(scheduleDto1, scheduleDto2);
        List<Schedule> schedules = List.of(schedule1, schedule2);

        when(scheduleRepository.findById(1L)).thenReturn(Optional.of(schedule1));
        when(scheduleRepository.findById(2L)).thenReturn(Optional.of(schedule2));
        when(groupRepository.findById(1L)).thenReturn(Optional.of(group));
        when(scheduleRepository.saveAll(any())).thenReturn(schedules);
        when(scheduleRepository.findAll()).thenReturn(schedules);

        List<ScheduleDto> result = scheduleService.updateBulk(scheduleDtos);

        assertEquals(2, result.size());
        assertEquals("Math", result.get(0).getSubject());
        assertEquals("Physics", result.get(1).getSubject());
        verify(cacheManager, times(2)).putSchedule(anyLong(), any(ScheduleDto.class));
        verify(cacheManager).putScheduleList(eq("findAll"), any());
    }

    @Test
    @DisplayName("should throw exception when updating non-existent schedule in bulk")
    void shouldThrowExceptionWhenUpdatingNonExistentScheduleInBulk() {
        ScheduleDto scheduleDto1 = mock(ScheduleDto.class);
        ScheduleDto scheduleDto2 = mock(ScheduleDto.class);

        when(scheduleDto1.getId()).thenReturn(1L);
        when(scheduleDto1.getGroupId()).thenReturn(1L);
        when(scheduleDto2.getId()).thenReturn(2L);
        when(scheduleDto2.getGroupId()).thenReturn(1L);

        when(scheduleRepository.findById(1L)).thenReturn(Optional.of(schedule));
        when(scheduleRepository.findById(2L)).thenReturn(Optional.empty());

        List<ScheduleDto> scheduleDtos = List.of(scheduleDto1, scheduleDto2);

        assertThrows(EntityNotFoundException.class, () -> scheduleService.updateBulk(scheduleDtos));
    }
}