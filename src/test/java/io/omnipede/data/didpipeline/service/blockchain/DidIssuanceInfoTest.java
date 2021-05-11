package io.omnipede.data.didpipeline.service.blockchain;

import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;
import static org.junit.Assert.assertThrows;

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
    public void should_throw_NPtrExc_when_builder_encounters_null_fields() {

        // Given
        long blockNumber = 1L;
        String ein = UUID.randomUUID().toString();
        Date issuedAt = new Date();

        // When
        Throwable blockNumberIsNull = assertThrows(NullPointerException.class, () -> {

            DidIssuanceInfo.builder()
                    .ein(ein)
                    .blockNumber(null)
                    .issuedAt(issuedAt)
                    .build();
        });

        Throwable einIsNull = assertThrows(NullPointerException.class, () -> {

            DidIssuanceInfo.builder()
                    .ein(null)
                    .blockNumber(blockNumber)
                    .issuedAt(issuedAt)
                    .build();
        });

        Throwable issuedAtIsNull = assertThrows(NullPointerException.class, () -> {

            DidIssuanceInfo.builder()
                    .ein(ein)
                    .blockNumber(blockNumber)
                    .issuedAt(null)
                    .build();
        });

        // Then
        assertThat(blockNumberIsNull).isInstanceOf(NullPointerException.class);
        assertThat(einIsNull).isInstanceOf(NullPointerException.class);
        assertThat(issuedAtIsNull).isInstanceOf(NullPointerException.class);
    }

    @Test
    public void should_throw_NPtrExc_when_constructor_encounters_null_fields() {

        // Given
        long blockNumber = 1L;
        String ein = UUID.randomUUID().toString();
        Date issuedAt = new Date();

        // When
        Throwable blockNumberIsNull = assertThrows(NullPointerException.class, () -> {

            new DidIssuanceInfo(ein, issuedAt, null);
        });

        Throwable einIsNull = assertThrows(NullPointerException.class, () -> {

            new DidIssuanceInfo(null, issuedAt, blockNumber);
        });

        Throwable issuedAtIsNull = assertThrows(NullPointerException.class, () -> {

            new DidIssuanceInfo(ein, null, blockNumber);
        });

        // Then
        assertThat(blockNumberIsNull).isInstanceOf(NullPointerException.class);
        assertThat(einIsNull).isInstanceOf(NullPointerException.class);
        assertThat(issuedAtIsNull).isInstanceOf(NullPointerException.class);
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