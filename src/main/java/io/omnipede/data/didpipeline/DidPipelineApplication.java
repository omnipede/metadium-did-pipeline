package io.omnipede.data.didpipeline;

import io.omnipede.data.didpipeline.application.DidPipeline;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

@Slf4j
@SpringBootApplication
@EnableScheduling
public class DidPipelineApplication implements CommandLineRunner {

    private final ThreadPoolTaskScheduler threadPoolTaskScheduler;

    private final DidPipeline didPipeline;

    private static final int TEN_SECONDS = 10 * 1000;

    @Autowired
    public DidPipelineApplication(ThreadPoolTaskScheduler threadPoolTaskScheduler, DidPipeline didPipeline) {

        this.threadPoolTaskScheduler = threadPoolTaskScheduler;
        this.didPipeline = didPipeline;
    }

    public static void main(String[] args) {
        SpringApplication.run(DidPipelineApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        threadPoolTaskScheduler.scheduleAtFixedRate(didPipeline::process, TEN_SECONDS);
    }
}
