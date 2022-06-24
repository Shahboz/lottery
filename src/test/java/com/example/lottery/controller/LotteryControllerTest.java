package com.example.lottery.controller;

import com.example.lottery.entity.Participant;
import com.example.lottery.entity.Winner;
import com.example.lottery.exception.ValidationException;
import com.example.lottery.repository.ParticipantRepository;
import com.example.lottery.repository.WinnerRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import java.util.ArrayList;
import java.util.List;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureMockMvc
class LotteryControllerTest {

    private final MockMvc mockMvc;

    private Participant participant;
    private Winner winner;

    @MockBean
    private ParticipantRepository participantRepository;
    @MockBean
    private WinnerRepository winnerRepository;

    @Autowired
    LotteryControllerTest(MockMvc mockMvc) {
        this.mockMvc = mockMvc;
    }

    @BeforeEach
    void setUp() {
        participant = new Participant();
        participant.setId(1);
        participant.setName("PlayerDto 1");
        participant.setAge(20);
        participant.setTown("Town 1");

        winner = new Winner(participant, 50);
    }

    @AfterEach
    void tearDown() {
        participant = null;
        winner = null;
    }

    @Test
    void addParticipantSuccess() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders
                .post("/lottery/participant")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\": \"" + participant.getName() + "\", \"age\":" + participant.getAge() + ", \"town\": \"" + participant.getTown() + "\"}"))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().string(containsString("Игрок добавлен")));

        Mockito.verify(participantRepository, Mockito.times(1)).save(Mockito.any(Participant.class));
        Mockito.verify(winnerRepository, Mockito.times(0)).save(Mockito.any(Winner.class));
    }

    @Test
    void addParticipantFailure() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders
                .post("/lottery/participant")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\": \"" + participant.getName() + "\"}"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(mvcResult -> Assertions.assertTrue(mvcResult.getResolvedException() instanceof ValidationException))
                .andExpect(mvcResult -> Assertions.assertNotNull(mvcResult.getResolvedException()))
                .andExpect(mvcResult -> Assertions.assertNotNull(mvcResult.getResolvedException().getMessage()));

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

        mockMvc.perform(get("/lottery/start"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.playerName").value(winner.getPlayerName()))
                .andExpect(jsonPath("$.playerAge").value(winner.getPlayerAge()))
                .andExpect(jsonPath("$.playerTown").value(winner.getPlayerTown()));

        Mockito.verify(participantRepository, Mockito.times(1)).deleteAll();
        Mockito.verify(winnerRepository, Mockito.times(1)).save(Mockito.any(Winner.class));
    }

    @Test
    void startFailure() throws Exception {
        Mockito.doReturn(1).when(participantRepository).countAllBy();

        mockMvc.perform(get("/lottery/start"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(mvcResult -> Assertions.assertTrue(mvcResult.getResolvedException() instanceof ValidationException))
                .andExpect(mvcResult -> Assertions.assertNotNull(mvcResult.getResolvedException()))
                .andExpect(mvcResult -> Assertions.assertNotNull(mvcResult.getResolvedException().getMessage()));

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