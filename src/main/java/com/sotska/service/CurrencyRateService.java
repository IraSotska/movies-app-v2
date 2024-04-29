package com.sotska.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.sotska.entity.Currency;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.net.URI;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static com.sotska.entity.Currency.EUR;
import static com.sotska.entity.Currency.USD;
import static java.util.stream.Collectors.toList;

@Slf4j
@Service
@RequiredArgsConstructor
public class CurrencyRateService {

    private static final DateTimeFormatter MONTH_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");
    public static final String CURRENCY_NAME_KEY = "cc";

    private final ObjectMapper objectMapper;
    private final WebClient webClient;

    private Map<Currency, Double> currencyRates;

    @Value("${service-path-nbu}")
    private String nbuPath;

    public Double getCurrencyRate(Currency currency) {
        return currencyRates.get(currency);
    }

    @PostConstruct
    @Scheduled(initialDelayString = "${cache.time-to-live-hours.currency-rates}",
            fixedDelayString = "${cache.time-to-live-hours.currency-rates}", timeUnit = TimeUnit.HOURS)
    private void updateCurrencyRates() {
        currencyRates = extractCurrencyRates(List.of(USD, EUR));
        log.info("Currency rates was updated.");
    }

    @SneakyThrows
    private Map<Currency, Double> extractCurrencyRates(List<Currency> currencies) {

        var nbuResponse = webClient
                .get()
                .uri(new URI(createPath()))
                .retrieve()
                .bodyToMono(String.class).block();

        HashMap<Currency, Double> currencyRates = extractCurrencies(currencies, nbuResponse);
        return currencyRates;
    }

    public String createPath() {
        var date = LocalDate.now();
        // NBU return next day currencies after 6 PM
        var formattedDate = date.format(MONTH_FORMATTER);

        var pathBuilder = new StringBuilder(nbuPath);
        pathBuilder.append("?date=");
        pathBuilder.append(formattedDate);
        pathBuilder.append("&json");

        return pathBuilder.toString();
    }

    protected HashMap<Currency, Double> extractCurrencies(List<Currency> currencies, String nbuResponse) throws com.fasterxml.jackson.core.JsonProcessingException {
        var arrayNodes = objectMapper.readValue(nbuResponse, ArrayNode.class);
        var currencyRates = new HashMap<Currency, Double>();
        var currenciesAsString = currencies.stream().map(Enum::name).collect(toList());

        for (var iterator = arrayNodes.elements(); iterator.hasNext(); ) {
            var node = iterator.next();

            var currencyName = node.get(CURRENCY_NAME_KEY).asText();
            if (currenciesAsString.contains(currencyName)) {
                currencyRates.put(Currency.valueOf(currencyName), node.get("rate").doubleValue());
            }
        }
        return currencyRates;
    }
}
