package io.omnipede.data.didpipeline;

import io.omnipede.data.didpipeline.application.DidPipeline;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@SpringBootApplication
public class DidPipelineApplication implements CommandLineRunner {

    private final DidPipeline didPipeline;

    @Autowired
    public DidPipelineApplication(DidPipeline didPipeline) {
        this.didPipeline = didPipeline;
    }

    public static void main(String[] args) {
        SpringApplication.run(DidPipelineApplication.class, args);
    }

    /**
     * Application 이 시작하면 현재 data warehouse 에 저장된 마지막 block 부터
     * block chain 의 가장 마지막 블록까지 탐색하여 did 발급 정보를 동기화시킨다
     */
    @Override
    public void run(String... args) throws Exception {

        log.info("DID pipeline is executing ...");
        didPipeline.sync();
    }
}
