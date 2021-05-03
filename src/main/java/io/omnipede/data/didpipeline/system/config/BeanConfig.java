package io.omnipede.data.didpipeline.system.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;

@Configuration
class BeanConfig {

    @Bean
    public Web3j web3j() {
        return Web3j.build(new HttpService("https://api.metadium.com/prod"));
    }

    /**
     * Bean for task scheduling
     * @return Task scheduler bean
     */
    @Bean
    public ThreadPoolTaskScheduler threadPoolTaskScheduler() {
        ThreadPoolTaskScheduler threadPoolTaskScheduler = new ThreadPoolTaskScheduler();
        threadPoolTaskScheduler.setPoolSize(1);
        threadPoolTaskScheduler.setThreadNamePrefix("ThreadPoolTaskScheduler");
        return threadPoolTaskScheduler;
    }
}
