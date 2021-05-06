package io.omnipede.data.didpipeline;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

@Slf4j
@SpringBootApplication
public class DidPipelineApplication {

    private final ThreadPoolTaskScheduler threadPoolTaskScheduler;

    private static final int ONE_MINUTE = 60 * 1000;

    @Autowired
    public DidPipelineApplication(ThreadPoolTaskScheduler threadPoolTaskScheduler) {

        this.threadPoolTaskScheduler = threadPoolTaskScheduler;
    }

    public static void main(String[] args) {
        SpringApplication.run(DidPipelineApplication.class, args);
    }
}
