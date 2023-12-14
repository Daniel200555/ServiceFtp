package com.example.demo.service.stream;

import org.springframework.core.io.Resource;
import reactor.core.publisher.Mono;

public sealed interface StreamService permits StreamServiceImpl {

    Mono<Resource> getVideo(String title);

}