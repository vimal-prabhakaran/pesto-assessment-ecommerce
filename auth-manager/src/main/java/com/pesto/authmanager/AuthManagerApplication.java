package com.pesto.authmanager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = {"com.pesto.ecomm.common.lib.*","com.pesto.authmanager.*"})
@EntityScan({"com.pesto.ecomm.common.lib.*"})
@EnableJpaRepositories({"com.pesto.ecomm.common.lib.repository"})
public class AuthManagerApplication {

    public static void main(String[] args) {
        SpringApplication.run(AuthManagerApplication.class, args);
    }

}
