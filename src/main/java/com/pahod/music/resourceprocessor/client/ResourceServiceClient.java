package com.pahod.music.resourceprocessor.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.loadbalancer.reactive.ReactorLoadBalancerExchangeFilterFunction;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class ResourceServiceClient {

  private final String apiURI;
  private final WebClient webClient;

  public static final MediaType MEDIA_TYPE = MediaType.parseMediaType("audio/mpeg");

  public ResourceServiceClient(
      @Value("${client.services.resource.endpoint}") String apiURI,
      ReactorLoadBalancerExchangeFilterFunction filterFunction,
      WebClient.Builder webClientBuilder) {
    this.apiURI = apiURI;
    this.webClient = webClientBuilder.filter(filterFunction).build();
  }

  public Resource fetchAudioFile(Integer resourceId) {
    return webClient
        .get()
        .uri(apiURI, resourceId)
        .accept(MEDIA_TYPE)
        .retrieve()
        .bodyToMono(Resource.class)
        .block();
  }
}
