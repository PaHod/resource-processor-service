package com.pahod.music.resourceprocessor.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.loadbalancer.reactive.ReactorLoadBalancerExchangeFilterFunction;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class ResourceServiceClient {

  private final String host;
  private final String getResource;
  private final String notifyProcessed;
  private final WebClient webClient;

  public static final MediaType MEDIA_TYPE = MediaType.parseMediaType("audio/mpeg");

  public ResourceServiceClient(
      @Value("${client.services.resource.host}") String host,
      @Value("${client.services.resource.endpoint-get-resource}") String getResource,
      @Value("${client.services.resource.endpoint-notify-processed}") String notifyProcessed,
      ReactorLoadBalancerExchangeFilterFunction filterFunction,
      WebClient.Builder webClientBuilder) {
    this.host = host;
    this.getResource = getResource;
    this.notifyProcessed = notifyProcessed;
    this.webClient = webClientBuilder.filter(filterFunction).build();
  }

  public Resource fetchAudioFile(Integer resourceId) {
    String uri = host + getResource;
    return webClient
        .get()
        .uri(uri, resourceId)
        .accept(MEDIA_TYPE)
        .retrieve()
        .bodyToMono(Resource.class)
        .block();
  }

  public void notifyFileProcessed(Integer resourceId) {
    String uri = host + notifyProcessed;
    webClient
        .post()
        .uri(uri, resourceId)
        .retrieve()
        .bodyToMono(Void.class)
        .block();
  }
}
