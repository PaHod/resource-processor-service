package com.pahod.music.resourceprocessor.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class ResourceServiceClient {
  public static final String LOAD_BALANCER_PREFIX = "lb://";

  private final String apiURI;
  private final WebClient webClient;

  public ResourceServiceClient(
      @Value("${client.services.resource.endpoint}") String apiURI,
      @Value("${client.services.resource.discoveryName}") String discoveryName,
      WebClient.Builder webClientBuilder) {
    this.apiURI = apiURI;
    this.webClient = webClientBuilder.baseUrl(LOAD_BALANCER_PREFIX + discoveryName).build();
  }

  public MultipartFile fetchAudioFile(Integer resourceId) {
    return webClient
        .get()
        .uri(apiURI, resourceId)
        .accept(MediaType.MULTIPART_FORM_DATA)
        .retrieve()
        .bodyToMono(MultipartFile.class)
        .block();
  }
}
