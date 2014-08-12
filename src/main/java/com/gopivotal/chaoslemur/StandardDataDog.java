/*
 * Copyright 2014 Pivotal Software, Inc. All Rights Reserved.
 */

package com.gopivotal.chaoslemur;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Component
final class StandardDataDog implements DataDog {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private static final String URI = "https://app.datadoghq.com/" +
            "api/v1/events?api_key={apiKey}&application_key={appKey}";

    private final String apiKey;

    private final String appKey;

    private final RestTemplate restTemplate;

    @Autowired
    StandardDataDog(@Value("${dataDog.apiKey}") String apiKey, @Value("${dataDog.appKey}") String appKey,
                    RestTemplate restTemplate) {
        this.apiKey = apiKey;
        this.appKey = appKey;
        this.restTemplate = restTemplate;
    }

    @Override
    public void sendEvent(String title, String message) {
        Map<String, String> payload = new HashMap<>();
        payload.put("title", title);
        payload.put("text", message);

        try {
            this.restTemplate.postForEntity(URI, payload, Void.class, this.apiKey, this.appKey);
        } catch (HttpClientErrorException e) {
            this.logger.error(e.getResponseBodyAsString());
        }
    }
}
