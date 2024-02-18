package com.vouched.dao.endorsement;

import com.vouched.model.domain.EndorserAccess;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.statement.SqlQuery;

// use endorser_access table
public interface AccessDao {


  // select
  @SqlQuery("SELECT * FROM endorser_access WHERE endorser_id = :endorserId and requester_email = :requesterEmail")
  Optional<EndorserAccess> getEndorserAccess(@Bind UUID endorserId,
      @Bind String requesterEmail);

  // update
  @SqlQuery("UPDATE endorser_access SET approved_at = now() WHERE endorser_id = :endorserId and id = :id returning *")
  Optional<EndorserAccess> approveEndorserAccess(@Bind UUID endorserId,
      @Bind UUID id);

  // delete
  @SqlQuery("DELETE FROM endorser_access WHERE endorser_id = :endorserId and id = :id returning *")
  Optional<EndorserAccess> deleteEndorserAccess(@Bind UUID endorserId, @Bind UUID id);

  // create
  @SqlQuery("INSERT INTO endorser_access(endorser_id, requester_email, message) VALUES(:endorserId, :requesterEmail, :message) returning *")
  UUID createEndorserAccess(@Bind UUID endorserId, @Bind String requesterEmail,
      @Bind String message);

  // select paginated for endorser
  @SqlQuery("SELECT * FROM endorser_access WHERE endorser_id = :endorserId ORDER BY approved_at DESC LIMIT :limit OFFSET :offset")
  List<EndorserAccess> getEndorserAccessForEndorser(@Bind UUID endorserId,
      @Bind int limit, @Bind int offset);


  // select paginated for user
  @SqlQuery("SELECT * FROM endorser_access WHERE requester_email = :requesterEmail and endorser_id=:endorserId and approved_at is not null ORDER BY approved_at DESC LIMIT :limit OFFSET :offset")
  List<EndorserAccess> getApprovedEndorserAccessForUser(@Bind String requesterEmail,
      @Bind UUID endorserId,
      @Bind int limit, @Bind int offset);


  @SqlQuery("SELECT * FROM endorser_access WHERE id = :accessId")
  EndorserAccess getEndorsementById(@Bind UUID accessId);
}
