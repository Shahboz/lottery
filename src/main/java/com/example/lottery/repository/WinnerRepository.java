package com.example.lottery.repository;

import com.example.lottery.entity.Winner;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;


public interface WinnerRepository extends JpaRepository<Winner, Integer> {

    List<Winner> findAllBy();

}