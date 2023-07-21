package com.example.ecommerceproject.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

@Configuration
public class SwaggerConfiguration {

  @Bean
  public Docket SwaggerApi(){
    return new Docket(DocumentationType.SWAGGER_2)
        .apiInfo(swaggerInfo())
        .select()
        .apis(RequestHandlerSelectors.basePackage("com.example.ecommerceproject.controller"))
        .paths(PathSelectors.any())
        .build();
  }

  private ApiInfo swaggerInfo() {
    return new ApiInfoBuilder().title("이커머스 프로젝트")
        .description("웹 서버 테스트를 위한 문서입니다.")
        .license("wookjong")
        .version("1")
        .build();
  }
}
