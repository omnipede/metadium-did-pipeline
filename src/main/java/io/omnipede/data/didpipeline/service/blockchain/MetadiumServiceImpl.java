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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Metadium blockchain 관련 API
 */
@Slf4j
@Service
class MetadiumServiceImpl implements BlockChainService {

    private final Web3j web3j;

    // Identity registry 컨트렉트 주소
    private static final String identityRegistryContractAddress = "0x42bbff659772231bb63c7c175a1021e080a4cf9d";

    private final IdentityRegistry identityRegistry;

    @Autowired
    public MetadiumServiceImpl(Web3j web3j) {
        this.web3j = web3j;
        this.identityRegistry = givenIdentityRegistry();
    }

    /**
     * IdentityRegistry contract 의 java wrapper 객체를 생성하는 메소드
     * @return IdentityRegistry wrapper object
     */
    private IdentityRegistry givenIdentityRegistry() {
        try {
            // Fee 를 소모하는 contract method 를 호출하지 않기 때문에, dummy credential 을 생성한다.
            Credentials dummy = Credentials.create(Keys.createEcKeyPair());
            return IdentityRegistry.load(identityRegistryContractAddress, web3j, dummy, new DefaultGasProvider());
        } catch (InvalidAlgorithmParameterException | NoSuchProviderException | NoSuchAlgorithmException e) {
            throw new RuntimeException("Error while creating IdentityRegistry contract", e);
        }
    }

    /**
     * 특정 metadium block 에서 identity creation event 를 읽어오는 메소드
     * from 에서 부터 1000 개를 읽는
     * @param from Event 를 읽어올 block 의 number
     */
    @Override
    public List<DidIssuanceInfo> getIdentityCreationEventsFrom(long from) {
        long count = 1000;
        return getIdentityCreationEventsFrom(from, count);
    }

    /**
     * 특정 metadium block 에서 identity creation event 를 읽어오는 메소드
     * @param from Event 를 읽어올 block 의 number
     * @param count startBlockNumber 로 부터 조회할 block 개수
     */
    @Override
    public List<DidIssuanceInfo> getIdentityCreationEventsFrom(long from, long count) {

        long to = from + count - 1;

        List<DidIssuanceInfo> didIssuanceInfoList = getIdentityCreationEventsBetween(from, to);

        log.info("# of issued DID between block #{} and block #{}: {}", from, to, didIssuanceInfoList.size());

        return didIssuanceInfoList;
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
            throw new IllegalStateException("Error while getting last block from metadium", e);
        }
    }

    /**
     * Block 범위 내의 did 발급 event 를 읽어서 did 발급 정보 리스트를 반환하는 메소드
     * @param from Block 범위 시작
     * @param to Block 범위 끝
     * @return DID 발급 정보 리스트
     */
    private List<DidIssuanceInfo> getIdentityCreationEventsBetween(long from, long to) {

        List<IdentityRegistry.IdentityCreatedEventResponse> identityCreatedEventResponseList = new ArrayList<>();

        // Read all identity created events from ~ to.
        identityRegistry.identityCreatedEventFlowable(
                DefaultBlockParameter.valueOf(BigInteger.valueOf(from)),
                DefaultBlockParameter.valueOf(BigInteger.valueOf(to))
        ).subscribe(identityCreatedEventResponse -> {
            // Pass if empty
            if (identityCreatedEventResponse == null)
                return;

            identityCreatedEventResponseList.add(identityCreatedEventResponse);
        }).dispose();

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
        Date issuedAt = getTimestampOfBlock(blockNumberLong)
                .orElseThrow(() -> new IllegalStateException("# "+ blockNumberLong + " block does not exists"));

        DidIssuanceInfo didIssuanceInfo = DidIssuanceInfo.builder()
                .ein(response.ein.toString())
                .issuedAt(issuedAt)
                .from(blockNumberLong)
                .to(blockNumberLong)
                .build();

        return Optional.of(didIssuanceInfo);
    }

    /**
     * 특정 metadium block 의 생성 시점을 반환하는 메소드. 만약 block 이 존재하지 않으면 empty 를 반환한다.
     * @param blockNumber Metadium block number
     * @return Block 생성 시각 (timestamp millis)
     */
    private Optional<Date> getTimestampOfBlock(long blockNumber) {

        EthBlock ethBlock = getBlock(blockNumber).orElse(null);
        if (ethBlock == null || ethBlock.getBlock() == null)
            return Optional.empty();

        // Sec 에서 milli 단위로 변환하기 위해 1000 을 곱한다.
        Date createdAt = new Date(ethBlock.getBlock().getTimestamp().longValue() * 1000);
        return Optional.of(createdAt);
    }

    /**
     * Block number 로 block 데이터를 가져오는 메소드
     * @param blockNumber Block number
     * @return Metadium block
     */
    private Optional<EthBlock> getBlock(long blockNumber) {

        try {
            EthBlock ethBlock
                    = web3j.ethGetBlockByNumber(DefaultBlockParameter.valueOf(BigInteger.valueOf(blockNumber)), false).send();
            if (ethBlock == null)
                return Optional.empty();
            return Optional.of(ethBlock);
        } catch (IOException e) {
            return Optional.empty();
        }
    }
}
