package com.courier.logistics;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class CourierLogisticsApplicationTests {
    @Autowired private MockMvc mockMvc;

    @Test void contextLoads() { }

    @Test void customersEndpointIsAvailable() throws Exception {
        mockMvc.perform(get("/api/customers")).andExpect(status().isOk());
    }

    @Test void packagesEndpointIsAvailable() throws Exception {
        mockMvc.perform(get("/api/packages")).andExpect(status().isOk());
    }

    @Test void rejectsContactNumbersThatAreNotTenDigits() throws Exception {
        mockMvc.perform(post("/api/customers").contentType(APPLICATION_JSON)
                        .content("{\"name\":\"Test Customer\",\"email\":\"invalid-contact@example.com\",\"contact\":\"12345\",\"address\":\"Test address\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation failed"));
    }
}
