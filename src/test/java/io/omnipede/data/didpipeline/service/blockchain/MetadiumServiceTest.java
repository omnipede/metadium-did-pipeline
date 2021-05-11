package io.omnipede.data.didpipeline.service.blockchain;

import io.reactivex.Flowable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.Request;
import org.web3j.protocol.core.methods.response.EthBlock;
import org.web3j.protocol.core.methods.response.Log;

import java.io.IOException;
import java.math.BigInteger;
import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

/**
 * MetadiumService 클래스 단위 테스트
 */
public class MetadiumServiceTest {

    private MetadiumService metadiumService;
    private Web3j web3j;
    private IdentityRegistry identityRegistry;

    @BeforeEach
    public void setup() {

        web3j = mock(Web3j.class);
        identityRegistry = mock(IdentityRegistry.class);
        metadiumService = new MetadiumService(web3j, identityRegistry);
    }

    @Test
    public void should_return_proper_block_number() throws Exception {

        // Given
        long blockNumber = 1234569911L;

        EthBlock.Block block = new EthBlock.Block();
        block.setNumber(String.valueOf(blockNumber));

        EthBlock ethBlock = new EthBlock();
        ethBlock.setResult(block);

        Request<?, ?> mockedRequest = mock(Request.class);
        doReturn(ethBlock)
                .when(mockedRequest)
                .send();

        doReturn(mockedRequest)
                .when(web3j)
                .ethGetBlockByNumber(DefaultBlockParameterName.LATEST, false);

        // When
        long latestBlock = metadiumService.getLatestBlock();

        // Then
        assertThat(latestBlock).isEqualTo(blockNumber);
    }

    @Test
    public void should_throw_IllegalStateException_when_network_error() throws Exception {

        // Given
        Request<?, ?> mockedRequest = mock(Request.class);
        doThrow(new IOException())
                .when(mockedRequest)
                .send();

        doReturn(mockedRequest)
                .when(web3j)
                .ethGetBlockByNumber(DefaultBlockParameterName.LATEST, false);

        // When
        Throwable throwable = assertThrows(IllegalStateException.class, () -> {
            metadiumService.getLatestBlock();
        });

        // Then
        assertThat(throwable).isNotNull();
        assertThat(throwable).isInstanceOf(IllegalStateException.class);
        assertThat(throwable).hasCause(new IOException());
    }

    @Test
    public void should_return_did_issuance_info_list() throws Exception {

        // Given
        long from = 10L;
        long to = 1000L;

        // Mock identityRegistry.identityCreatedEventFlowable
        IdentityRegistry.IdentityCreatedEventResponse event1 = givenIdentityCreatedEvent(1L, 101L);
        IdentityRegistry.IdentityCreatedEventResponse event2 = givenIdentityCreatedEvent(2L, 102L);
        IdentityRegistry.IdentityCreatedEventResponse event3 = givenIdentityCreatedEvent(3L, 103L);
        IdentityRegistry.IdentityCreatedEventResponse event4 = givenIdentityCreatedEvent(null, 103L);
        IdentityRegistry.IdentityCreatedEventResponse event5 = givenIdentityCreatedEvent(5L, null);
        IdentityRegistry.IdentityCreatedEventResponse event6 = new IdentityRegistry.IdentityCreatedEventResponse();

        Flowable<IdentityRegistry.IdentityCreatedEventResponse> mockedFlowable = Flowable.just(
                event1, event2, event3, event4, event5, event6
        );

        doReturn(mockedFlowable)
                .when(identityRegistry)
                .identityCreatedEventFlowable(
                        refEq(DefaultBlockParameter.valueOf(BigInteger.valueOf(from))),
                        refEq(DefaultBlockParameter.valueOf(BigInteger.valueOf(to))));

        // Mock web3j.ethGetBlockByNumber
        EthBlock.Block block = new EthBlock.Block();
        block.setTimestamp("0x1234");

        EthBlock ethBlock = new EthBlock();
        ethBlock.setResult(block);

        Request<?, ?> mockedRequest = mock(Request.class);
        doReturn(ethBlock)
                .when(mockedRequest)
                .send();
        doReturn(mockedRequest)
                .when(web3j)
                .ethGetBlockByNumber(any(), eq(false));

        // When
        List<DidIssuanceInfo> didIssuanceInfoList = metadiumService.getIdentityCreationEventsBetween(from, to);

        // Then
        assertThat(didIssuanceInfoList.size()).isEqualTo(3);
        assertThat(didIssuanceInfoList).extracting(DidIssuanceInfo::getEin)
                .containsExactly("1", "2", "3");
        assertThat(didIssuanceInfoList).extracting(DidIssuanceInfo::getBlockNumber)
                .containsExactly(101L, 102L, 103L);
    }

    @Test
    public void should_throw_IllegalStateException_if_network_error_occur_while_ethGetBlockByNumber() throws Exception {

        // Given
        long from = 100L;
        long to = 1000L;

        // Mock identityRegistry.identityCreatedEventFlowable
        IdentityRegistry.IdentityCreatedEventResponse event = givenIdentityCreatedEvent(1L, 101L);

        Flowable<IdentityRegistry.IdentityCreatedEventResponse> mockedFlowable = Flowable.just(event);

        doReturn(mockedFlowable)
                .when(identityRegistry)
                .identityCreatedEventFlowable(
                        refEq(DefaultBlockParameter.valueOf(BigInteger.valueOf(from))),
                        refEq(DefaultBlockParameter.valueOf(BigInteger.valueOf(to))));

        // Mock web3j.ethGetBlockByNumber
        Request<?, ?> mockRequest = mock(Request.class);
        doThrow(IOException.class)
                .when(mockRequest)
                .send();

        long expectedBlockNumber = event.log.getBlockNumber().longValue();
        doReturn(mockRequest)
                .when(web3j)
                .ethGetBlockByNumber(
                        refEq(DefaultBlockParameter.valueOf(BigInteger.valueOf(expectedBlockNumber))), eq(false));

        // When
        Throwable throwable = assertThrows(IllegalStateException.class, () -> {
                metadiumService.getIdentityCreationEventsBetween(from, to);
        });

        // Then
        assertThat(throwable).isNotNull();
        assertThat(throwable).hasMessageContaining("Exception while getting block number #" + expectedBlockNumber);
    }

    /**
     * 테스트용 identity creation event 객체를 생성하는 메소드
     */
    private IdentityRegistry.IdentityCreatedEventResponse givenIdentityCreatedEvent(Long ein, Long blockNumber) {
        IdentityRegistry.IdentityCreatedEventResponse event = new IdentityRegistry.IdentityCreatedEventResponse();

        if (ein != null)
            event.ein =  BigInteger.valueOf(ein);

        event.log = new Log();
        if (blockNumber != null) {
            event.log.setBlockNumber(String.valueOf(blockNumber));
        }

        return event;
    }

    @Test
    public void should_throw_IllegalArgumentException_when_from_is_larger_than_to() {

        // Given
        long from = 2;
        long to = 1;

        // When
        Throwable throwable = assertThrows(IllegalArgumentException.class, () -> {
            metadiumService.getIdentityCreationEventsBetween(from, to);
        });

        // Then
        assertThat(throwable).isNotNull();
        assertThat(throwable).isInstanceOf(IllegalArgumentException.class);
        assertThat(throwable).hasMessageContaining("Parameter 'from' should be larger than parameter 'to'. 'from': " + from  +  " 'to': " + to);
    }
}
