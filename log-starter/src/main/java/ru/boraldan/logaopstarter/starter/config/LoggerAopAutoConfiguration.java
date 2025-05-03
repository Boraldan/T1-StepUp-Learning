package ru.boraldan.logaopstarter.starter.config;

import org.apache.logging.log4j.Level;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.boraldan.logaopstarter.starter.aspect.LogAspect;

@Configuration
@EnableConfigurationProperties(LogProperties.class)
@ConditionalOnProperty(prefix = "log-aspect", name = "enable", havingValue = "true", matchIfMissing = true)
public class LoggerAopAutoConfiguration {

    @Bean
    public LogAspect logAspect(LogProperties logProperties) {
        return new LogAspect(Level.toLevel(logProperties.getLevel()));
    }

}
