package com.vouched.dao.endorsement;

import com.vouched.model.domain.Endorsement;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.statement.GetGeneratedKeys;
import org.jdbi.v3.sqlobject.statement.SqlQuery;

public interface EndorsementDao {


  // select paginated for user
  @SqlQuery(
      "SELECT * FROM endorsements WHERE endorse_id = :endorserId ORDER BY created_at DESC LIMIT :limit OFFSET :offset")
  List<Endorsement> getEndorsementsForEndorserId(@Bind UUID endorserId, @Bind int limit,
      @Bind int offset);


  @SqlQuery("SELECT * FROM endorsements WHERE id = :endorsementId")
  Optional<Endorsement> getEndorsement(UUID endorsementId);

  // delete
  @SqlQuery("DELETE FROM endorsements WHERE id = :endorsementId returning *")
  Optional<Endorsement> deleteEndorsement(UUID endorsementId);

  // create
  @SqlQuery("INSERT INTO endorsements(endorser_id, message, first_name, last_name, relationship) VALUES(:endorserId, :message, :firstName, :lastName, :relationship) returning *")
  @GetGeneratedKeys
  UUID createEndorsement(@Bind UUID endorserId, @Bind String message,
      @Bind String firstName, @Bind String lastName, @Bind String relationship);
}
