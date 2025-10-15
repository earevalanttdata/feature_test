package es.nttdata.assetsproxy.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.context.annotation.Bean;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
public class AsyncConfig {

    /**
     * Define un pool de hilos para ejecutar tareas @Async.
     * Controlo concurrencia, colas y rendimiento.
     */
    @Bean(name = "taskExecutor")
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(4);       // número de hilos base
        executor.setMaxPoolSize(8);        // máximo de hilos simultáneos
        executor.setQueueCapacity(100);    // tamaño de cola
        executor.setThreadNamePrefix("AsyncUploader-");
        executor.initialize();
        return executor;
    }
}
