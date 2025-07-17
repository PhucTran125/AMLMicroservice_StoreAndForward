package com.vpbankhackathon.store_and_forward_service.utils.error_handlers;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.TopicPartition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.listener.CommonErrorHandler;
import org.springframework.kafka.listener.MessageListenerContainer;
import org.springframework.stereotype.Component;

@Component
public class CustomErrorHandler implements CommonErrorHandler {

    private static final Logger logger = LoggerFactory.getLogger(CustomErrorHandler.class);

    public void handleRecord(Exception thrownException, ConsumerRecord<?, ?> record,
                             Consumer<?, ?> consumer, MessageListenerContainer container) {
        if (thrownException instanceof org.apache.kafka.common.errors.SerializationException) {
            logger.error("Serialization exception for record from topic {} at offset {}: {}",
                record.topic(), record.offset(), thrownException.getMessage());
            // Acknowledge the offset to skip the failed record
            consumer.seek(new TopicPartition(record.topic(), record.partition()), record.offset() + 1);
            consumer.commitSync(); // Sync commit to ensure offset is acknowledged
        } else {
            // Re-throw other exceptions if needed
            throw new RuntimeException("Unhandled exception", thrownException);
        }
    }

    @Override
    public void handleOtherException(Exception thrownException, Consumer<?, ?> consumer,
                                     MessageListenerContainer container, boolean batchListener) {
        logger.error("Other exception occurred in Kafka consumer container: {}",
            thrownException.getMessage(), thrownException);

        try {
            // Log container information
            if (container != null) {
                logger.error("Container: {}, Group ID: {}, Batch Listener: {}",
                    container.getClass().getSimpleName(),
                    container.getGroupId(),
                    batchListener);
            }

            // Log consumer information if available
            if (consumer != null) {
                try {
                    var assignment = consumer.assignment();
                    if (!assignment.isEmpty()) {
                        logger.error("Consumer assignment: {}", assignment);

                        // Get current positions for all assigned partitions
                        assignment.forEach(partition -> {
                            try {
                                long position = consumer.position(partition);
                                logger.error("Current position for partition {}: {}", partition, position);
                            } catch (Exception e) {
                                logger.error("Failed to get position for partition {}: {}", partition, e.getMessage());
                            }
                        });
                    }
                } catch (Exception e) {
                    logger.error("Failed to get consumer assignment: {}", e.getMessage());
                }
            }

            // Handle specific exception types
            if (thrownException instanceof org.apache.kafka.common.errors.WakeupException) {
                logger.warn("Consumer wakeup exception - container might be stopping");
                // Don't restart for wakeup exceptions as they're usually intentional
                return;
            } else if (thrownException instanceof org.apache.kafka.common.errors.AuthenticationException ||
                thrownException instanceof org.apache.kafka.common.errors.AuthorizationException) {
                logger.error("Authentication/Authorization error - stopping container");
                if (container != null) {
                    container.stop();
                }
                return;
            } else if (thrownException instanceof org.apache.kafka.common.errors.NetworkException ||
                thrownException instanceof org.apache.kafka.common.errors.TimeoutException) {
                logger.warn("Network/Timeout exception - will retry with backoff");
                // Add delay before retry
                try {
                    Thread.sleep(5000); // 5 second backoff
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    logger.error("Interrupted during backoff");
                }
            }

            // For other exceptions, attempt recovery
            if (consumer != null) {
                try {
                    // Seek to the beginning of assigned partitions if needed
                    var assignment = consumer.assignment();
                    if (!assignment.isEmpty()) {
                        logger.info("Attempting to seek to end offset for recovery");
                        consumer.seekToEnd(assignment);
                        consumer.commitSync();
                        logger.info("Successfully seeked to end offset");
                    }
                } catch (Exception seekException) {
                    logger.error("Failed to seek consumer for recovery: {}", seekException.getMessage());
                }
            }

        } catch (Exception handlingException) {
            logger.error("Exception occurred while handling other exception: {}",
                handlingException.getMessage(), handlingException);
        }

        // Log final summary
        logger.error("Completed handling other exception: {}", thrownException.getClass().getSimpleName());
    }
}
