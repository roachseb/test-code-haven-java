package app;

import com.ferrylink.tmf.filter.mongodb.EntityBase;
import com.ferrylink.tmf.filter.mongodb.TmfReactiveRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.Map;

@ApplicationScoped
public class PartyRepo implements TmfReactiveRepositoryBase<Party> {

  @Override
  public Party fromMap(Map<String, Object> map) {
    return EntityBase.fromMap(map, Party.class, Party.BANNED_FIELDS);
  }
}
