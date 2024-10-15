package rt.marson.syeta.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@EnableAsync
public class AsyncConfig {
    @Bean(name = "taskExecutor")
    public ThreadPoolTaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5); // минимальное количество потоков
        executor.setMaxPoolSize(10); // максимальное количество потоков
        executor.setQueueCapacity(50); // размер очереди задач
        executor.setThreadNamePrefix("Async-");
        executor.initialize();
        return executor;
    }

    @Bean(name = "threadPoolTaskExecutor")
    public ThreadPoolTaskExecutor threadPoolTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5); // минимум потоков
        executor.setMaxPoolSize(10); // максимум потоков
        executor.setQueueCapacity(50); // максимальная очередь
        executor.setThreadNamePrefix("FileUpload-");
        executor.initialize();
        return executor;
    }
}
