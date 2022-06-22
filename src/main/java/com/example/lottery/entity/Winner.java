package com.example.lottery.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import javax.persistence.*;


@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "winners")
public class Winner {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "player_name", nullable = false)
    private String playerName;

    @Column(name = "player_age", nullable = false)
    private Integer playerAge;

    @Column(name = "player_town", nullable = false)
    private String playerTown;

    @Column(name = "win_amount", nullable = false)
    private Integer winAmount;

    public Winner(Participant winPlayer, Integer winAmount) {
        this.playerName = winPlayer.getName();
        this.playerAge = winPlayer.getAge();
        this.playerTown = winPlayer.getTown();
        this.winAmount = winAmount;
    }

}