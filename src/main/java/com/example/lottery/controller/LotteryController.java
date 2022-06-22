package com.example.lottery.controller;

import com.example.lottery.entity.Participant;
import com.example.lottery.entity.Winner;
import com.example.lottery.exception.EmptyParticipantException;
import com.example.lottery.service.LotteryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import java.util.List;


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
    public String addParticipant(String name, Integer age, String town) {
        return lotteryService.addParticipant(name, age, town);
    }

    @GetMapping("/participant")
    @ResponseBody
    public List<Participant> getParticipants() {
        return lotteryService.getAllParticipants();
    }

    @GetMapping("/start")
    @ResponseBody
    public Winner start() throws EmptyParticipantException {
        return lotteryService.start();
    }

    @GetMapping("/winners")
    @ResponseBody
    public List<Winner> getWinners() {
        return lotteryService.getWinners();
    }

    @ExceptionHandler(EmptyParticipantException.class)
    @ResponseBody
    public String handleException(EmptyParticipantException exception) {
        return exception.getMessage();
    }

}