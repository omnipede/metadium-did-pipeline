package io.omnipede.data.didpipeline.service.blockchain;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.Keys;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.EthBlock;
import org.web3j.tx.gas.DefaultGasProvider;

import java.io.IOException;
import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Metadium blockchain 관련 API
 */
@Slf4j
@Service
class MetadiumService implements BlockChainService {

    private final Web3j web3j;

    private final IdentityRegistry identityRegistry;

    @Autowired
    public MetadiumService(Web3j web3j, IdentityRegistry identityRegistry) {
        this.web3j = web3j;
        this.identityRegistry = identityRegistry;
    }

    /**
     * Get latest metadium block
     * @return Last block of metadium block chain
     */
    @Override
    public Long getLatestBlock() {
        try {
            EthBlock ethBlock = web3j.ethGetBlockByNumber(DefaultBlockParameterName.LATEST, false).send();
            return ethBlock.getBlock().getNumber().longValue();
        } catch (IOException e) {
            throw new IllegalStateException("Error while getting latest block from metadium", e);
        }
    }

    /**
     * Block 범위 내의 did 발급 event 를 읽어서 did 발급 정보 리스트를 반환하는 메소드
     * @param from Block 범위 시작
     * @param to Block 범위 끝
     * @return DID 발급 정보 리스트
     */
    @Override
    public List<DidIssuanceInfo> getIdentityCreationEventsBetween(long from, long to) {

        if (from > to)
            throw new IllegalStateException("Parameter 'from' should be larger than parameter 'to'. 'from': " + from + " 'to': " + to);

        List<IdentityRegistry.IdentityCreatedEventResponse> identityCreatedEventResponseList = new ArrayList<>();

        // Read all identity created events from ~ to.
        identityRegistry.identityCreatedEventFlowable(
                DefaultBlockParameter.valueOf(BigInteger.valueOf(from)),
                DefaultBlockParameter.valueOf(BigInteger.valueOf(to))
        ).retry(32).subscribe(identityCreatedEventResponseList::add).dispose();

        // Convert identity created events to did issuance information
        return identityCreatedEventResponseList
                .stream()
                .parallel()
                .map(this::toDidIssuanceInfo)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    /**
     * Block chain event response 객체를 DID 발급 정보 DTO 로 변환하는 메소드
     * @param response Block chain event response 객체
     * @return DID 발급 정보 DTO
     */
    private Optional<DidIssuanceInfo> toDidIssuanceInfo(IdentityRegistry.IdentityCreatedEventResponse response) {

        if (response == null || response.log == null || response.ein == null)
            return Optional.empty();

        BigInteger blockNumber = response.log.getBlockNumber();
        if (blockNumber == null)
            return Optional.empty();

        long blockNumberLong = blockNumber.longValue();
        Date issuedAt = getTimestampOfBlock(blockNumberLong);

        DidIssuanceInfo didIssuanceInfo = DidIssuanceInfo.builder()
                .ein(response.ein.toString())
                .issuedAt(issuedAt)
                .blockNumber(blockNumberLong)
                .build();

        return Optional.of(didIssuanceInfo);
    }

    /**
     * 특정 metadium block 의 생성 시점을 반환하는 메소드. 만약 block 이 존재하지 않으면 empty 를 반환한다.
     * @param blockNumber Metadium block number
     * @return Block 생성 시각 (timestamp millis)
     */
    private Date getTimestampOfBlock(long blockNumber) {

        try {
            EthBlock ethBlock = web3j.ethGetBlockByNumber(
                    DefaultBlockParameter.valueOf(BigInteger.valueOf(blockNumber)), false
            ).send();

            // Sec 에서 milli 단위로 변환하기 위해 1000 을 곱한다.
            return  new Date(ethBlock.getBlock().getTimestamp().longValue() * 1000);
        } catch (IOException e) {
            throw new IllegalStateException("Exception while getting block number #" + blockNumber, e);
        }
    }
}
