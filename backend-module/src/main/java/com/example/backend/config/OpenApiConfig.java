package com.example.backend.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
    info = @Info(
        title = "Beneficio Management API",
        version = "1.0.0",
        description = """
            API REST para gerenciamento de benefícios de funcionários.
            
            **Funcionalidades principais:**
            - CRUD completo de benefícios
            - Transferências entre benefícios com validações
            - Soft delete (exclusão lógica)
            - Validação de saldo e status
            
            **Tecnologias:**
            - Spring Boot 3.2.0
            - Spring Data JPA
            - Jakarta Validation
            - H2 Database (dev) / PostgreSQL (prod)
            
            **Autenticação:**
            Esta versão não implementa autenticação. Em produção, recomenda-se
            implementar OAuth2/JWT para segurança dos endpoints.
            """,
        contact = @Contact(
            name = "Sistema BIP",
            email = "suporte@beneficios.com",
            url = "https://beneficios.com"
        ),
        license = @License(
            name = "Apache 2.0",
            url = "https://www.apache.org/licenses/LICENSE-2.0"
        )
    ),
    servers = {
        @Server(
            url = "http://localhost:8080",
            description = "Servidor de Desenvolvimento (Local)"
        ),
        @Server(
            url = "https://api-dev.beneficios.com",
            description = "Servidor de Desenvolvimento (Cloud)"
        ),
        @Server(
            url = "https://api.beneficios.com",
            description = "Servidor de Produção"
        )
    }
)
public class OpenApiConfig {
    // Configuração declarativa via anotações
    // Não é necessário código adicional
}
