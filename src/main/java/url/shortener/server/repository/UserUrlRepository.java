package url.shortener.server.repository;

import java.util.List;
import url.shortener.server.entity.UserUrl;


public interface UserUrlRepository {

  List<String> findAll(String userId);

  boolean save(UserUrl userUrl);

  void delete(UserUrl userUrl);
}
