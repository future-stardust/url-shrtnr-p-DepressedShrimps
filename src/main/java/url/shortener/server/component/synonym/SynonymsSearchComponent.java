package url.shortener.server.component.synonym;

import java.util.List;
import javax.validation.constraints.NotBlank;

public interface SynonymsSearchComponent {

  boolean isSearchable(@NotBlank String alias);

  List<String> retrieveSynonyms(@NotBlank String alias, int proposalCount);
}
