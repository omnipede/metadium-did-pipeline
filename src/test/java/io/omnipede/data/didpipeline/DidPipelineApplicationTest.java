package io.omnipede.data.didpipeline;

import io.omnipede.data.didpipeline.application.DidPipeline;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class DidPipelineApplicationTest {

    @MockBean
    private DidPipeline didPipeline;

    @SpyBean
    private ThreadPoolTaskScheduler scheduler;

    @Test
    public void scheduler_should_be_created_and_scheduling_method_should_be_called() {

        // Given

        // When
        // Below code is just for coverage ...
        DidPipelineApplication.main(new String[]{});

        // Then
        verify(scheduler, times(1))
                .scheduleAtFixedRate(any(), eq(10 * 1000L));
        verify(didPipeline, atLeastOnce())
                .process();
    }
}