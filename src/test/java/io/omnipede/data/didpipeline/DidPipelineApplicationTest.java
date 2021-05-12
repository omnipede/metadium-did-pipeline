package io.omnipede.data.didpipeline;

import io.omnipede.data.didpipeline.application.DidPipeline;
import io.omnipede.data.didpipeline.service.warehouse.DataWarehouseService;
import org.junit.BeforeClass;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.data.elasticsearch.ElasticsearchDataAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class DidPipelineApplicationTest {

    @Nested
    @ExtendWith(SpringExtension.class)
    @SpringBootTest
    @EnableAutoConfiguration(exclude = ElasticsearchDataAutoConfiguration.class)
    public class WiredUnitTest {

        @MockBean(name = "didIssuanceElasticsearchRepository")
        private ElasticsearchRepository<?, ?> elasticsearchRepository;

        @MockBean(name = "elasticsearchService")
        private DataWarehouseService dataWarehouseService;

        @MockBean
        private DidPipeline didPipeline;

        @SpyBean
        private ThreadPoolTaskScheduler scheduler;

        @Test
        public void scheduler_should_be_created_and_scheduling_method_should_be_called() {

            // Given
            long period = 10 * 1000L;

            // When

            // Then
            verify(scheduler, times(1))
                    .scheduleAtFixedRate(any(), eq(period));
            verify(didPipeline, times(1))
                    .process();
        }
    }

    @Nested
    public class NonWiredUnitTest {

        // Given
        MockedStatic<SpringApplication> mockedStatic;

        @BeforeEach
        public void setup() {

            mockedStatic = mockStatic(SpringApplication.class);
        }

        @AfterEach
        public void cleanup() {

            mockedStatic.close();
        }

        @Test
        public void main_method_coverage() {
            // Given

            // When
            DidPipelineApplication.main(new String[]{});

            // Then
            mockedStatic.verify(() -> {
                SpringApplication.run(eq(DidPipelineApplication.class), any());
            }, times(1));
        }
    }
}