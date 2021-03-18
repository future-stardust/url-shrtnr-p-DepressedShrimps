package edu.kpi.testcourse.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;

/**
 * User profile, sufficient for signing in. User-generated content is stored separately.
 */
public record User(
  @JsonProperty("email") String email,
  @JsonProperty("passwordHash") String passwordHash,
  @JsonProperty("urls") ArrayList<String> urls
) {}
