package io.omnipede.data.didpipeline.service.warehouse;

import io.omnipede.data.didpipeline.service.DockerIntegration;
import io.omnipede.data.didpipeline.service.blockchain.DidIssuanceInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

/**
 * Elasticsearch CRUD 통합 테스트.
 * Context loading 을 최소화하기 위해 필요한 클래스만 테스트 환경에 올린다.
 */
@ExtendWith(SpringExtension.class)
@EnableAutoConfiguration
@ContextConfiguration(
    classes = {
        ElasticsearchService.class, DidIssuanceElasticsearchRepository.class
    }
)
public class ElasticsearchServiceITTest extends DockerIntegration {

    @Autowired
    private DidIssuanceElasticsearchRepository didIssuanceElasticsearchRepository;

    @Autowired
    private ElasticsearchService elasticsearchService;

    @BeforeEach
    public void setup() {
        // 테스트 시작 전 DW 내부 데이터를 삭제한다
        didIssuanceElasticsearchRepository.deleteAll();
    }

    @Test
    public void should_find_last_block_number() throws Exception {

        // Given
        DidIssuanceDocument document = DidIssuanceDocument.builder()
                .ein(UUID.randomUUID().toString())
                .createdAt(new Date())
                .blockNumber(1024L)
                .issuedAt(new Date())
                .build();

        didIssuanceElasticsearchRepository.save(document);

        // When
        Long lastBlockNumber = elasticsearchService.findLastBlockNumber()
                .orElse(null);

        // Then
        assertThat(lastBlockNumber).isNotNull();
        assertThat(lastBlockNumber).isEqualTo(document.getBlockNumber());
    }

    @Test
    public void should_save_did_issuance_list() throws Exception {

        // Given
        List<DidIssuanceInfo> didIssuanceInfoList = IntStream.range(0, 100).parallel()
            .mapToObj(num -> DidIssuanceInfo.builder()
                .ein(String.valueOf(num))
                .issuedAt(new Date())
                .blockNumber((long) num)
                .build()
            ).collect(Collectors.toList());

        // When
        elasticsearchService.save(didIssuanceInfoList);

        // Then
        List<DidIssuanceInfo> filtered = didIssuanceInfoList.stream().parallel()
                .filter(this::isSaved)
                .collect(Collectors.toList());

        assertThat(filtered.size()).isEqualTo(didIssuanceInfoList.size());
    }

    /**
     * DID 발급 정보가 elasticsearch 에 정확하게 저장되었는지 확인하는 메소드
     * @param didIssuanceInfo 저장되었는지 확인할 DTO
     * @return 저장 여부
     */
    private boolean isSaved(DidIssuanceInfo didIssuanceInfo) {
        DidIssuanceDocument document
                = didIssuanceElasticsearchRepository.findById(didIssuanceInfo.getEin()).orElse(null);

        // Document 저장 되었는지 확인
        if (document == null)
            return false;

        // EIN 확인
        if (!didIssuanceInfo.getEin().equals(document.getEin()))
            return false;

        // DID 발급 시각 확인
        if (!didIssuanceInfo.getIssuedAt().equals(document.getIssuedAt()))
            return false;

        // Block number 확인
        return didIssuanceInfo.getBlockNumber().equals(document.getBlockNumber());
    }
}
