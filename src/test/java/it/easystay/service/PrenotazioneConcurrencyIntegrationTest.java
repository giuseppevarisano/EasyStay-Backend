package it.easystay.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.easystay.dto.PrenotazioneRequestDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

import java.time.LocalDate;
import java.util.concurrent.*;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@SpringBootTest
@ActiveProfiles("h2") // <--- Questo dice a Spring: "Ignora MySQL, usa application-test.properties"
@AutoConfigureMockMvc
/*Web Integration Test (MockMvc): Testi Controller + Service + Database. È il test più completo ("Verticale").*/
public class PrenotazioneConcurrencyIntegrationTest {

    @Autowired
    private MockMvc mockMvc; //simula chiamate HTTP senza server reale

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void quandoDueClientContemporanei_alloraSoloUnoCreaLaPrenotazione() throws Exception {
        PrenotazioneRequestDTO request = new PrenotazioneRequestDTO();
        request.setCasaId(1L);
        request.setDataInizio(LocalDate.now().plusDays(30));
        request.setDataFine(LocalDate.now().plusDays(35));

        int threads = 2;
        ExecutorService exec = Executors.newFixedThreadPool(threads);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch doneLatch = new CountDownLatch(threads);

        List<Integer> statuses = new CopyOnWriteArrayList<>();

        for (int i = 0; i < threads; i++) {
            exec.submit(() -> {
                try {
                    startLatch.await();
                    var mvcResult = mockMvc.perform(post("/api/prenotazioni")
                            .with(user("utente1@esempio.it").roles("USER"))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                            .andReturn();

                    statuses.add(mvcResult.getResponse().getStatus());
                } catch (Exception e) {
                    statuses.add(500);
                } finally {
                    doneLatch.countDown();
                }
            });
        }

        startLatch.countDown();
        doneLatch.await(15, TimeUnit.SECONDS);
        exec.shutdownNow();

        assertThat(statuses).hasSize(2);
        assertThat(statuses).containsExactlyInAnyOrder(201, 409);
    }
}
