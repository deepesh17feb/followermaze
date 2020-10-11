package com.sc.messaging;

import com.sc.messaging.constant.MessagingComponent;
import com.sc.messaging.constant.MessagingProvider;
import com.sc.messaging.file.config.SCFileConfig;
import com.sc.messaging.file.consumer.SCFileConsumer;
import com.sc.messaging.file.producer.SCFileProducer;
import com.sc.messaging.kafka.config.SCKafkaConfig;
import com.sc.messaging.kafka.consumer.SCKafkaConsumerFactory;
import com.sc.messaging.kafka.producer.SCKafkaProducerFactory;
import com.sc.messaging.kafka.stream.impl.MazeKafkaStream;

/**
 * Master Factory Class to generate all type of messaging components
 */
public interface MessageFactory {

    static MessageFactoryBuilder builder() {
        return new MessageFactoryBuilder();
    }

    class MessageFactoryBuilder {

        private MessagingProvider provider;
        private MessagingComponent component;
        private IConfig config;

        public MessageFactoryBuilder provider(MessagingProvider provider) {
            this.provider = provider;
            return this;
        }

        public MessageFactoryBuilder component(MessagingComponent component) {
            this.component = component;
            return this;
        }

        public MessageFactoryBuilder config(IConfig config) {
            this.config = config;
            return this;
        }

        public IMessaging build() {
            switch (this.provider) {
                case KAFKA: {

                    switch (this.component) {
                        case PRODUCER:

                            if (this.config instanceof SCKafkaConfig) {
                                return SCKafkaProducerFactory.createSoundCloudKafkaProducer((SCKafkaConfig) this.config);
                            }
                            throw new IllegalArgumentException("Invalid Producer Config");

                        case CONSUMER:

                            if (this.config instanceof SCKafkaConfig) {
                                return SCKafkaConsumerFactory.createSoundCloudKafkaConsumer((SCKafkaConfig) this.config);
                            }
                            throw new IllegalArgumentException("Invalid Consumer Config");

                        case STREAM:
                            if (this.config instanceof SCKafkaConfig) {
                                return new MazeKafkaStream((SCKafkaConfig) this.config);
                            }
                            throw new IllegalArgumentException("Invalid Stream Config");

                        default:
                            throw new IllegalArgumentException("Messaging Component not supported");
                    }
                }

                case FILE:
                    switch (this.component) {
                        case PRODUCER:

                            if (this.config instanceof SCFileConfig) {
                                return new SCFileProducer((SCFileConfig) this.config);
                            }
                            throw new IllegalArgumentException("Invalid Producer Config");

                        case CONSUMER:

                            if (this.config instanceof SCFileConfig) {
                                return new SCFileConsumer((SCFileConfig) this.config);
                            }
                            throw new IllegalArgumentException("Invalid Consumer Config");

                        default:
                            throw new IllegalArgumentException("Messaging Component not supported");
                    }
                default:
                    throw new IllegalArgumentException("Messaging Provider not supported");
            }
        }

    }
}
