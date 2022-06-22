package com.example.lottery.service;

import com.example.lottery.dto.ParticipantRepository;
import com.example.lottery.dto.WinnerRepository;
import com.example.lottery.entity.Participant;
import com.example.lottery.entity.Winner;
import com.example.lottery.exception.EmptyParticipantException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.List;


@Service
public class LotteryService {

    private final RestTemplate restTemplate;
    private final ParticipantRepository participantRepository;
    private final WinnerRepository winnerRepository;

    @Autowired
    public LotteryService(RestTemplate restTemplate, ParticipantRepository participantRepository, WinnerRepository winnerRepository) {
        this.restTemplate = restTemplate;
        this.participantRepository = participantRepository;
        this.winnerRepository = winnerRepository;
    }

    public String addParticipant(String name, Integer age, String town) {
        String result;
        if (StringUtils.isEmpty(name)) {
            result = "Не указано имя игрока!";
        } else if (age == null || age < 0) {
            result = "Некорректный возраст игрока!";
        } else if (StringUtils.isEmpty(town)) {
            result = "Не указан город участника!";
        } else {
            Participant participant = participantRepository.findParticipantByName(name);
            if (participant == null) {
                participant = new Participant();
                participant.setName(name);
                participant.setAge(age);
                participant.setTown(town);
                participantRepository.save(participant);
                result = "Игрок добавлен!";

            } else if (participant.getAge() != age || !participant.getTown().equals(town)) {
                participant.setAge(age);
                participant.setTown(town);
                participantRepository.save(participant);
                result = "Игрок обновлен!";

            } else {
                result = "Игрок уже участвует в игре!";
            }
        }
        return result;
    }

    public List<Participant> getAllParticipants() {
        return participantRepository.findAllBy();
    }

    public List<Winner> getWinners() {
        return winnerRepository.findAllBy();
    }

    private Integer getRandomValue(Integer minValue, Integer maxValue) {
        String requestUrl = "https://www.random.org/integers/" +
                "?num=1" +
                "&min="  + minValue +
                "&max="  + maxValue +
                "&col=1" +
                "&base=10" +
                "&format=plain" +
                "&rnd=new";
        return Integer.parseInt(restTemplate.getForObject(requestUrl, String.class).trim());
    }

    public Winner start() throws EmptyParticipantException {
        if (participantRepository.countAllBy() < 2) {
            throw new EmptyParticipantException("Количество игроков меньше 2");
        }
        Integer winAmount = getRandomValue(1, 1000);
        Integer winPlayerId = getRandomValue(participantRepository.findMinId(), participantRepository.findMaxId());
        Participant winPlayer = participantRepository.getOne(winPlayerId);

        Winner winner = new Winner(winPlayer, winAmount);
        winnerRepository.save(winner);
        participantRepository.deleteAll();

        return winner;
    }

}