package com.vip.interviewpartner.service;

import com.vip.interviewpartner.domain.Member;
import com.vip.interviewpartner.domain.Resume;
import com.vip.interviewpartner.domain.Room;
import com.vip.interviewpartner.domain.RoomParticipant;
import com.vip.interviewpartner.repository.RoomParticipantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * RoomParticipantService 클래스는 방 참가자와 관련된 비즈니스 로직을 처리합니다.
 * 주어진 멤버, 이력서, 방 정보를 기반으로 RoomParticipant 객체를 생성하고 저장하는 기능을 제공합니다.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RoomParticipantService {

    private final RoomParticipantRepository roomParticipantRepository;

    /**
     * 주어진 멤버, 이력서, 방 정보를 기반으로 RoomParticipant 객체를 생성하고 저장합니다.
     *
     * @param member 멤버 객체
     * @param resume 이력서 객체
     * @param room   방 객체
     * @return 저장된 RoomParticipant 객체
     */
    @Transactional
    public RoomParticipant save(Member member, Resume resume, Room room) {
        RoomParticipant roomParticipant = RoomParticipant.builder()
                .member(member)
                .room(room)
                .resume(resume)
                .build();
        roomParticipantRepository.save(roomParticipant);
        return roomParticipant;
    }
}
