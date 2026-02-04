package it.easystay.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.easystay.dto.PrenotazioneRequestDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest // Carica intero contesto Spring - Integration/E2E
@ActiveProfiles("h2") // Usa DB reale (H2) - Integration
@AutoConfigureMockMvc
@Transactional // Rollback automatico dopo test - Integration
/*Web Integration Test (MockMvc): Testi Controller + Service + Database. È il test più completo ("Verticale").*/
class PrenotazioneServiceIntegrationTest {

    @Autowired
    private MockMvc mockMvc; // Simula chiamate HTTP al Controller - ✅ API Test

    @Autowired
    private ObjectMapper objectMapper; // Per trasformare oggetti in JSON

    @Test
    @WithMockUser(username = "utente1@esempio.it", roles = "USER") // Simula un "Security Context" popolato, autenticazione Spring Security - ✅ Security Test
    void testCreazionePrenotazioneE2E() throws Exception {
        PrenotazioneRequestDTO request = new PrenotazioneRequestDTO();
        request.setCasaId(1L);
        request.setDataInizio(LocalDate.now().plusDays(20));
        request.setDataFine(LocalDate.now().plusDays(25));

        mockMvc.perform(post("/api/prenotazioni")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated()) // Verifica che torni 201 Created
                .andExpect(jsonPath("$.id").exists()); // Verifica che ci sia un ID nella risposta
    }
}
