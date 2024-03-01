package com.pesto.ordermanagerservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = {"com.pesto.ecomm.common.lib.*","com.pesto.ordermanagerservice.*"})
@EntityScan({"com.pesto.ecomm.common.lib.*"})
@EnableJpaRepositories({"com.pesto.ecomm.common.lib.repository"})
public class OrderManagerServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(OrderManagerServiceApplication.class, args);
    }

}
