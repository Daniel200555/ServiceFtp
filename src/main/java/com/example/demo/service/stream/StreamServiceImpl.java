package com.example.demo.service.stream;

import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public non-sealed class StreamServiceImpl implements StreamService {

    private ResourceLoader resourceLoader;
    private Environment environment;
    private String path;

    public StreamServiceImpl(ResourceLoader resourceLoader, Environment environment) {
        this.resourceLoader = resourceLoader;
        this.environment = environment;
        this.path = environment.getProperty("ftp.link");
    }

    @Override
    public Mono<Resource> getVideo(String title) {
        return Mono.fromSupplier(() -> resourceLoader.getResource(path + title));
    }
}
