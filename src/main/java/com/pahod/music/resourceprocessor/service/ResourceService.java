package com.pahod.music.resourceprocessor.service;

import com.pahod.music.resourceprocessor.client.ResourceServiceClient;
import com.pahod.music.resourceprocessor.client.SongClient;
import com.pahod.music.resourceprocessor.exception.FileParsingException;
import com.pahod.music.resourceprocessor.exception.ResourceNotFoundException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.mp3.LyricsHandler;
import org.apache.tika.parser.mp3.Mp3Parser;
import org.apache.tika.sax.BodyContentHandler;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.xml.sax.SAXException;

@Slf4j
@Service
@RequiredArgsConstructor
public class ResourceService {

  private final SongClient songClient;
  private final ResourceServiceClient resourceServiceClient;

  public void newResourceUploaded(Integer resourceId) {
    byte[] byteArray;
    try {
      Resource fileData = resourceServiceClient.fetchAudioFile(resourceId);
      byteArray = fileData.getContentAsByteArray();
    } catch (IOException e) {
      throw new ResourceNotFoundException(
          String.format("Resource with ID %d not found", resourceId));
    }

    log.debug("retrieved audio file from resource service with resourceId: {}", resourceId);

    Metadata metadata = parseMetadata(byteArray);
    songClient.saveMetadata(metadata, resourceId);
    resourceServiceClient.notifyFileProcessed(resourceId);
  }

  private static Metadata parseMetadata(byte[] fileData) {
    Mp3Parser mp3Parser = new Mp3Parser();
    BodyContentHandler handler = new BodyContentHandler();
    Metadata metadata = new Metadata();
    ParseContext parseContext = new ParseContext();

    try (InputStream inputStream = new ByteArrayInputStream(fileData)) {
      mp3Parser.parse(inputStream, handler, metadata, parseContext);

      LyricsHandler lyrics = new LyricsHandler(inputStream, handler);
      while (lyrics.hasLyrics()) {
        System.out.println(lyrics);
      }
    } catch (IOException | SAXException | TikaException e) {
      throw new FileParsingException("Failed to parse metadata.");
    }

    return metadata;
  }
}
