package com.vip.interviewpartner.domain.room.repository;

import com.vip.interviewpartner.domain.room.enitty.Room;
import com.vip.interviewpartner.domain.room.enitty.RoomStatus;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Room 엔티티에 대한 데이터 접근 리포지토리입니다.
 */
public interface RoomRepository extends JpaRepository<Room, Long> {
    /**
     * 주어진 상태의 방 목록을 페이징 처리하여 반환합니다.
     *
     * @param status   조회할 방의 상태
     * @param pageable 페이징 정보
     * @return 주어진 상태의 방 목록
     */
    Page<Room> findByStatus(RoomStatus status, Pageable pageable);

    Optional<Room> findBySessionId(String sessionId);
}
