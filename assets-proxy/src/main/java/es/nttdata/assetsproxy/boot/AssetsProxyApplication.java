package es.nttdata.assetsproxy.boot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = "es.nttdata.assetsproxy")
@EnableJpaRepositories(basePackages = "es.nttdata.assetsproxy.infrastructure.persistence.spring")
@EntityScan(basePackages = "es.nttdata.assetsproxy.infrastructure.persistence.entity")
public class AssetsProxyApplication {

    public static void main(String[] args) {
        SpringApplication.run(AssetsProxyApplication.class, args);
    }
}
