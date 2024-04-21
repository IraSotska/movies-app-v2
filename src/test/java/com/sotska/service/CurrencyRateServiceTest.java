package com.sotska.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sotska.controller.ITest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.reactive.function.client.WebClient;

import java.net.URISyntaxException;
import java.util.List;

import static com.sotska.entity.Currency.EUR;
import static com.sotska.entity.Currency.USD;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {CurrencyRateService.class, ObjectMapper.class})
class CurrencyRateServiceTest extends ITest {

    @MockBean
    private WebClient webClient;

    @Autowired
    private CurrencyRateService currencyRateService;

    @Test
    void shouldExtractCurrenciesFromString() throws JsonProcessingException, URISyntaxException {
        var result = currencyRateService.extractCurrencies(List.of(USD, EUR), contentOf("com/sotska/service/nbu_response.json"));
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.containsKey(EUR));
        assertTrue(result.containsKey(USD));
        assertEquals(41.2968D, result.get(EUR));
        assertEquals(38.2077D, result.get(USD));
    }
}