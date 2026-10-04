package io.github.moomien.errorfreetext.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

public interface TaskRepository extends JpaRepository<Task, UUID> {
    @Query(value = """
            SELECT * FROM error_free_text.tasks
            WHERE status = 'NEW'
            ORDER BY created_at
            LIMIT :limit
            FOR UPDATE SKIP LOCKED
            """, nativeQuery = true)
    List<Task> findNewForUpdate(@Param("limit") int limit);

    @Modifying
    @Transactional
    @Query("""
            update Task t
            set t.status = :newStatus, t.updatedAt = CURRENT_TIMESTAMP
            where t.status = :currentStatus
            """)
    int updateStatus(@Param("currentStatus") TaskStatus currentStatus,
                     @Param("newStatus") TaskStatus newStatus);
}