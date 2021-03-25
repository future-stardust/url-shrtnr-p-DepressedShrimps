package url.shortener.server.config;

import io.micronaut.context.annotation.Bean;
import io.micronaut.context.annotation.Factory;
import url.shortener.server.bigtable.BigTable;
import url.shortener.server.bigtable.MultiValueTable;
import url.shortener.server.bigtable.impl.BigTableImpl;
import url.shortener.server.bigtable.impl.MultiValueTableImpl;
import url.shortener.server.config.properties.RepositoryProperties;
import url.shortener.server.config.properties.RepositoryProperties.TableConfig;

import javax.inject.Named;
import javax.inject.Singleton;

@Factory
public class RepositoryFactory {

  @Bean
  @Singleton
  @Named("tokenTable")
  public BigTable tokenTable(RepositoryProperties repositoryProperties) {
    TableConfig tokenConfig = repositoryProperties.getToken();
    return new BigTableImpl(
        tokenConfig.getTableName(),
        tokenConfig.getBucketNumber(),
        tokenConfig.getKeyLength()
    );
  }

  @Bean
  @Singleton
  @Named("urlTable")
  public BigTable urlTable(RepositoryProperties repositoryProperties) {
    TableConfig urlConfig = repositoryProperties.getUrl();
    return new BigTableImpl(
        urlConfig.getTableName(),
        urlConfig.getBucketNumber(),
        urlConfig.getKeyLength()
    );
  }

  @Bean
  @Singleton
  @Named("userTable")
  public BigTable userTable(RepositoryProperties repositoryProperties) {
    TableConfig userConfig = repositoryProperties.getUser();
    return new BigTableImpl(
        userConfig.getTableName(),
        userConfig.getBucketNumber(),
        userConfig.getKeyLength()
    );
  }

  @Bean
  @Singleton
  @Named("userUrlTable")
  public MultiValueTable userUrlTable(RepositoryProperties repositoryProperties) {
    TableConfig userUrl = repositoryProperties.getUserUrl();
    return new MultiValueTableImpl(
        userUrl.getTableName(),
        userUrl.getBucketNumber(),
        userUrl.getKeyLength()
    );
  }
}
