package io.omnipede.data.didpipeline.service.blockchain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.web3j.crypto.Keys;
import org.web3j.protocol.Web3j;

import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = MetadiumConfig.class)
public class MetadiumConfigTest {

    private MetadiumConfig metadiumConfig;

    @Autowired
    private Web3j wiredWeb3j;

    @Autowired
    private IdentityRegistry wiredIdentityRegistry;

    @BeforeEach
    public void setup() {

        metadiumConfig = new MetadiumConfig();
    }

    @Test
    public void web3j_should_be_wired() {

        assertThat(wiredWeb3j).isNotNull();
    }

    @Test
    public void identityRegistry_should_be_wired() {

        assertThat(wiredIdentityRegistry).isNotNull();
    }

    @Test
    public void identityRegistry_creation_should_fail_when_exception_occur_while_creating_ec_key() {

        // Given
        Web3j web3j = metadiumConfig.web3j();
        MockedStatic<Keys> keysMockedStatic = mockStatic(Keys.class);
        keysMockedStatic.when(Keys::createEcKeyPair)
                .thenThrow(new InvalidAlgorithmParameterException())
                .thenThrow(new NoSuchProviderException())
                .thenThrow(new NoSuchAlgorithmException());

        // When
        Throwable throwable1 = assertThrows(RuntimeException.class, () -> {
            metadiumConfig.identityRegistry(web3j);
        });

        Throwable throwable2 = assertThrows(RuntimeException.class, () -> {
            metadiumConfig.identityRegistry(web3j);
        });

        Throwable throwable3 = assertThrows(RuntimeException.class, () -> {
            metadiumConfig.identityRegistry(web3j);
        });

        // Then
        assertThat(throwable1).hasMessageContaining("Error while creating IdentityRegistry contract wrapper");
        assertThat(throwable1).hasCause(new InvalidAlgorithmParameterException());

        assertThat(throwable2).hasMessageContaining("Error while creating IdentityRegistry contract wrapper");
        assertThat(throwable2).hasCause(new NoSuchProviderException());

        assertThat(throwable3).hasMessageContaining("Error while creating IdentityRegistry contract wrapper");
        assertThat(throwable3).hasCause(new NoSuchAlgorithmException());
        keysMockedStatic.close();
    }
}