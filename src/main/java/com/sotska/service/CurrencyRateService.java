package com.sotska.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.sotska.entity.Currency;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class CurrencyRateService {

    private static final DateTimeFormatter MONTH_FORMATTER = DateTimeFormatter.ofPattern("MM");
    private static final String AND_DETERMINER = "&";
    private static final String PARAMS_DETERMINER = "?";
    public static final String CURRENCY_NAME_KEY = "cc";

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    private Map<Currency, Double> currencyRates;

    @Value("${service.path.nbu}")
    private String nbuPath;

    public Double getCurrencyRate(Currency currency) {
        return currencyRates.get(currency);
    }

    @Scheduled(fixedDelayString = "${cache.time-to-live.currency-rates}", timeUnit = TimeUnit.HOURS)
    private void updateCurrencyRates() {
        currencyRates = extractCurrencyRates(List.of("USD", "EUR"));
        log.info("Currency rates was updated.");
    }

    @SneakyThrows
    private Map<Currency, Double> extractCurrencyRates(List<String> currencies) {
        var nbuResponse = restTemplate.getForObject(createPath(), String.class);

        var arrayNodes = objectMapper.readValue(nbuResponse, ArrayNode.class);
        var currencyRates = new HashMap<Currency, Double>();

        for (var iterator = arrayNodes.elements(); iterator.hasNext(); ) {
            var node = iterator.next();
            var currencyName = node.get(CURRENCY_NAME_KEY).asText();

            if (currencies.contains(currencyName)) {
                currencyRates.put(Currency.valueOf(currencyName), node.get(CURRENCY_NAME_KEY).doubleValue());
            }
        }
        return currencyRates;
    }

    private String createPath() {
        var date = LocalDate.now();
        var formattedDate = date.getYear() + date.format(MONTH_FORMATTER) + date.getDayOfMonth();

        var pathBuilder = new StringBuilder(nbuPath);
        pathBuilder.append(PARAMS_DETERMINER);
        pathBuilder.append("date=");
        pathBuilder.append(formattedDate);
        pathBuilder.append(AND_DETERMINER);
        pathBuilder.append("json");

        return pathBuilder.toString();
    }
}
