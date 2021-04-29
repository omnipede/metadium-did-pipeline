package io.omnipede.data.didpipeline.service.blockchain;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

/**
 * Metadium blockchain 관련 API 통합 테스트 코드
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class MetadiumServiceImplITTest {

    @Autowired
    private MetadiumServiceImpl metadiumService;

    /**
     * 적절한 수의 DID 발급 데이터를 반환하는지 테스트한다
     */
    @Test
    public void should_return_proper_number_of_data() throws Exception {

        // Given
        long from = 10010000L;

        // When
        List<DidIssuanceInfo> didIssuanceInfoList = metadiumService.getIdentityCreationEventsFrom(from);

        // Then
        assertThat(didIssuanceInfoList).isNotNull();
        assertThat(didIssuanceInfoList).hasSize(6);
    }
}
