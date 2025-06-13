package com.vip.interviewpartner.domain.room.service;

import com.vip.interviewpartner.common.dto.PageCustom;
import com.vip.interviewpartner.domain.room.dto.response.RoomParticipantCount;
import com.vip.interviewpartner.domain.room.dto.response.RoomResponseDTO;
import com.vip.interviewpartner.domain.room.enitty.Room;
import com.vip.interviewpartner.domain.room.enitty.RoomStatus;
import com.vip.interviewpartner.domain.room.repository.RoomRepository;
import com.vip.interviewpartner.domain.room_participant.repository.RoomParticipantRepository;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * RoomLookupService는 방의 상태에 따라 방 목록을 조회하는 서비스입니다.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RoomLookupService {

    private final RoomParticipantRepository roomParticipantRepository;
    private final RoomRepository roomRepository;

    /**
     * 주어진 상태의 방 목록을 조회합니다.
     *
     * @param roomStatus 조회할 방의 상태
     * @param pageable   페이징 정보
     * @return 주어진 상태의 방 목록
     */
    public PageCustom<RoomResponseDTO> getRoomsByStatus(RoomStatus roomStatus, Pageable pageable) {
        Page<Room> rooms = roomRepository.findByStatus(roomStatus, pageable);

        List<Long> roomIds = rooms.stream()
                .map(Room::getId)
                .collect(Collectors.toList());

        Map<Long, Integer> participantCountMap = (roomStatus == RoomStatus.OPEN)
                ? getParticipantCount(roomIds)
                : Collections.emptyMap();

        Page<RoomResponseDTO> page = rooms.map(room ->
                RoomResponseDTO.of(room, participantCountMap.getOrDefault(room.getId(), 0))
        );
        return new PageCustom<>(page);
    }

    private Map<Long, Integer> getParticipantCount(List<Long> roomIds) {
        List<RoomParticipantCount> counts = roomParticipantRepository.countCurrentlyJoinedByRoomIds(roomIds);

        return counts.stream()
                .collect(Collectors.toMap(
                        RoomParticipantCount::roomId,
                        c -> c.count().intValue()
                ));
    }
}
