package es.nttdata.assetsproxy;

import org.springframework.boot.SpringApplication;

public class TestAssetsProxyApplication {

	public static void main(String[] args) {
		SpringApplication.from(AssetsProxyApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
