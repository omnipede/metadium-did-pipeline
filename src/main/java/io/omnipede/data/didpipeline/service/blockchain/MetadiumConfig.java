package io.omnipede.data.didpipeline.service.blockchain;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.Keys;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.gas.DefaultGasProvider;

import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

/**
 * Block chain 관련 bean configuration class
 */
@Configuration
class MetadiumConfig {

    // Metadium blockchain HTTP api server domain
    private static final String metadiumHttpProvider = "https://api.metadium.com/prod";

    // Identity registry 컨트렉트 주소
    private static final String identityRegistryContractAddress = "0x42bbff659772231bb63c7c175a1021e080a4cf9d";

    /**
     * BlockChain 통신 시 사용하는 web3 client bean
     * @return Web3 client bean
     */
    @Bean
    public Web3j web3j() {

        return Web3j.build(new HttpService(metadiumHttpProvider));
    }

    /**
     * DID 발급 정보를 조회할 smart contract wrapper bean
     * @return Identity registry contract wrapper bean
     */
    @Bean
    public IdentityRegistry identityRegistry(Web3j web3j) {

        try {
            // Fee 를 소모하는 contract method 를 호출하지 않기 때문에, dummy credential 을 생성한다.
            Credentials dummy = Credentials.create(Keys.createEcKeyPair());
            return IdentityRegistry.load(identityRegistryContractAddress, web3j, dummy, new DefaultGasProvider());
        } catch (InvalidAlgorithmParameterException | NoSuchProviderException | NoSuchAlgorithmException e) {
            throw new RuntimeException("Error while creating IdentityRegistry contract wrapper", e);
        }
    }
}
