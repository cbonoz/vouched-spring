package com.vouched.dao;

import com.vouched.model.domain.Endorsement;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.statement.GetGeneratedKeys;
import org.jdbi.v3.sqlobject.statement.SqlQuery;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EndorsementDao {

    // insert
    @SqlQuery("INSERT INTO endorsements (user_id, endorser_id, message) VALUES (:userId, :endorserId, :message)")
    @GetGeneratedKeys
    UUID createEndorsement(@Bind UUID userId, @Bind UUID endorserId, @Bind String message);

// select paginated for endorser
    @SqlQuery("SELECT * FROM endorsements WHERE endorser_id = :endorserId ORDER BY created_at DESC LIMIT :limit OFFSET :offset")
    List<Endorsement> getEndorsementsForEndorser(@Bind UUID endorserId, @Bind int limit, @Bind int offset);

    // select paginated for user
    @SqlQuery("SELECT * FROM endorsements WHERE user_id = :userId ORDER BY created_at DESC LIMIT :limit OFFSET :offset")
    List<Endorsement> getEndorsementsForUser(@Bind UUID userId, @Bind int limit, @Bind int offset);

// update
    @SqlQuery("UPDATE endorsements SET message = :message WHERE user_id = :userId and endorser_id = :endorserId returning *")
    Optional<Endorsement> updateEndorsement(@Bind String message, @Bind UUID userId, @Bind UUID endorserId);



    @SqlQuery("SELECT * FROM endorsements WHERE id = :endorsementId")
    Endorsement getEndorsement(UUID endorsementId);

    // delete
    @SqlQuery("DELETE FROM endorsements WHERE id = :endorsementId returning *")
    Optional<Endorsement> deleteEndorsement(UUID endorsementId);
}
