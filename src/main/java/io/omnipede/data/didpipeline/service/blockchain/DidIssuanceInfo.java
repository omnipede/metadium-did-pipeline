package io.omnipede.data.didpipeline.service.blockchain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

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
    private String ein;

    // 발급 시각 (추정값)
    private Date issuedAt;

    //
    private long from;

    //
    private long to;
}
