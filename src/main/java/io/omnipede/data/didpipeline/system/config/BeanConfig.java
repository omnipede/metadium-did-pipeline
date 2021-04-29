package io.omnipede.data.didpipeline.system.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;

@Configuration
class BeanConfig {

    @Bean
    public Web3j web3j() {
        return Web3j.build(new HttpService("https://api.metadium.com/prod"));
    }
}
