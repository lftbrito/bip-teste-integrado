package com.example.backend.config;

import com.example.backend.mapper.BeneficioMapper;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

@TestConfiguration
public class TestConfig {

    @Bean
    @Primary
    public BeneficioMapper beneficioMapper() {
        return Mockito.mock(BeneficioMapper.class);
    }
}
