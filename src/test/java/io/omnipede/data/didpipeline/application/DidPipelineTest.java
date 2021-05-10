package io.omnipede.data.didpipeline.application;

import io.omnipede.data.didpipeline.service.blockchain.BlockChainService;
import io.omnipede.data.didpipeline.service.warehouse.DataWarehouseService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.Optional;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class DidPipelineTest {

    private DidPipeline didPipeline;

    private BlockChainService blockChainService;
    private DataWarehouseService dataWarehouseService;

    private static final long DEFAULT_LOOKUP_COUNT = 1000L;

    @BeforeEach
    public void setup() {

        blockChainService = mock(BlockChainService.class);
        dataWarehouseService = mock(DataWarehouseService.class);

        doReturn(Collections.emptyList()).when(blockChainService)
                .getIdentityCreationEventsBetween(anyLong(), anyLong());

        doNothing().when(dataWarehouseService)
                .save(any());

        didPipeline = new DidPipeline(blockChainService, dataWarehouseService);
    }

    @Test
    public void when_last_block_is_not_saved_in_DW() {

        // Given
        long latestBlock = 103188L;
        long nextBlock = 0L;
        when(blockChainService.getLatestBlock()).thenReturn(latestBlock);
        // Assume that nothing saved in DW
        when(dataWarehouseService.findLastBlockNumber()).thenReturn(Optional.empty());

        // When
        didPipeline.process();

        // Then
        verify(blockChainService, times(1))
                .getIdentityCreationEventsBetween(nextBlock, nextBlock + DEFAULT_LOOKUP_COUNT - 1);

        int expectedIterationCount = (int) ((latestBlock - nextBlock) / DEFAULT_LOOKUP_COUNT) + 1;
        verify(dataWarehouseService, times(expectedIterationCount)).save(any());
    }

    @Test
    public void when_last_block_is_saved_in_DW() {

        // Given
        long latestBlock = 100000L;
        long lastBlock = 100L;
        long nextBlock = lastBlock + 1;

        when(dataWarehouseService.findLastBlockNumber()).thenReturn(Optional.of(lastBlock));
        when(blockChainService.getLatestBlock()).thenReturn(latestBlock);

        // When
        didPipeline.process();

        // Then
        verify(blockChainService, times(1))
                .getIdentityCreationEventsBetween(nextBlock, nextBlock + DEFAULT_LOOKUP_COUNT - 1);

        int expectedIterationCount = (int) ((latestBlock - nextBlock) / DEFAULT_LOOKUP_COUNT) + 1;
        verify(dataWarehouseService, times(expectedIterationCount)).save(any());
    }
}