package com.example.lottery.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
public class PlayerDto {

    private String name;
    private Integer age;
    private String town;

    public PlayerDto(String name, Integer age, String town) {
        this.name = name;
        this.age = age;
        this.town = town;
    }

}