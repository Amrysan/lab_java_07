package com.example.labspring1.service;

import com.example.labspring1.cache.CacheManager;
import com.example.labspring1.dto.GroupDto;
import com.example.labspring1.dto.ScheduleDto;
import com.example.labspring1.model.Group;
import com.example.labspring1.model.Schedule;
import com.example.labspring1.repository.GroupRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class GroupService {

    private final GroupRepository groupRepository;
    private final CacheManager cacheManager;
    private final RequestCounter requestCounter;

    public GroupService(GroupRepository groupRepository, CacheManager cacheManager, RequestCounter requestCounter) {
        this.groupRepository = groupRepository;
        this.cacheManager = cacheManager;
        this.requestCounter = requestCounter;
    }

    @Transactional(readOnly = true)
    public List<GroupDto> findAll() {
        requestCounter.increment();
        String cacheKey = "findAll";
        if (cacheManager.containsGroupListKey(cacheKey)) {
            return cacheManager.getGroupList(cacheKey);
        }

        List<GroupDto> result = groupRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        cacheManager.putGroupList(cacheKey, result);
        return result;
    }

    @Transactional(readOnly = true)
    public GroupDto findById(Long id) {
        requestCounter.increment();
        if (cacheManager.containsGroupKey(id)) {
            return cacheManager.getGroup(id);
        }

        Group group = groupRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Group not found with id: " + id));
        GroupDto result = convertToDto(group);
        cacheManager.putGroup(id, result);
        return result;
    }

    @Transactional(readOnly = true)
    public GroupDto findByGroupNumber(String groupNumber) {
        requestCounter.increment();
        String cacheKey = "findByGroupNumber:" + groupNumber;
        if (cacheManager.containsGroupListKey(cacheKey)) {
            return cacheManager.getGroupList(cacheKey).get(0);
        }

        Group group = groupRepository.findByGroupNumber(groupNumber)
                .orElseThrow(() -> new EntityNotFoundException("Group not found with number: " + groupNumber));
        GroupDto result = convertToDto(group);
        cacheManager.putGroupList(cacheKey, List.of(result));
        cacheManager.putGroup(group.getId(), result);
        return result;
    }

    @Transactional
    public GroupDto create(GroupDto groupDto) {
        requestCounter.increment();
        Group group = new Group();
        group.setGroupNumber(groupDto.getGroupNumber());
        Group savedGroup = groupRepository.save(group);
        GroupDto result = convertToDto(savedGroup);
        cacheManager.clearGroupCache();
        return result;
    }

    @Transactional
    public GroupDto update(Long id, GroupDto groupDto) {
        requestCounter.increment();
        Group group = groupRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Group not found with id: " + id));
        group.setGroupNumber(groupDto.getGroupNumber());
        Group updatedGroup = groupRepository.save(group);
        GroupDto result = convertToDto(updatedGroup);
        cacheManager.clearGroupCache();
        return result;
    }

    @Transactional
    public void delete(Long id) {
        requestCounter.increment();
        if (!groupRepository.existsById(id)) {
            throw new EntityNotFoundException("Group not found with id: " + id);
        }
        groupRepository.deleteById(id);
        cacheManager.clearGroupCache();
    }

    @Transactional
    public List<GroupDto> createBulk(List<GroupDto> groupDtos) {
        requestCounter.increment();
        List<Group> groups = groupDtos.stream()
                .map(dto -> {
                    Group group = new Group();
                    group.setGroupNumber(dto.getGroupNumber());
                    return group;
                })
                .collect(Collectors.toList());

        List<Group> savedGroups = groupRepository.saveAll(groups);

        List<GroupDto> result = savedGroups.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());

        result.forEach(dto -> {
            cacheManager.putGroup(dto.getId(), dto);
            cacheManager.putGroupList("findByGroupNumber:" + dto.getGroupNumber(), List.of(dto));
        });
        cacheManager.putGroupList("findAll", findAll());
        return result;
    }

    @Transactional
    public List<GroupDto> updateBulk(List<GroupDto> groupDtos) {
        requestCounter.increment();
        List<Group> groups = groupDtos.stream()
                .map(dto -> {
                    Group group = groupRepository.findById(dto.getId())
                            .orElseThrow(() -> new EntityNotFoundException("Group not found with id: " + dto.getId()));
                    group.setGroupNumber(dto.getGroupNumber());
                    return group;
                })
                .collect(Collectors.toList());

        List<Group> updatedGroups = groupRepository.saveAll(groups);

        List<GroupDto> result = updatedGroups.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());

        result.forEach(dto -> {
            cacheManager.putGroup(dto.getId(), dto);
            cacheManager.putGroupList("findByGroupNumber:" + dto.getGroupNumber(), List.of(dto));
        });
        cacheManager.putGroupList("findAll", findAll());
        return result;
    }

    private GroupDto convertToDto(Group group) {
        List<ScheduleDto> scheduleDtos = group.getSchedules().stream()
                .map(this::convertToScheduleDto)
                .collect(Collectors.toList());
        return new GroupDto(group.getId(), group.getGroupNumber(), scheduleDtos);
    }

    private ScheduleDto convertToScheduleDto(Schedule schedule) {
        return new ScheduleDto(
                schedule.getId(),
                schedule.getSubject(),
                schedule.getLessonType(),
                schedule.getTime(),
                schedule.getAuditorium(),
                schedule.getGroup().getId()
        );
    }
}