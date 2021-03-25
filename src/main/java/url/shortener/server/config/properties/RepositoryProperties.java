package url.shortener.server.config.properties;

import io.micronaut.context.annotation.ConfigurationBuilder;
import io.micronaut.context.annotation.ConfigurationProperties;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

@Data
@Accessors(chain = true)
@ConfigurationProperties("repository")
public class RepositoryProperties {

  @ConfigurationBuilder(configurationPrefix = "token")
  private TableConfig token = new TableConfig();

  @ConfigurationBuilder(configurationPrefix = "url")
  private TableConfig url = new TableConfig();

  @ConfigurationBuilder(configurationPrefix = "user")
  private TableConfig user = new TableConfig();

  @ConfigurationBuilder(configurationPrefix = "userUrl")
  private TableConfig userUrl = new TableConfig();


  @Data
  @Accessors(chain = true)
  public static class TableConfig {

    @NotBlank
    private String tableName;

    @Min(1)
    private int bucketNumber;

    @Min(1)
    private int keyLength;
  }
}
