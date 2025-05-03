package ru.boraldan.logaopstarter.starter.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.ConstructorBinding;

import java.util.List;

@ConfigurationProperties(prefix = "log-aspect.set-level")
public class LogProperties {

    private final String level;

    @ConstructorBinding
    public LogProperties(String level) {
        if (level == null || !List.of("INFO", "DEBUG", "WARN", "ERROR").contains(level.toUpperCase())) {
            this.level = "INFO";
            return;
        }
        this.level = level.toUpperCase();
    }

    public String getLevel() {
        return level;
    }


}
