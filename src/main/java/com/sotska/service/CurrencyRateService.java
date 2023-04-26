package com.sotska.service;

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
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.sotska.entity.Currency.UAH;
import static java.util.stream.Collectors.toMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class CurrencyRateService {

    private static final DateTimeFormatter MONTH_FORMATTER = DateTimeFormatter.ofPattern("MM");
    private static final String AND_DETERMINER = "&";
    private static final String PARAMS_DETERMINER = "?";
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
        currencyRates = Arrays.stream(Currency.values()).filter(currency -> !UAH.equals(currency)).collect(toMap(currency -> currency, this::extractCurrencyRate));
        log.info("Currency rates was updated.");
    }

    @SneakyThrows
    private Double extractCurrencyRate(Currency currency) {
        var result = restTemplate.getForObject(createPath(currency.name()), String.class);
        return Double.parseDouble(objectMapper.readValue(result, ArrayNode.class).get(0).get("rate").asText());
    }

    private String createPath(String currency) {
        var date = LocalDate.now();
        var formattedDate = date.getYear() + date.format(MONTH_FORMATTER) + date.getDayOfMonth();

        var pathBuilder = new StringBuilder(nbuPath);
        pathBuilder.append(PARAMS_DETERMINER);
        pathBuilder.append("valcode=");
        pathBuilder.append(currency);
        pathBuilder.append(AND_DETERMINER);
        pathBuilder.append("date=");
        pathBuilder.append(formattedDate);
        pathBuilder.append(AND_DETERMINER);
        pathBuilder.append("json");

        return pathBuilder.toString();
    }
}
