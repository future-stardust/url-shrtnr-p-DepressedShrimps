package url.shortener.server.dto;

import java.util.List;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class NotUniqueAliasErrorDto {

  private String message;
  private List<String> suggestions;
}
