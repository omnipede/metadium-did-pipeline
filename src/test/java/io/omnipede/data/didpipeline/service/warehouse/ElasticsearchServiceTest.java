package io.omnipede.data.didpipeline.service.warehouse;

import io.omnipede.data.didpipeline.service.blockchain.DidIssuanceInfo;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.data.elasticsearch.UncategorizedElasticsearchException;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.assertj.core.groups.Tuple.tuple;
import static org.mockito.Mockito.*;

public class ElasticsearchServiceTest {

    private ElasticsearchService elasticsearchService;
    private DidIssuanceElasticsearchRepository elasticsearchRepository;

    @BeforeEach
    public void setup() {

        elasticsearchRepository = mock(DidIssuanceElasticsearchRepository.class);
        elasticsearchService = new ElasticsearchService(elasticsearchRepository);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void should_save_did_issuance_list() {

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
        ArgumentCaptor<List<DidIssuanceDocument>> captor = ArgumentCaptor
                .forClass(List.class);

        verify(elasticsearchRepository, times(1))
                .saveAll(captor.capture());

        List<DidIssuanceDocument> capturedArgument = captor.getValue();
        List<DidIssuanceDocument> expectedArgument = didIssuanceInfoList
                .stream().parallel().map(didIssuanceInfo -> DidIssuanceDocument.builder()
                        .ein(didIssuanceInfo.getEin())
                        .blockNumber(didIssuanceInfo.getBlockNumber())
                        .issuedAt(didIssuanceInfo.getIssuedAt())
                        .createdAt(new Date())
                        .build())
                .collect(Collectors.toList());

        assertThat(capturedArgument)
                .hasSize(100)
                .extracting(
                        DidIssuanceDocument::getEin,
                        DidIssuanceDocument::getBlockNumber,
                        DidIssuanceDocument::getIssuedAt
                )
                .containsExactlyElementsOf(
                        expectedArgument.stream()
                                .map(e -> Tuple.tuple(e.getEin(), e.getBlockNumber(), e.getIssuedAt()))
                                .collect(Collectors.toList())
                );
    }

    @Test
    public void should_find_last_block_of_DW_properly() {

        // Given
        DidIssuanceDocument document = DidIssuanceDocument.builder()
                .createdAt(new Date())
                .issuedAt(new Date())
                .blockNumber(10027L)
                .ein(UUID.randomUUID().toString())
                .build();

        doReturn(Optional.of(document))
                .when(elasticsearchRepository)
                .findByOrderByBlockNumberDesc();

        // When
        Long lastBlock = elasticsearchService.findLastBlockNumber().orElse(null);

        // Then
        assertThat(lastBlock)
                .isNotNull()
                .isEqualTo(document.getBlockNumber());
    }

    @Test
    public void should_return_empty_when_last_block_does_not_exist() {

        // Given
        doReturn(Optional.empty())
                .when(elasticsearchRepository)
                .findByOrderByBlockNumberDesc();

        // When
        Long lastBlock = elasticsearchService.findLastBlockNumber().orElse(null);

        // Then
        assertThat(lastBlock).isNull();
    }

    @Test
    public void should_return_empty_when_UncategorizedElasticsearchException_occur() {

        // Given
        doThrow(UncategorizedElasticsearchException.class)
                .when(elasticsearchRepository)
                .findByOrderByBlockNumberDesc();

        // When
        Long lastBlock = elasticsearchService.findLastBlockNumber().orElse(null);

        // Then
        assertThat(lastBlock).isNull();
    }
}
