package com.example.labspring1.repository;

import com.example.labspring1.model.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
    List<Schedule> findByGroupId(Long groupId);

    @Query("SELECT s FROM Schedule s WHERE s.group.groupNumber = :groupNumber AND s.time LIKE %:date%")
    List<Schedule> findByGroupNumberAndDate(@Param("groupNumber") String groupNumber, @Param("date") String date);
}