package io.omnipede.data.didpipeline.service.blockchain;

import java.util.List;

public interface BlockChainService {

    List<DidIssuanceInfo> getIdentityCreationEventsBetween(long from, long to);
    Long getLatestBlock();
}
