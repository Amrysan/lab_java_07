package com.example.labspring1.service;

import com.example.labspring1.cache.CacheManager;
import com.example.labspring1.dto.GroupDto;
import com.example.labspring1.model.Group;
import com.example.labspring1.repository.GroupRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GroupServiceTest {

    @InjectMocks
    private GroupService groupService;

    @Mock
    private GroupRepository groupRepository;

    @Mock
    private CacheManager cacheManager;

    @Mock
    private Group group;

    @Mock
    private GroupDto groupDto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        // Настройка поведения моков
        when(group.getId()).thenReturn(1L);
        when(group.getGroupNumber()).thenReturn("334701");
        when(group.getSchedules()).thenReturn(Collections.emptyList());

        when(groupDto.getId()).thenReturn(1L);
        when(groupDto.getGroupNumber()).thenReturn("334701");
        when(groupDto.getSchedules()).thenReturn(Collections.emptyList());
    }

    @Test
    @DisplayName("should return all groups from cache when cache contains data")
    void shouldReturnAllGroupsFromCache() {
        String cacheKey = "findAll";
        List<GroupDto> cachedGroups = List.of(groupDto);
        when(cacheManager.containsGroupListKey(cacheKey)).thenReturn(true);
        when(cacheManager.getGroupList(cacheKey)).thenReturn(cachedGroups);

        List<GroupDto> result = groupService.findAll();

        assertEquals(cachedGroups, result);
        verify(groupRepository, never()).findAll();
    }

    @Test
    @DisplayName("should return all groups from repository when cache is empty")
    void shouldReturnAllGroupsFromRepository() {
        String cacheKey = "findAll";
        when(cacheManager.containsGroupListKey(cacheKey)).thenReturn(false);
        when(groupRepository.findAll()).thenReturn(List.of(group));

        List<GroupDto> result = groupService.findAll();

        assertEquals(1, result.size());
        assertEquals(groupDto.getId(), result.get(0).getId());
        assertEquals(groupDto.getGroupNumber(), result.get(0).getGroupNumber());
        verify(cacheManager).putGroupList(cacheKey, result);
    }

    @Test
    @DisplayName("should return group by id from cache when cache contains data")
    void shouldReturnGroupByIdFromCache() {
        when(cacheManager.containsGroupKey(1L)).thenReturn(true);
        when(cacheManager.getGroup(1L)).thenReturn(groupDto);

        GroupDto result = groupService.findById(1L);

        assertEquals(groupDto, result);
        verify(groupRepository, never()).findById(any());
    }

    @Test
    @DisplayName("should return group by id from repository when cache is empty")
    void shouldReturnGroupByIdFromRepository() {
        when(cacheManager.containsGroupKey(1L)).thenReturn(false);
        when(groupRepository.findById(1L)).thenReturn(Optional.of(group));

        GroupDto result = groupService.findById(1L);

        assertEquals(groupDto.getId(), result.getId());
        assertEquals(groupDto.getGroupNumber(), result.getGroupNumber());
        verify(cacheManager).putGroup(1L, result);
    }

    @Test
    @DisplayName("should throw exception when group by id not found")
    void shouldThrowExceptionWhenGroupNotFoundById() {
        when(cacheManager.containsGroupKey(1L)).thenReturn(false);
        when(groupRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> groupService.findById(1L));
    }

    @Test
    @DisplayName("should return group by number from cache when cache contains data")
    void shouldReturnGroupByNumberFromCache() {
        String cacheKey = "findByGroupNumber:12345";
        when(cacheManager.containsGroupListKey(cacheKey)).thenReturn(true);
        when(cacheManager.getGroupList(cacheKey)).thenReturn(List.of(groupDto));

        GroupDto result = groupService.findByGroupNumber("12345");

        assertEquals(groupDto, result);
        verify(groupRepository, never()).findByGroupNumber(any());
    }

    @Test
    @DisplayName("should return group by number from repository when cache is empty")
    void shouldReturnGroupByNumberFromRepository() {
        String cacheKey = "findByGroupNumber:12345";
        when(cacheManager.containsGroupListKey(cacheKey)).thenReturn(false);
        when(groupRepository.findByGroupNumber("12345")).thenReturn(Optional.of(group));

        GroupDto result = groupService.findByGroupNumber("12345");

        assertEquals(groupDto.getId(), result.getId());
        assertEquals(groupDto.getGroupNumber(), result.getGroupNumber());
        verify(cacheManager).putGroupList(cacheKey, List.of(result));
        verify(cacheManager).putGroup(group.getId(), result);
    }

    @Test
    @DisplayName("should throw exception when group by number not found")
    void shouldThrowExceptionWhenGroupNotFoundByNumber() {
        String cacheKey = "findByGroupNumber:12345";
        when(cacheManager.containsGroupListKey(cacheKey)).thenReturn(false);
        when(groupRepository.findByGroupNumber("12345")).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> groupService.findByGroupNumber("12345"));
    }

    @Test
    @DisplayName("should create group successfully")
    void shouldCreateGroupSuccessfully() {
        when(groupRepository.save(any(Group.class))).thenReturn(group);

        GroupDto result = groupService.create(groupDto);

        assertEquals(groupDto.getId(), result.getId());
        assertEquals(groupDto.getGroupNumber(), result.getGroupNumber());
        verify(cacheManager).clearGroupCache();
    }

    @Test
    @DisplayName("should update group successfully")
    void shouldUpdateGroupSuccessfully() {
        Group updatedGroup = mock(Group.class);
        GroupDto updatedDto = mock(GroupDto.class);

        when(updatedGroup.getId()).thenReturn(1L);
        when(updatedGroup.getGroupNumber()).thenReturn("67890");
        when(updatedGroup.getSchedules()).thenReturn(Collections.emptyList());
        when(updatedDto.getId()).thenReturn(1L);
        when(updatedDto.getGroupNumber()).thenReturn("67890");
        when(updatedDto.getSchedules()).thenReturn(Collections.emptyList());

        when(groupRepository.findById(1L)).thenReturn(Optional.of(group));
        when(groupRepository.save(any(Group.class))).thenReturn(updatedGroup);

        GroupDto result = groupService.update(1L, updatedDto);

        assertEquals(updatedDto.getId(), result.getId());
        assertEquals(updatedDto.getGroupNumber(), result.getGroupNumber());
        verify(cacheManager).clearGroupCache();
    }

    @Test
    @DisplayName("should throw exception when updating non-existent group")
    void shouldThrowExceptionWhenUpdatingNonExistentGroup() {
        when(groupRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> groupService.update(1L, groupDto));
    }

    @Test
    @DisplayName("should delete group successfully")
    void shouldDeleteGroupSuccessfully() {
        when(groupRepository.existsById(1L)).thenReturn(true);

        groupService.delete(1L);

        verify(groupRepository).deleteById(1L);
        verify(cacheManager).clearGroupCache();
    }

    @Test
    @DisplayName("should throw exception when deleting non-existent group")
    void shouldThrowExceptionWhenDeletingNonExistentGroup() {
        when(groupRepository.existsById(1L)).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> groupService.delete(1L));
    }

    @Test
    @DisplayName("should create multiple groups successfully")
    void shouldCreateMultipleGroupsSuccessfully() {
        GroupDto groupDto1 = mock(GroupDto.class);
        GroupDto groupDto2 = mock(GroupDto.class);
        Group group1 = mock(Group.class);
        Group group2 = mock(Group.class);

        when(groupDto1.getGroupNumber()).thenReturn("12345");
        when(groupDto1.getSchedules()).thenReturn(Collections.emptyList());
        when(groupDto2.getGroupNumber()).thenReturn("67890");
        when(groupDto2.getSchedules()).thenReturn(Collections.emptyList());
        when(group1.getId()).thenReturn(1L);
        when(group1.getGroupNumber()).thenReturn("12345");
        when(group1.getSchedules()).thenReturn(Collections.emptyList());
        when(group2.getId()).thenReturn(2L);
        when(group2.getGroupNumber()).thenReturn("67890");
        when(group2.getSchedules()).thenReturn(Collections.emptyList());

        List<GroupDto> groupDtos = List.of(groupDto1, groupDto2);
        List<Group> groups = List.of(group1, group2);

        when(groupRepository.saveAll(any())).thenReturn(groups);
        when(groupRepository.findAll()).thenReturn(groups);

        List<GroupDto> result = groupService.createBulk(groupDtos);

        assertEquals(2, result.size());
        assertEquals("12345", result.get(0).getGroupNumber());
        assertEquals("67890", result.get(1).getGroupNumber());
        verify(cacheManager, times(2)).putGroup(anyLong(), any(GroupDto.class));
        verify(cacheManager, times(2)).putGroupList(anyString(), any());
        verify(cacheManager).putGroupList(eq("findAll"), any());
    }

    @Test
    @DisplayName("should update multiple groups successfully")
    void shouldUpdateMultipleGroupsSuccessfully() {
        GroupDto groupDto1 = mock(GroupDto.class);
        GroupDto groupDto2 = mock(GroupDto.class);
        Group group1 = mock(Group.class);
        Group group2 = mock(Group.class);

        when(groupDto1.getId()).thenReturn(1L);
        when(groupDto1.getGroupNumber()).thenReturn("12345");
        when(groupDto1.getSchedules()).thenReturn(Collections.emptyList());
        when(groupDto2.getId()).thenReturn(2L);
        when(groupDto2.getGroupNumber()).thenReturn("67890");
        when(groupDto2.getSchedules()).thenReturn(Collections.emptyList());
        when(group1.getId()).thenReturn(1L);
        when(group1.getGroupNumber()).thenReturn("12345");
        when(group1.getSchedules()).thenReturn(Collections.emptyList());
        when(group2.getId()).thenReturn(2L);
        when(group2.getGroupNumber()).thenReturn("67890");
        when(group2.getSchedules()).thenReturn(Collections.emptyList());

        List<GroupDto> groupDtos = List.of(groupDto1, groupDto2);
        List<Group> groups = List.of(group1, group2);

        when(groupRepository.findById(1L)).thenReturn(Optional.of(group1));
        when(groupRepository.findById(2L)).thenReturn(Optional.of(group2));
        when(groupRepository.saveAll(any())).thenReturn(groups);
        when(groupRepository.findAll()).thenReturn(groups);

        List<GroupDto> result = groupService.updateBulk(groupDtos);

        assertEquals(2, result.size());
        assertEquals("12345", result.get(0).getGroupNumber());
        assertEquals("67890", result.get(1).getGroupNumber());
        verify(cacheManager, times(2)).putGroup(anyLong(), any(GroupDto.class));
        verify(cacheManager, times(2)).putGroupList(anyString(), any());
        verify(cacheManager).putGroupList(eq("findAll"), any());
    }

    @Test
    @DisplayName("should throw exception when updating non-existent group in bulk")
    void shouldThrowExceptionWhenUpdatingNonExistentGroupInBulk() {
        GroupDto groupDto1 = mock(GroupDto.class);
        GroupDto groupDto2 = mock(GroupDto.class);

        when(groupDto1.getId()).thenReturn(1L);
        when(groupDto1.getGroupNumber()).thenReturn("12345");
        when(groupDto2.getId()).thenReturn(2L);
        when(groupDto2.getGroupNumber()).thenReturn("67890");

        when(groupRepository.findById(1L)).thenReturn(Optional.of(group));
        when(groupRepository.findById(2L)).thenReturn(Optional.empty());

        List<GroupDto> groupDtos = List.of(groupDto1, groupDto2);

        assertThrows(EntityNotFoundException.class, () -> groupService.updateBulk(groupDtos));
    }
}