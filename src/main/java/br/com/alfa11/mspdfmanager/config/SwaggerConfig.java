package br.com.alfa11.mspdfmanager.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI apiInfo() {
        return new OpenAPI().info(new Info().title("MS-PDF-Manager").version("1.0")
                .description("Este é um microsserviço desenvolvido com Spring Boot 3.2 e Java 21 capaz de armazenar arquivos pdf em um storage S3 compatível e em seguida fazer o merge de várias laminas (arquivos PDF) em um só documento. O serviço também é capaz de acrescentar informações customizadas em qualquer uma das prancas selecionadas através da descrição do body JSON correspondente. A aplicação de referência, quando executada a partir do Intellij faz o deploy de um containier com o Min.io (storage compatível com S3). Para utilizar uma conta S3 basta trocar as credenciais no arquivo de configuração. Não foram implementados testes ou segurança. Recomenda-se que estas funcionalidades sejam implementadas antes de colocar este serviço em produção.")
                .contact(getContactDetails()));
    }

    private Contact getContactDetails() {
        return new Contact().name("Elcio Abrahão")
                .email("elcioabrahao@alumni.usp.bt")
                .url("https://alfa11.com.br");
    }
}