package com.example.lottery.controller;

import com.example.lottery.dto.ParticipantRepository;
import com.example.lottery.dto.WinnerRepository;
import com.example.lottery.entity.Participant;
import com.example.lottery.entity.Winner;
import com.example.lottery.exception.EmptyParticipantException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureMockMvc
class LotteryControllerTest {

    private final MockMvc mockMvc;

    private Participant participant;
    private Winner winner;
    private String playerJson;

    @MockBean
    private ParticipantRepository participantRepository;
    @MockBean
    private WinnerRepository winnerRepository;
    @Mock
    private RestTemplate restTemplate;

    @Autowired
    LotteryControllerTest(MockMvc mockMvc) {
        this.mockMvc = mockMvc;
    }

    @BeforeEach
    void setUp() throws JsonProcessingException {
        participant = new Participant();
        participant.setId(1);
        participant.setName("Player 1");
        participant.setAge(20);
        participant.setTown("Town 1");

        winner = new Winner(participant, 50);
        playerJson = new ObjectMapper().writer().withDefaultPrettyPrinter().writeValueAsString(winner);
    }

    @AfterEach
    void tearDown() {
        participant = null;
        winner = null;
        playerJson = null;
    }

    @Test
    void addParticipantSuccess() throws Exception {
        MultiValueMap<String, String> requestParams = new LinkedMultiValueMap<>();
        requestParams.add("name", participant.getName());
        requestParams.add("age", participant.getAge().toString());
        requestParams.add("town", participant.getTown());

        mockMvc.perform(MockMvcRequestBuilders
                .post("/lottery/participant")
                .params(requestParams))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Игрок добавлен")));

        Mockito.verify(participantRepository, Mockito.times(1)).save(Mockito.any(Participant.class));
        Mockito.verify(winnerRepository, Mockito.times(0)).save(Mockito.any(Winner.class));
    }

    @Test
    void addParticipantFailure() throws Exception {
        MultiValueMap<String, String> requestParams = new LinkedMultiValueMap<>();
        requestParams.add("name", participant.getName());

        mockMvc.perform(MockMvcRequestBuilders
                .post("/lottery/participant")
                .params(requestParams))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(mvcResult -> assertTrue(mvcResult.getResolvedException() instanceof EmptyParticipantException))
                .andExpect(mvcResult -> assertNotNull(mvcResult.getResolvedException().getMessage()));

        Mockito.verify(participantRepository, Mockito.times(0)).save(Mockito.any(Participant.class));
        Mockito.verify(winnerRepository, Mockito.times(0)).save(Mockito.any(Winner.class));
    }

    @Test
    void getParticipantsSuccess() throws Exception {
        List<Participant> participantList = new ArrayList<>();
        participantList.add(participant);

        Mockito.doReturn(participantList).when(participantRepository).findAllBy();

        mockMvc.perform(get("/lottery/participant"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*", hasSize(1)));
    }

    @Test
    void getParticipantsFailure() throws Exception {
        Mockito.doReturn(new ArrayList<>()).when(participantRepository).findAllBy();

        mockMvc.perform(get("/lottery/participant"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*", hasSize(0)));
    }

    @Test
    void startSuccess() throws Exception {
        Mockito.doReturn(2).when(participantRepository).countAllBy();
        Mockito.doReturn(1).when(participantRepository).findMinId();
        Mockito.doReturn(2).when(participantRepository).findMaxId();
        Mockito.doReturn(participant).when(participantRepository).getOne(Mockito.any(Integer.class));
        Mockito.doReturn(winner).when(winnerRepository).save(Mockito.any(Winner.class));
        Mockito.when(restTemplate.getForObject("https://www.random.org/*", String.class)).thenReturn(winner.getWinAmount().toString());

        mockMvc.perform(get("/lottery/start"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(equalTo(playerJson)));

        Mockito.verify(participantRepository, Mockito.times(1)).deleteAll();
        Mockito.verify(winnerRepository, Mockito.times(1)).save(Mockito.any(Winner.class));
    }

    @Test
    void startFailure() throws Exception {
        Mockito.doReturn(1).when(participantRepository).countAllBy();

        mockMvc.perform(get("/lottery/start"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(mvcResult -> assertTrue(mvcResult.getResolvedException() instanceof EmptyParticipantException))
                .andExpect(mvcResult -> assertNotNull(mvcResult.getResolvedException().getMessage()));


        Mockito.verify(participantRepository, Mockito.times(0)).save(Mockito.any(Participant.class));
        Mockito.verify(winnerRepository, Mockito.times(0)).save(Mockito.any(Winner.class));
    }

    @Test
    void getWinnersSuccess() throws Exception {
        List<Winner> winnerList = new ArrayList<>();
        winnerList.add(winner);

        Mockito.doReturn(winnerList).when(winnerRepository).findAllBy();

        mockMvc.perform(get("/lottery/winners"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*", hasSize(1)));
    }

    @Test
    void getWinnersFailure() throws Exception {
        Mockito.doReturn(new ArrayList<>()).when(winnerRepository).findAllBy();

        mockMvc.perform(get("/lottery/winners"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*", hasSize(0)));
    }

}