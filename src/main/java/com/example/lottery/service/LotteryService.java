package com.example.lottery.service;

import com.example.lottery.dto.PlayerDto;
import com.example.lottery.repository.ParticipantRepository;
import com.example.lottery.repository.WinnerRepository;
import com.example.lottery.entity.Participant;
import com.example.lottery.entity.Winner;
import com.example.lottery.exception.ValidationException;
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
    public LotteryService(RestTemplate restTemplate,
                          ParticipantRepository participantRepository,
                          WinnerRepository winnerRepository) {
        this.restTemplate = restTemplate;
        this.participantRepository = participantRepository;
        this.winnerRepository = winnerRepository;
    }

    public List<Participant> getAllParticipants() {
        return participantRepository.findAllBy();
    }

    public List<Winner> getWinners() {
        return winnerRepository.findAllBy();
    }

    public String addParticipant(PlayerDto player) throws ValidationException {
        // Проверки
        if (StringUtils.isEmpty(player.getName())) {
            throw new ValidationException("Не указано имя игрока!");
        }

        if (player.getAge() == null || player.getAge() < 0) {
            throw new ValidationException("Некорректный возраст игрока!");
        }

        if (StringUtils.isEmpty(player.getTown())) {
            throw new ValidationException("Не указан город участника!");
        }

        String result;
        Participant participant = participantRepository.findParticipantByName(player.getName());
        if (participant == null) {
            participant = new Participant();
            participant.setName(player.getName());
            participant.setAge(player.getAge());
            participant.setTown(player.getTown());
            participantRepository.save(participant);
            result = "Игрок добавлен!";
        } else if (!participant.getAge().equals(player.getAge()) || !participant.getTown().equals(player.getTown())) {
            participant.setAge(player.getAge());
            participant.setTown(player.getTown());
            participantRepository.save(participant);
            result = "Игрок обновлен!";
        } else {
            result = "Игрок уже участвует в игре!";
        }

        return result;
    }

    public Integer getRandomValue(Integer minValue, Integer maxValue) {
        String requestUrl = String.format(
                "https://www.random.org/integers/?num=1&min=%d&max=%d&col=1&base=10&format=plain&rnd=new",
                minValue,
                maxValue
        );
        String randomValue = restTemplate.getForObject(requestUrl, String.class);
        return StringUtils.isEmpty(randomValue) ? null : Integer.parseInt(randomValue.trim());
    }

    public Winner start() throws ValidationException {
        if (participantRepository.countAllBy() < 2) {
            throw new ValidationException("Количество игроков меньше 2");
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