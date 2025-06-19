package com.example.labspring1.cache;

import com.example.labspring1.dto.GroupDto;
import com.example.labspring1.dto.ScheduleDto;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class CacheManager {
    private final Map<String, List<GroupDto>> groupListCache = new HashMap<>();
    private final Map<Long, GroupDto> groupCache = new HashMap<>();
    private final Map<String, List<ScheduleDto>> scheduleListCache = new HashMap<>();
    private final Map<Long, ScheduleDto> scheduleCache = new HashMap<>();


    public void putGroupList(String key, List<GroupDto> value) {
        groupListCache.put(key, value);
    }

    public List<GroupDto> getGroupList(String key) {
        return groupListCache.get(key);
    }

    public boolean containsGroupListKey(String key) {
        return groupListCache.containsKey(key);
    }


    public void putGroup(Long key, GroupDto value) {
        groupCache.put(key, value);
    }

    public GroupDto getGroup(Long key) {
        return groupCache.get(key);
    }

    public boolean containsGroupKey(Long key) {
        return groupCache.containsKey(key);
    }

    public void removeGroup(Long key) {
        groupCache.remove(key);
    }

    public void putScheduleList(String key, List<ScheduleDto> value) {
        scheduleListCache.put(key, value);
    }

    public List<ScheduleDto> getScheduleList(String key) {
        return scheduleListCache.get(key);
    }

    public boolean containsScheduleListKey(String key) {
        return scheduleListCache.containsKey(key);
    }

    public void putSchedule(Long key, ScheduleDto value) {
        scheduleCache.put(key, value);
    }

    public ScheduleDto getSchedule(Long key) {
        return scheduleCache.get(key);
    }

    public boolean containsScheduleKey(Long key) {
        return scheduleCache.containsKey(key);
    }

    public void removeSchedule(Long key) {
        scheduleCache.remove(key);
    }

    public void clearGroupCache() {
        groupListCache.clear();
        groupCache.clear();
    }

    public void clearScheduleCache() {
        scheduleListCache.clear();
        scheduleCache.clear();
    }
}