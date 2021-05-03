package io.omnipede.data.didpipeline.service.warehouse;

import io.omnipede.data.didpipeline.service.blockchain.DidIssuanceInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * DID 발급 정보를 elasticsearch 상에서 CRUD 할 때 사용하는 data warehouse 인터페이스 구현체
 */
@Service
class ElasticsearchService implements DataWarehouseService {

    private final DidIssuanceElasticsearchRepository didIssuanceElasticsearchRepository;

    @Autowired
    public ElasticsearchService(DidIssuanceElasticsearchRepository didIssuanceElasticsearchRepository) {
        this.didIssuanceElasticsearchRepository = didIssuanceElasticsearchRepository;
    }

    /**
     * Elastic search 에 DID 발급 정보를 저장한다.
     * @param didIssuanceInfo DID 발급 정보
     */
    public void save(DidIssuanceInfo didIssuanceInfo) {

        DidIssuanceDocument document = DidIssuanceDocument.builder()
                .ein(didIssuanceInfo.getEin())
                .issuedAt(didIssuanceInfo.getIssuedAt())
                .createdAt(new Date())
                .build();

        didIssuanceElasticsearchRepository.save(document);
    }
}
