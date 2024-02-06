package uz.mediasolutions.saleservicebot.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.Parameter;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.service.ApiKey;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;

import java.util.Collections;
import java.util.List;

@Configuration
public class SpringFoxConfig {
    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.basePackage("uz.mediasolutions.saleservicebot.controller"))
                .paths(PathSelectors.any())
                .build()
//                .globalOperationParameters(Collections.singletonList(acceptLanguageHeader()))
                .securityContexts(List.of(securityContext()))
                .securitySchemes(List.of(apiKey()));
    }

//    private Parameter acceptLanguageHeader() {
//        return new ParameterBuilder()
//                .name("Accept-Language")
//                .description("Language header for internationalization")
//                .modelRef(new ModelRef("string"))
//                .parameterType("header")
//                .defaultValue("en-US")  // Default language, change as needed
//                .build();
//    }

    private SecurityContext securityContext() {
        return SecurityContext.builder().securityReferences(defaultAuth()).build();
    }
    private List<SecurityReference> defaultAuth() {
        AuthorizationScope authorizationScope = new AuthorizationScope("global", "accessEverything");
        AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
        authorizationScopes[0] = authorizationScope;
        return List.of(new SecurityReference("JWT", authorizationScopes));
    }


    private ApiKey apiKey() {
        return new ApiKey("JWT", "Authorization", "header");
    }
}