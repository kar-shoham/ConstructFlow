package com.constructflow.timesheet_service.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {

    @Bean
    public NewTopic timesheetApprovedTopic() {
        return TopicBuilder.name("timesheet-approved")
                .partitions(1)
                .replicas(1)
                .build();
    }
}
