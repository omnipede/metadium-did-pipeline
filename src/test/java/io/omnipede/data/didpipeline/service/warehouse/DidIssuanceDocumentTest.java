package io.omnipede.data.didpipeline.service.warehouse;

import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.Assert.assertThrows;

public class DidIssuanceDocumentTest {

    @Test
    public void document_should_be_created_property() {

        // Given
        String ein = UUID.randomUUID().toString();
        Date issuedAt = new Date();
        Date createdAt = new Date();
        Long blockNumber = 1L;

        // When
        DidIssuanceDocument document = DidIssuanceDocument.builder()
                .ein(ein)
                .issuedAt(issuedAt)
                .createdAt(createdAt)
                .blockNumber(blockNumber)
                .build();

        // Then
        assertThat(document).isNotNull();
        assertThat(document.getEin()).isEqualTo(ein);
        assertThat(document.getIssuedAt()).isEqualTo(issuedAt);
        assertThat(document.getCreatedAt()).isEqualTo(createdAt);
        assertThat(document.getBlockNumber()).isEqualTo(blockNumber);
    }

    @Test
    public void should_throw_NPtrExc_when_builder_encounters_null_fields() {

        // Given
        String ein = UUID.randomUUID().toString();
        Date issuedAt = new Date();
        Date createdAt = new Date();
        Long blockNumber = 1L;

        // When
        Throwable einIsNull = assertThrows(NullPointerException.class, () -> {
            DidIssuanceDocument.builder()
                    .ein(null)
                    .blockNumber(blockNumber)
                    .issuedAt(issuedAt)
                    .createdAt(createdAt)
                    .build();
        });

        Throwable blockNumberIsNull = assertThrows(NullPointerException.class, () -> {
            DidIssuanceDocument.builder()
                    .ein(ein)
                    .blockNumber(null)
                    .issuedAt(issuedAt)
                    .createdAt(createdAt)
                    .build();
        });

        Throwable issuedAtIsNull = assertThrows(NullPointerException.class, () -> {
            DidIssuanceDocument.builder()
                    .ein(ein)
                    .blockNumber(blockNumber)
                    .issuedAt(null)
                    .createdAt(createdAt)
                    .build();
        });

        Throwable createdAtIsNull = assertThrows(NullPointerException.class, () -> {
            DidIssuanceDocument.builder()
                    .ein(ein)
                    .blockNumber(blockNumber)
                    .issuedAt(issuedAt)
                    .createdAt(null)
                    .build();
        });

        // Then
        assertThat(einIsNull).isInstanceOf(NullPointerException.class);
        assertThat(blockNumberIsNull).isInstanceOf(NullPointerException.class);
        assertThat(issuedAtIsNull).isInstanceOf(NullPointerException.class);
        assertThat(createdAtIsNull).isInstanceOf(NullPointerException.class);
    }

    @Test
    public void should_throw_NPtrExc_when_constructor_encounters_null_fields() {

        // Given
        String ein = UUID.randomUUID().toString();
        Date issuedAt = new Date();
        Date createdAt = new Date();
        Long blockNumber = 1L;

        // When
        Throwable einIsNull = assertThrows(NullPointerException.class, () -> {

            new DidIssuanceDocument(null, issuedAt, createdAt, blockNumber);
        });

        Throwable blockNumberIsNull = assertThrows(NullPointerException.class, () -> {

            new DidIssuanceDocument(ein, issuedAt, createdAt, null);
        });

        Throwable issuedAtIsNull = assertThrows(NullPointerException.class, () -> {

            new DidIssuanceDocument(ein, null, createdAt, blockNumber);
        });

        Throwable createdAtIsNull = assertThrows(NullPointerException.class, () -> {

            new DidIssuanceDocument(ein, issuedAt, null, blockNumber);
        });

        // Then
        assertThat(einIsNull).isInstanceOf(NullPointerException.class);
        assertThat(blockNumberIsNull).isInstanceOf(NullPointerException.class);
        assertThat(issuedAtIsNull).isInstanceOf(NullPointerException.class);
        assertThat(createdAtIsNull).isInstanceOf(NullPointerException.class);
    }

    @Test
    public void toString_method_of_builder_should_work() {

        // Given
        DidIssuanceDocument.DidIssuanceDocumentBuilder document = DidIssuanceDocument.builder();

        // When
        String docString = document.toString();

        // Then
        assertThat(docString).isEqualTo("DidIssuanceDocument.DidIssuanceDocumentBuilder(ein=null, issuedAt=null, createdAt=null, blockNumber=null)");
    }
}