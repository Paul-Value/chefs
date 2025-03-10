package com.fooddelivery.chefs;

import com.fooddelivery.chefs.repository.ChefRepository;
import com.fooddelivery.chefs.repository.CustomerRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Sql(scripts = {
        "/sql/insert_test_chef.sql",
        "/sql/insert_test_customer.sql"
}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "/sql/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class SecurityIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ChefRepository chefRepository;

    @Autowired
    private CustomerRepository customerRepository;

    // Тест для доступа повара
    @Test
    void chefAccess_ValidCredentials_ReturnsOk() throws Exception {
        mockMvc.perform(get("/chefs/working-status")
                        .header("X-Access-Code", "CHEF_ACCESS_CODE")
                        .header("X-Device-Id", "CHEF_DEVICE_ID"))
                .andExpect(status().isOk());
    }

    // Тест для доступа заказчика
    @Test
    void customerAccess_ValidDeviceId_ReturnsOk() throws Exception {
        mockMvc.perform(post("/addresses")
                        .header("X-Device-Id", "CUSTOMER_DEVICE_ID"))
                .andExpect(status().isOk());
    }

    // Тест с неверным access_code для повара
    @Test
    void chefAccess_InvalidAccessCode_ReturnsUnauthorized() throws Exception {
        mockMvc.perform(get("/chefs/working-status")
                        .header("X-Access-Code", "INVALID_CODE")
                        .header("X-Device-Id", "CHEF_DEVICE_ID"))
                .andExpect(status().isUnauthorized());
    }

    // Тест без device_id для заказчика
    @Test
    void customerAccess_MissingDeviceId_ReturnsUnauthorized() throws Exception {
        mockMvc.perform(post("/addresses"))
                .andExpect(status().isUnauthorized());
    }
}