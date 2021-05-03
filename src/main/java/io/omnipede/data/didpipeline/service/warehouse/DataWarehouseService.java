package io.omnipede.data.didpipeline.service.warehouse;

import io.omnipede.data.didpipeline.service.blockchain.DidIssuanceInfo;

import java.util.List;
import java.util.Optional;

public interface DataWarehouseService {

    /**
     * Data warehouse 에 DID 발급정보를 batch insert 시킨다
     * @param didIssuanceInfoList DID 발급 정보 리스트
     */
    void save(List<DidIssuanceInfo> didIssuanceInfoList);

    /**
     * Data warehouse 에 저장된 마지막 block number 를 찾아 반환한다
     * @return Data warehouse 상에 저장된 마지막 block number
     */
    Optional<Long> findLastBlockNumber();
}
