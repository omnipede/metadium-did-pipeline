package io.omnipede.data.didpipeline.service.warehouse;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.Date;

@Getter
@AllArgsConstructor
@Builder
@Document(indexName = "did-issuance")
class DidIssuanceDocument {

    // DID 의 ein
    @Id
    @NonNull
    private final String ein;

    // 발급 시각
    @Field(value = "issued_at", type = FieldType.Date, format = DateFormat.date_hour_minute_second_millis)
    @NonNull
    private final Date issuedAt;

    // Warehouse 저장 시각
    @Field(value = "created_at", type = FieldType.Date, format = DateFormat.date_hour_minute_second_millis)
    @NonNull
    private final Date createdAt;

    // DID 발급 이벤트가 발생한 block 의 number
    @NonNull
    @Field(value = "block_number", type = FieldType.Long)
    private final Long blockNumber;
}
