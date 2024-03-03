package com.pahod.music.resourceprocessor.web.controller;

import com.pahod.music.resourceprocessor.service.ResourceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1/processor/resources")
@RequiredArgsConstructor
public class ResourceController {

  private final ResourceService resourceService;

  @GetMapping("/ping")
  public ResponseEntity<?> pingPong() {
    return ResponseEntity.ok("processor pong");
  }

  @PostMapping("/{id}")
  public void getResource(@PathVariable("id") int id) {
    log.debug("Notified about new resource uploaded with id: {}", id);

    resourceService.newResourceUploaded(id);
  }
}
