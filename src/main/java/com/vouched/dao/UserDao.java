package com.vouched.dao;

import com.vouched.model.domain.User;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.statement.GetGeneratedKeys;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

import java.util.Optional;
import java.util.UUID;

public interface UserDao {

    @SqlQuery("SELECT * FROM users WHERE email = :emailAddress")
    Optional<User> getUserByEmail(String emailAddress);


    @SqlUpdate("INSERT INTO users (email, external_id) VALUES (:email, :externalId) on conflict do nothing")
    @GetGeneratedKeys
    Optional<UUID> createUserFromEmail(@Bind String email, @Bind String externalId);

    @SqlQuery("SELECT * FROM users WHERE id = :id")
    Optional<User> getUserById(UUID id);

    @SqlQuery("SELECT * FROM users WHERE handle = :handle")
    Optional<User> getUserByHandle(String handle);

}
