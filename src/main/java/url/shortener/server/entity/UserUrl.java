package url.shortener.server.entity;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class UserUrl {

  private String userId;
  private String alias;

}
