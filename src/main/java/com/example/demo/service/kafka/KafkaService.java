package com.example.demo.service.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaService {

    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaService.class);

    @KafkaListener(topics = "createuser", groupId = "group-id")
    public void consume(String messages) {
        LOGGER.info(String.format("Message received -> %s", messages));
    }

}
