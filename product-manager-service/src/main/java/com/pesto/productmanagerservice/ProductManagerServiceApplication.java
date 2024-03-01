package com.pesto.productmanagerservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = {"com.pesto.ecomm.common.lib.*","com.pesto.productmanagerservice.*"})
@EntityScan({"com.pesto.ecomm.common.lib.*"})
@EnableJpaRepositories({"com.pesto.ecomm.common.lib.repository"})
public class ProductManagerServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProductManagerServiceApplication.class, args);
    }

}
