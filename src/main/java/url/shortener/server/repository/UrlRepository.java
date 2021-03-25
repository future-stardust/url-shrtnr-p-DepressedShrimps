package url.shortener.server.repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import url.shortener.server.entity.ShortenedUrl;

public interface UrlRepository {

  List<ShortenedUrl> findAll(Set<String> aliases);

  Optional<ShortenedUrl> findById(String alias);

  boolean save(ShortenedUrl shortenedUrl);

  void deleteById(String alias);

  boolean existsById(String alias);

  String nextAvailableAlias();

  List<String> findAvailableAliases(int count);
}
