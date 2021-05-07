package io.omnipede.data.didpipeline.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.elasticsearch.ElasticsearchContainer;
import org.testcontainers.utility.DockerImageName;

/**
 * 테스트 시작 전 docker container 통합 테스트 환경을 구성하는 코드
 */
public abstract class DockerIntegration {

    private static final Logger logger = LoggerFactory.getLogger(DockerIntegration.class);

    private static final DockerImageName ELASTIC_SEARCH = DockerImageName.parse("docker.elastic.co/elasticsearch/elasticsearch:7.10.1");

    private static final ElasticsearchContainer elasticsearch = new ElasticsearchContainer(ELASTIC_SEARCH)
            .withLogConsumer(new Slf4jLogConsumer(logger));

    static {
        logger.info("Docker containers are starting up");
        elasticsearch.start();
    }

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        registry.add("spring.elasticsearch.rest.uris", elasticsearch::getHttpHostAddress);
    }
}
