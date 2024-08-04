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
        return new OpenAPI().info(new Info().title("MS-Feature-Flag VExpenses")
                .description("Descrição...")
                .version("v1.0")
                .contact(getContactDetails()));
    }

    private Contact getContactDetails() {
        return new Contact().name("Elcio Abrahão")
                .email("elcio.abrahao@vr.com.br")
                .url("https://vexpenses.com");
    }
}