package com.example.lottery.repository;

import com.example.lottery.entity.Participant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;


public interface ParticipantRepository extends JpaRepository<Participant, Integer> {

    Participant findParticipantByName(String name);

    List<Participant> findAllBy();

    Integer countAllBy();

    @Query(value = "select min(p.id) from Participant p")
    Integer findMinId();

    @Query(value = "select max(p.id) from Participant p")
    Integer findMaxId();

}