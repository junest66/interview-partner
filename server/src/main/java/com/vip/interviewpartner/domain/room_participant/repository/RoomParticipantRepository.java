package com.vip.interviewpartner.domain.room_participant.repository;

import com.vip.interviewpartner.domain.room.dto.response.RoomParticipantCount;
import com.vip.interviewpartner.domain.room_participant.entity.RoomParticipant;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * RoomParticipant 엔티티에 대한 데이터 접근 리포지토리입니다.
 */
public interface RoomParticipantRepository extends JpaRepository<RoomParticipant, Long> {
    /**
     * 주어진 멤버 ID를 기반으로 RoomParticipant 객체를 페이징하여 조회합니다. Room 엔티티를 함께 페치 조인(fetch join)하여 조회합니다.
     *
     * @param memberId 조회할 멤버의 ID
     * @param pageable 페이징 정보
     * @return 페이징된 RoomParticipant 객체 목록
     */
    Page<RoomParticipant> findByMemberId(Long memberId, Pageable pageable);

    /**
     * 주어진 참가자 ID로 RoomParticipant 객체를 조회합니다. Room, RoomTags, Tag, Member, Resume 엔티티를 함께 페치 조인(fetch join)하여 조회합니다.
     *
     * @param id 조회할 RoomParticipant의 ID
     * @return 조회된 RoomParticipant 객체, 존재하지 않을 경우 Optional.empty()
     */
    @Query("SELECT rp FROM RoomParticipant rp " +
            "LEFT JOIN FETCH rp.room r " +
            "LEFT JOIN FETCH r.roomTags rt " +
            "LEFT JOIN FETCH rt.tag " +
            "LEFT JOIN FETCH rp.member " +
            "LEFT JOIN FETCH rp.resume " +
            "WHERE rp.id = :id")
    Optional<RoomParticipant> findWithDetailsById(Long id);

    /**
     * 현재 방에 참여 중인 참가자 수를 반환하는 메서드입니다.
     */
    @Query("SELECT COUNT(rp) FROM RoomParticipant rp WHERE rp.room.id = :roomId AND rp.leaveDate IS NULL")
    int countByRoomIdAndCurrentlyJoined(Long roomId);

    @Query("""
                SELECT new com.vip.interviewpartner.domain.room.dto.response.RoomParticipantCount(rp.room.id, COUNT(rp))
                FROM RoomParticipant rp
                WHERE rp.room.id IN :roomIds AND rp.leaveDate IS NULL
                GROUP BY rp.room.id
            """)
    List<RoomParticipantCount> countCurrentlyJoinedByRoomIds(@Param("roomIds") List<Long> roomIds);

}
