package io.omnipede.data.didpipeline.service.warehouse;

import io.omnipede.data.didpipeline.service.blockchain.DidIssuanceInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.UncategorizedElasticsearchException;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * DID 발급 정보를 elasticsearch 상에서 CRUD 할 때 사용하는 data warehouse 인터페이스 구현체
 */
@Slf4j
@Service
class ElasticsearchService implements DataWarehouseService {

    private final DidIssuanceElasticsearchRepository didIssuanceElasticsearchRepository;

    @Autowired
    public ElasticsearchService(DidIssuanceElasticsearchRepository didIssuanceElasticsearchRepository) {
        this.didIssuanceElasticsearchRepository = didIssuanceElasticsearchRepository;
    }

    /**
     * DID 발급 정보 리스트를 elasticsearch 에 batch insert 하는 메소드
     * @param didIssuanceInfoList DID 발급 정보 리스트
     */
    public void save(List<DidIssuanceInfo> didIssuanceInfoList) {

        // DTO 리스트를 document 리스트로 변환
        List<DidIssuanceDocument> documents = didIssuanceInfoList
                .stream().parallel().map(didIssuanceInfo -> DidIssuanceDocument.builder()
                    .ein(didIssuanceInfo.getEin())
                    .blockNumber(didIssuanceInfo.getTo())
                    .issuedAt(didIssuanceInfo.getIssuedAt())
                    .createdAt(new Date())
                    .build())
                .collect(Collectors.toList());

        // document 리스트 저장
        didIssuanceElasticsearchRepository.saveAll(documents);
    }

    /**
     * Elasticsearch 상에 저장된 마지막 block number 를 찾아 반환한다.
     * @return Elasticsearch 상에 저장된 마지막 block number.
     */
    public Optional<Long> findLastBlockNumber() {

        try {
            // 가장 큰 block number 를 찾아 반환한다
            DidIssuanceDocument document = didIssuanceElasticsearchRepository.findByOrderByBlockNumberDesc()
                    .orElse(null);

            // If not found, then return empty
            if (document == null)
                return Optional.empty();

            Long blockNumber = document.getBlockNumber();
            return Optional.of(blockNumber);
        } catch (UncategorizedElasticsearchException e) {
            return Optional.empty();
        }
    }
}
