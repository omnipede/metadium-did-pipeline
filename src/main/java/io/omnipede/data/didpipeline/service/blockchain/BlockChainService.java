package io.omnipede.data.didpipeline.service.blockchain;

import java.util.List;

public interface BlockChainService {

    List<DidIssuanceInfo> getIdentityCreationEventsFrom(long from);
    List<DidIssuanceInfo> getIdentityCreationEventsFrom(long from, long count);
    Long getLatestBlock();
}
