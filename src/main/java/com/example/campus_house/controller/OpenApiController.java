package com.example.campus_house.controller;

import io.swagger.v3.oas.models.OpenAPI;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class OpenApiController {

    private final OpenAPI openAPI;

    public OpenApiController(OpenAPI openAPI) {
        this.openAPI = openAPI;
    }

    @GetMapping(value = "/openapi.yaml", produces = "application/yaml")
    public ResponseEntity<String> getOpenApiYaml() {
        try {
            String yaml = io.swagger.v3.core.util.Yaml.mapper().writeValueAsString(openAPI);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_TYPE, "application/yaml;charset=UTF-8")
                    .body(yaml);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().contentType(MediaType.TEXT_PLAIN)
                    .body("Failed to generate OpenAPI YAML: " + e.getMessage());
        }
    }
}


