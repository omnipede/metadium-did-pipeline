package io.omnipede.data.didpipeline.service.blockchain;

import lombok.*;

import java.util.Date;

/**
 * DID 발급 정보 DTO
 */
@Getter
@Setter
@AllArgsConstructor
@Builder
public class DidIssuanceInfo {

    // 발급된 EIN
    @NonNull
    private String ein;

    // 발급 시각 (추정값)
    @NonNull
    private Date issuedAt;

    // DID 발급 정보가 저장된 block 의 block number
    @NonNull
    private Long blockNumber;
}
