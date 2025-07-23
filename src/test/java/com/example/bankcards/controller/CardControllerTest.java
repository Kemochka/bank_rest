package com.example.bankcards.controller;

import com.example.bankcards.TestMockConfig;
import com.example.bankcards.dto.CardCreateDto;
import com.example.bankcards.dto.TransferRequestDto;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.StatusCode;
import com.example.bankcards.service.TransferService;
import com.example.bankcards.service.card.CardService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CardController.class)
@Import(TestMockConfig.class)
class CardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CardService cardService;

    @Autowired
    private TransferService transferService;

    @Autowired
    private ObjectMapper objectMapper;

    private Card sampleCard;

    @BeforeEach
    void setUp() {
        sampleCard = new Card();
        sampleCard.setId(1L);
        sampleCard.setBalance(BigDecimal.valueOf(100));
        sampleCard.setStatus(StatusCode.ACTIVE);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getCardById_shouldReturnCard() throws Exception {
        when(cardService.findById(1L)).thenReturn(Optional.of(sampleCard));

        mockMvc.perform(get("/api/card/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createCard_shouldReturnCreated() throws Exception {
        CardCreateDto dto = new CardCreateDto();
        dto.setUserId(1L);
        dto.setBalance(BigDecimal.valueOf(100));
        dto.setStatus(StatusCode.ACTIVE);

        when(cardService.createCard(any(CardCreateDto.class))).thenReturn(sampleCard);

        mockMvc.perform(post("/api/card/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                        .with(csrf()))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(roles = "USER")
    void transferBetweenCards_shouldReturnNoContent() throws Exception {
        TransferRequestDto transferDto = new TransferRequestDto("4000000001", "4000000002", BigDecimal.valueOf(50));

        Mockito.doNothing().when(transferService).transferBetweenOwnCards(any(TransferRequestDto.class), any(Long.class));

        mockMvc.perform(post("/api/card/transfers/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transferDto))
                        .with(csrf()))
                .andExpect(status().isNoContent());
    }
}