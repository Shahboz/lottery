package com.example.lottery.controller;

import com.example.lottery.dto.PlayerDto;
import com.example.lottery.entity.Participant;
import com.example.lottery.entity.Winner;
import com.example.lottery.exception.ValidationException;
import com.example.lottery.service.LotteryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import java.util.List;


@Slf4j
@Controller
@RequestMapping("/lottery")
public class LotteryController {

    private final LotteryService lotteryService;

    @Autowired
    public LotteryController(LotteryService lotteryService) {
        this.lotteryService = lotteryService;
    }

    @PostMapping("/participant")
    @ResponseBody
    @ResponseStatus(HttpStatus.CREATED)
    public String addParticipant(@RequestBody PlayerDto playerDto) throws ValidationException {
        return lotteryService.addParticipant(playerDto);
    }

    @GetMapping("/participant")
    @ResponseBody
    public List<Participant> getParticipants() {
        return lotteryService.getAllParticipants();
    }

    @GetMapping("/start")
    @ResponseBody
    public Winner start() throws ValidationException {
        return lotteryService.start();
    }

    @GetMapping("/winners")
    @ResponseBody
    public List<Winner> getWinners() {
        return lotteryService.getWinners();
    }

    @ExceptionHandler(ValidationException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleException(ValidationException exception) {
        log.error("Ошибка валидации данных", exception);
        return exception.getMessage();
    }

}