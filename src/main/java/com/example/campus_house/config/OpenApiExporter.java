package com.example.campus_house.config;

import io.swagger.v3.oas.models.OpenAPI;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Path;

@Component
public class OpenApiExporter implements ApplicationRunner {

    private final OpenAPI openAPI;

    public OpenApiExporter(OpenAPI openAPI) {
        this.openAPI = openAPI;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        String yaml = io.swagger.v3.core.util.Yaml.mapper().writeValueAsString(openAPI);
        Path out = Path.of("build", "openapi.yaml");
        Files.createDirectories(out.getParent());
        Files.writeString(out, yaml);
    }
}


