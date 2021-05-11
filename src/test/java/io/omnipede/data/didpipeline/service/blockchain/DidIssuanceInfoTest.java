package io.omnipede.data.didpipeline.service.blockchain;

import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;

public class DidIssuanceInfoTest {

    @Test
    public void dto_should_be_created_properly_with_builder() {

        // Given
        long blockNumber = 1L;
        String ein = UUID.randomUUID().toString();
        Date issuedAt = new Date();

        // When
        DidIssuanceInfo didIssuanceInfo = DidIssuanceInfo.builder()
                .blockNumber(blockNumber)
                .ein(ein)
                .issuedAt(issuedAt)
                .build();

        // Then
        assertThat(didIssuanceInfo).isNotNull();
        assertThat(didIssuanceInfo.getBlockNumber()).isEqualTo(blockNumber);
        assertThat(didIssuanceInfo.getIssuedAt()).isEqualTo(issuedAt);
        assertThat(didIssuanceInfo.getEin()).isEqualTo(ein);
    }

    @Test
    public void dto_should_be_created_property_with_constructor() {

        // Given
        long blockNumber = 1L;
        String ein = UUID.randomUUID().toString();
        Date issuedAt = new Date();

        // When
        DidIssuanceInfo didIssuanceInfo = new DidIssuanceInfo(ein, issuedAt, blockNumber);

        // Then
        assertThat(didIssuanceInfo).isNotNull();
        assertThat(didIssuanceInfo.getBlockNumber()).isEqualTo(blockNumber);
        assertThat(didIssuanceInfo.getIssuedAt()).isEqualTo(issuedAt);
        assertThat(didIssuanceInfo.getEin()).isEqualTo(ein);
    }

    @Test
    public void should_throw_exception_when_ein_is_null() {

        // Given
        long blockNumber = 1L;
        Date issuedAt = new Date();

        // When
        Throwable throwable = catchThrowable(() -> {
            DidIssuanceInfo.builder()
                    .ein(null)
                    .blockNumber(blockNumber)
                    .issuedAt(issuedAt)
                    .build();
        });

        Throwable another = catchThrowable(() -> {
            new DidIssuanceInfo(null, issuedAt, blockNumber);
        });

        // Then
        assertThat(throwable).isNotNull();
        assertThat(throwable).isInstanceOf(NullPointerException.class);
        assertThat(another).isNotNull();
        assertThat(another).isInstanceOf(NullPointerException.class);
    }

    @Test
    public void should_throw_exception_when_blockNumber_is_null() {

        // Given
        String ein = UUID.randomUUID().toString();
        Date issuedAt = new Date();

        // When
        Throwable throwable = catchThrowable(() -> {
            DidIssuanceInfo.builder()
                    .ein(ein)
                    .blockNumber(null)
                    .issuedAt(issuedAt)
                    .build();
        });

        Throwable another = catchThrowable(() -> {
            new DidIssuanceInfo(ein, issuedAt, null);
        });

        // Then
        assertThat(throwable).isNotNull();
        assertThat(throwable).isInstanceOf(NullPointerException.class);
        assertThat(another).isNotNull();
        assertThat(another).isInstanceOf(NullPointerException.class);
    }

    @Test
    public void should_throw_exception_when_issuedAt_is_null() {

        // Given
        String ein = UUID.randomUUID().toString();
        long blockNumber = 123L;

        // When
        Throwable throwable = catchThrowable(() -> {
            DidIssuanceInfo.builder()
                    .ein(ein)
                    .blockNumber(blockNumber)
                    .issuedAt(null)
                    .build();
        });

        Throwable another = catchThrowable(() -> {
            new DidIssuanceInfo(ein, null, blockNumber);
        });

        // Then
        assertThat(throwable).isNotNull();
        assertThat(throwable).isInstanceOf(NullPointerException.class);
        assertThat(another).isNotNull();
        assertThat(another).isInstanceOf(NullPointerException.class);
    }

    @Test
    public void toString_method_of_builder() {

        // Given
        DidIssuanceInfo.DidIssuanceInfoBuilder didIssuanceInfo = DidIssuanceInfo.builder();

        // When
        String t = didIssuanceInfo.toString();

        // Then
        assertThat(t).isEqualTo("DidIssuanceInfo.DidIssuanceInfoBuilder(ein=null, issuedAt=null, blockNumber=null)");
    }
}