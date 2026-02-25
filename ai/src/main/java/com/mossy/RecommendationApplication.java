package com.mossy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;

@SpringBootApplication(exclude = {
    JpaRepositoriesAutoConfiguration.class,
    HibernateJpaAutoConfiguration.class
})
@EnableR2dbcRepositories
@EnableFeignClients
@ComponentScan(
    basePackages = "com.mossy",
    excludeFilters = @ComponentScan.Filter(
        type = FilterType.REGEX,
        pattern = "com\\.mossy\\.kafka\\.outbox\\..*"
    )
)
public class RecommendationApplication {
    public static void main(String[] args) {
        SpringApplication.run(RecommendationApplication.class, args);
    }
}