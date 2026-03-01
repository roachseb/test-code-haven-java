package app;

import java.util.List;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.ferrylink.tmf.filter.mongodb.EntityBase;
import io.quarkus.mongodb.panache.common.MongoEntity;


@Schema(description = "Represents a party with roles")
@MongoEntity(collection = "parties")
public class Party extends EntityBase {

  @Schema(description = "List of roles associated with the party", example = "[\"admin\", \"user\"]")
  public List<String> roles = List.of();

  public Party() {
  }

  @JsonCreator
  public Party(List<String> roles) {
    this.roles = roles;
  }
}