package io.omnipede.data.didpipeline.system.config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(
        classes = BeanConfig.class
)
public class BeanConfigTest {

    @Autowired
    private ThreadPoolTaskScheduler scheduler;

    @Test
    public void ThreadPoolTaskScheduler_bean_should_be_wired() {

        assertThat(scheduler).isNotNull();
    }
}