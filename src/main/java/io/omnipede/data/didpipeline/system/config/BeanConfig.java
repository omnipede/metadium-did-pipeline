package io.omnipede.data.didpipeline.system.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;

/**
 * 공용 bean configuration class
 */
@Configuration
class BeanConfig {

    /**
     * Bean for task scheduling
     * @return Task scheduler bean
     */
    @Bean
    public ThreadPoolTaskScheduler threadPoolTaskScheduler() {
        ThreadPoolTaskScheduler threadPoolTaskScheduler = new ThreadPoolTaskScheduler();
        threadPoolTaskScheduler.setPoolSize(1);
        threadPoolTaskScheduler.setThreadNamePrefix("did-pipeline-pool");
        return threadPoolTaskScheduler;
    }
}
