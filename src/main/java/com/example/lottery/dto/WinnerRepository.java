package com.example.lottery.dto;

import com.example.lottery.entity.Winner;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;


public interface WinnerRepository extends JpaRepository<Winner, Integer> {

    List<Winner> findAllBy();

}