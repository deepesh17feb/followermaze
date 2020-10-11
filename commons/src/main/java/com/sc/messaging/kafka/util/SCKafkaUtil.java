package com.sc.messaging.kafka.util;

import com.sc.messaging.kafka.config.SCKafkaConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.admin.AdminClient;

import java.util.Properties;
import java.util.concurrent.ExecutionException;

@Slf4j
public class SCKafkaUtil {

    public static boolean kafkaTopicExists(SCKafkaConfig scKafkaConfig, String topic) {

        Properties properties = new Properties();
        properties.setProperty(CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG, scKafkaConfig.getBootstrapServers());

        AdminClient client = AdminClient.create(properties);

        try {
            log.debug("Required Topic [{}] - Total Topics List : [{}]", client.listTopics().names().get());
            return client.listTopics().names().get().stream().anyMatch(topicName -> topicName.equals(topic));
        } catch (InterruptedException e) {
            log.error("Error while listing kafka topic - {}", e.getMessage());
        } catch (ExecutionException e) {
            log.error("Error while listing kafka topic", e.getMessage());
        }

        return false;
    }

}
