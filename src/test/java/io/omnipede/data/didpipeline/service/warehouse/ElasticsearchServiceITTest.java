package io.omnipede.data.didpipeline.service.warehouse;

import io.omnipede.data.didpipeline.service.blockchain.DidIssuanceInfo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

/**
 * Elasticsearch CRUD 통합 테스트
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class ElasticsearchServiceITTest {

    @Autowired
    private DidIssuanceElasticsearchRepository didIssuanceElasticsearchRepository;

    @Autowired
    private ElasticsearchService elasticsearchService;

    @Test
    public void did_issuance_info_should_be_saved() throws Exception {

        // Given
        DidIssuanceInfo didIssuanceInfo = DidIssuanceInfo.builder()
                .ein(UUID.randomUUID().toString())
                .issuedAt(new Date())
                .from(0)
                .to(0)
                .build();

        // When
        elasticsearchService.save(didIssuanceInfo);

        // Then
        DidIssuanceDocument document = didIssuanceElasticsearchRepository.findById(didIssuanceInfo.getEin())
                .orElse(null);

        assertThat(document).isNotNull();
        assertThat(document.getEin()).isEqualTo(didIssuanceInfo.getEin());
        assertThat(document.getIssuedAt()).isEqualTo(didIssuanceInfo.getIssuedAt());

        // Clear test data
        didIssuanceElasticsearchRepository.delete(document);
    }
}
