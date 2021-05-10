package io.omnipede.data.didpipeline.service.blockchain;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.web3j.protocol.Web3j;

import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

/**
 * Metadium blockchain 관련 API 통합 테스트 코드
 */
public class MetadiumServiceITTest {

    private static MetadiumService metadiumService;

    @BeforeAll
    public static void setup() {

        MetadiumConfig metadiumConfig = new MetadiumConfig();
        Web3j web3j = metadiumConfig.web3j();
        IdentityRegistry identityRegistry = metadiumConfig.identityRegistry(web3j);

        metadiumService = new MetadiumService(web3j, identityRegistry);
    }

    /**
     * 적절한 수의 DID 발급 데이터를 반환하는지 테스트한다
     */
    @Test
    public void should_return_proper_number_of_data() throws Exception {

        // Given
        long from = 10010000L;
        long to = from + 1000 - 1;

        // When
        List<DidIssuanceInfo> didIssuanceInfoList = metadiumService.getIdentityCreationEventsBetween(from, to);

        // Then
        assertThat(didIssuanceInfoList).isNotNull();
        assertThat(didIssuanceInfoList).hasSize(6);
    }

    /**
     * Metadium block chain 의 latest block 을 반환해야 한다.
     */
    @Test
    public void should_return_valid_last_block_number() throws Exception {

        // Given
        long previousLastBlock = 18225909L;

        // When
        long lastBlockNumber = metadiumService.getLatestBlock();

        // Then
        assertThat(lastBlockNumber).isGreaterThan(previousLastBlock);
    }
}