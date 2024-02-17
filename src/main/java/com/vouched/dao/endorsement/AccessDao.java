package com.vouched.dao.endorsement;

import com.vouched.model.domain.EndorserAccess;
import java.util.Date;
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
      @Bind String requestEmail);

  // update
  @SqlQuery("UPDATE endorser_access SET approved_at = :approvedAt WHERE endorser_id = :endorserId and requester_id = :requesterId returning *")
  Optional<EndorserAccess> updateEndorserAccess(@Bind UUID endorserId,
      @Bind UUID requesterId, @Bind Date approvedAt);

  // delete
  @SqlQuery("DELETE FROM endorser_access WHERE endorser_id = :endorserId and requester_id = :requesterId returning *")
  Optional<EndorserAccess> deleteEndorserAccess(@Bind UUID endorserId,
      @Bind UUID requesterId);

  // create
  @SqlQuery("INSERT INTO endorser_access(endorser_id, requester_id, approved_at) VALUES(:endorserId, :requesterId, :approvedAt) returning *")
  UUID createEndorserAccess(@Bind UUID endorserId, @Bind UUID requesterId,
      @Bind Date approvedAt);

  // select paginated for endorser
  @SqlQuery("SELECT * FROM endorser_access WHERE endorser_id = :endorserId ORDER BY approved_at DESC LIMIT :limit OFFSET :offset")
  List<EndorserAccess> getEndorserAccessForEndorser(@Bind UUID endorserId,
      @Bind int limit, @Bind int offset);

  // select paginated for user
  @SqlQuery("SELECT * FROM endorser_access WHERE requester_id = :requesterId ORDER BY approved_at DESC LIMIT :limit OFFSET :offset")
  List<EndorserAccess> getEndorserAccessForUser(@Bind UUID requesterId, @Bind int limit,
      @Bind int offset);

  // select paginated for user
  @SqlQuery("SELECT * FROM endorser_access WHERE requester_id = :requesterId and approved_at is not null ORDER BY approved_at DESC LIMIT :limit OFFSET :offset")
  List<EndorserAccess> getApprovedEndorserAccessForUser(@Bind UUID requesterId,
      @Bind int limit, @Bind int offset);


}
