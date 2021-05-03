package io.omnipede.data.didpipeline;

import io.omnipede.data.didpipeline.application.DidPipeline;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

@Slf4j
@EnableScheduling
@SpringBootApplication
public class DidPipelineApplication implements CommandLineRunner {

    private final DidPipeline didPipeline;

    private final ThreadPoolTaskScheduler threadPoolTaskScheduler;

    private static final int ONE_MINUTE = 60 * 1000;

    @Autowired
    public DidPipelineApplication(DidPipeline didPipeline, ThreadPoolTaskScheduler threadPoolTaskScheduler) {
        this.didPipeline = didPipeline;
        this.threadPoolTaskScheduler = threadPoolTaskScheduler;
    }

    public static void main(String[] args) {
        SpringApplication.run(DidPipelineApplication.class, args);
    }

    /**
     * Application 이 시작하면 현재 data warehouse 에 저장된 마지막 block 부터
     * block chain 의 가장 마지막 블록까지 탐색하여 did 발급 정보를 동기화시킨다. 동기화 이후 스케쥴링을 시작한다.
     */
    @Override
    public void run(String... args) throws Exception {

        log.info("DID pipeline is executing ...");

        log.info("[*] Synchronizing starts. And this job would take a long time ...");
        didPipeline.sync();
        log.info("[*] Synchronizing ends.");

        // Do scheduling every minutes
        log.info("[*] Scheduling starts for every {} ms", ONE_MINUTE);
        threadPoolTaskScheduler
                .scheduleAtFixedRate(didPipeline::sync, ONE_MINUTE);
    }
}
