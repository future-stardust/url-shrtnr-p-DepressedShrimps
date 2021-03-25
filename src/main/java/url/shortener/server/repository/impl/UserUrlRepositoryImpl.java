package url.shortener.server.repository.impl;

import url.shortener.server.bigtable.MultiValueTable;
import url.shortener.server.entity.UserUrl;
import url.shortener.server.repository.UserUrlRepository;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import javax.validation.constraints.NotNull;
import java.util.List;

@Singleton
public class UserUrlRepositoryImpl implements UserUrlRepository {

  private final MultiValueTable userUrlTable;

  @Inject
  public UserUrlRepositoryImpl(@Named("userUrlTable") MultiValueTable userUrlTable) {
    this.userUrlTable = userUrlTable;
  }

  @Override
  public List<String> findAll(@NotNull String userId) {
    return userUrlTable.findByKey(userId);
  }

  @Override
  public boolean save(@NotNull UserUrl userUrl) {
    return userUrlTable.put(userUrl.getUserId(), userUrl.getAlias());
  }

  @Override
  public void delete(@NotNull UserUrl userUrl) {
    userUrlTable.deleteByKey(userUrl.getUserId(), userUrl.getAlias());
  }
}
