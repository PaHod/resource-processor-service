package com.pahod.music.resourceprocessor.service;

import com.pahod.music.resourceprocessor.client.ResourceServiceClient;
import com.pahod.music.resourceprocessor.client.SongClient;
import com.pahod.music.resourceprocessor.exception.FileParsingException;
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
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.xml.sax.SAXException;

@Slf4j
@Service
@RequiredArgsConstructor
public class ResourceService {

  private final SongClient songClient;
  private final ResourceServiceClient resourceServiceClient;

  public void newResourceUploaded(Integer resourceId) {
    MultipartFile audioFile = resourceServiceClient.fetchAudioFile(resourceId);
    log.debug("retrieved audio file from resource service with resourceId: {}", resourceId);

    Metadata metadata = parseMetadata(audioFile);
    songClient.saveMetadata(metadata, resourceId);
  }

  private static Metadata parseMetadata(MultipartFile audioFile) {
    Mp3Parser mp3Parser = new Mp3Parser();
    BodyContentHandler handler = new BodyContentHandler();
    Metadata metadata = new Metadata();
    ParseContext parseContext = new ParseContext();

    try (InputStream inputStream = audioFile.getInputStream()) {
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
