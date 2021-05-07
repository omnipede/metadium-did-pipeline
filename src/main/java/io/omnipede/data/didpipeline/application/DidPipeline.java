package io.omnipede.data.didpipeline.application;

import io.omnipede.data.didpipeline.service.blockchain.BlockChainService;
import io.omnipede.data.didpipeline.service.blockchain.DidIssuanceInfo;
import io.omnipede.data.didpipeline.service.warehouse.DataWarehouseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 블록체인으로부터 did 발급 정보를 조회하고, 해당 정보를 DW 에 저장하는 pipeline 을 추상화한 클래스.
 */
@Slf4j
@Service
public class DidPipeline {

    private final BlockChainService blockChainService;

    private final DataWarehouseService dataWarehouseService;

    private static final long DEFAULT_LOOKUP_COUNT = 1000L;

    @Autowired
    public DidPipeline(BlockChainService blockChainService, DataWarehouseService dataWarehouseService) {
        this.blockChainService = blockChainService;
        this.dataWarehouseService = dataWarehouseService;
    }

    public void process() {

        long latestBlock = blockChainService.getLatestBlock();
        long lastBlock = dataWarehouseService.findLastBlockNumber()
                .orElse(-1L);

        long nextBlock = lastBlock + 1;

        // nextBlock 부터 latestBlock 까지 조사하되, DEFAULT_LOOKUP_COUNT 개 씩 나눠서 조사한다
        for (long from = nextBlock; from <= latestBlock; from += DEFAULT_LOOKUP_COUNT) {

            long to = Math.min(from + DEFAULT_LOOKUP_COUNT - 1, latestBlock);

            // from block 부터 to block 까지 조사하며 did 발급 정보를 조회한다
            List<DidIssuanceInfo> didIssuanceInfoList = blockChainService.getIdentityCreationEventsBetween(from, to);

            log.info("# of issued did from block #{} to block #{}: {}", from, to, didIssuanceInfoList.size());

            // DID 발급 정보를 저장소에 저장한다
            dataWarehouseService.save(didIssuanceInfoList);
        }
    }
}
