package io.omnipede.data.didpipeline.application;

import io.omnipede.data.didpipeline.service.blockchain.BlockChainService;
import io.omnipede.data.didpipeline.service.blockchain.DidIssuanceInfo;
import io.omnipede.data.didpipeline.service.warehouse.DataWarehouseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Block chain 상에서 DID 발급 정보를 읽어와서 warehouse 에 적재하는 pipeline 을 추상화한 클래스.
 */
@Slf4j
@Service
public class DidPipeline {

    // DID 발급 정보를 가져올 때 사용하는 service 객체
    private final BlockChainService blockChainService;

    // Data warehouse CRUD service object
    private final DataWarehouseService dataWarehouseService;

    @Autowired
    public DidPipeline(BlockChainService blockChainService, DataWarehouseService dataWarehouseService) {
        this.blockChainService = blockChainService;
        this.dataWarehouseService = dataWarehouseService;
    }

    /**
     * 스케쥴링을 시작하기 전, data warehouse 에 저장된 마지막 block number 와
     * block chain 상의 latest block 사이에 생성된 did 발급 정보를 data source 에 새로 저장하는
     * 동기화 메소드.
     */
    public void sync() {

        // Block chain 에 생성된 가장 최신에 생성된 block
        long latestBlock = blockChainService.getLatestBlock();

        // Warehouse 에 저장된 가장 마지막 block
        long lastBlock = dataWarehouseService.findLastBlockNumber()
                .orElse(0L);

        log.info("Synchronizing from block #{} to block #{} starts", lastBlock, latestBlock);

        // Latest block 과 last block 사이에 생성된 did 발급 정보를 조회 후 저장한다
        fetchAndSaveDidIssuanceInfo(lastBlock, latestBlock);

        log.info("Synchronizing from block #{} to block #{} ends", lastBlock, latestBlock);
    }

    /**
     * Block 범위 내의 did 발급 정보를 조회한 뒤 warehouse 에 저장하는 메소드
     * @param fromBlock 시작 block number
     * @param toBlock 끝 block number
     */
    private void fetchAndSaveDidIssuanceInfo(long fromBlock, long toBlock) {

        // 한번에 1000 개의 block 을 조사한다
        long lookupCount = 1000L;
        for (long i = fromBlock; i < toBlock; i += lookupCount) {

            // i 번째 블록을 기준으로 lookupCount 만큼의 블록을 조사하며 did 발급 이벤트가 발생했는지 확인한다.
            List<DidIssuanceInfo> didIssuanceInfoList = blockChainService.getIdentityCreationEventsFrom(i, lookupCount);

            // DID 발급 정보를 warehouse 에 저장한다
            dataWarehouseService.save(didIssuanceInfoList);
        }
    }
}
