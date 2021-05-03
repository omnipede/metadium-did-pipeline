package io.omnipede.data.didpipeline.service.warehouse;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Elastic search 에서 DID 발급정보를 CRUD 할 때 사용할 DAO
 */
interface DidIssuanceElasticsearchRepository extends ElasticsearchRepository<DidIssuanceDocument, String> {


}
