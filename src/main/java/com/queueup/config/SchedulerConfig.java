package com.queueup.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling
public class SchedulerConfig {
    // Just enables @Scheduled annotation
    // The actual scheduling logic is in QueueScheduler
}