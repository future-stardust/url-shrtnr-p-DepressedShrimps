package url.shortener.server.component.synonym.impl;

import io.micronaut.core.type.Argument;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.client.RxHttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.uri.UriBuilder;
import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javax.inject.Singleton;
import javax.validation.constraints.NotBlank;
import lombok.Data;
import url.shortener.server.component.synonym.SynonymsSearchComponent;

@Singleton
public class DatamuseSynonymsSearchComponentImpl implements SynonymsSearchComponent {

  private static final Pattern WORD_PATTERN = Pattern.compile("[a-zA-Z]+");

  private final RxHttpClient synonymsClient;

  public DatamuseSynonymsSearchComponentImpl(@Client("${synonyms.api.host}") RxHttpClient synonymsClient) {
    this.synonymsClient = synonymsClient;
  }

  @Override
  public boolean isSearchable(@NotBlank String alias) {
    return WORD_PATTERN.matcher(alias).matches();
  }

  @Override
  public List<String> retrieveSynonyms(@NotBlank String alias, int proposalCount) {
    URI uri = UriBuilder.of("/words")
        .queryParam("rel_syn", alias)
        .queryParam("max", proposalCount)
        .build();

    return synonymsClient.exchange(HttpRequest.GET(uri), Argument.listOf(Synonym.class))
        .map(HttpResponse::getBody)
        .filter(Optional::isPresent)
        .map(Optional::get)
        .blockingFirst()
        .stream()
        .map(Synonym::getWord)
        .collect(Collectors.toList());
  }

  @Data
  public static class Synonym {

    private String word;
    private int score;
  }
}
