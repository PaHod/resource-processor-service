package com.pahod.music.resourceprocessor.listener;

import com.pahod.music.resourceprocessor.service.ResourceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class RabbitMQConsumer {

  private final ResourceService resourceService;

  @RabbitListener(queues = {"${rabbitmq.queue.name}"})
  public void consume(String message) {

    log.info(String.format("Received message -> %s", message));
    resourceService.newResourceUploaded(Integer.valueOf(message));
  }
}
