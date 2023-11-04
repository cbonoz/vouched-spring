//package com.vouched.dao;
//
//
//import com.vouched.model.domain.Comment;
//import com.vouched.model.dto.CommentRating;
//import com.vouched.model.dto.CommentResponse;
//import com.vouched.model.dto.FlaggedComment;
//import org.jdbi.v3.sqlobject.SingleValue;
//import org.jdbi.v3.sqlobject.customizer.Bind;
//import org.jdbi.v3.sqlobject.statement.GetGeneratedKeys;
//import org.jdbi.v3.sqlobject.statement.SqlQuery;
//import org.jdbi.v3.sqlobject.statement.SqlUpdate;
//
//import java.util.List;
//import java.util.Optional;
//import java.util.UUID;
//
//public interface CommentDao {
//    @SqlQuery("SELECT * FROM comments where target_user_id = :targetUserId")
//    List<Comment> getCommentsForUser(@Bind UUID targetUserId);
//
//    @SqlQuery("SELECT * FROM comments WHERE id = :id")
//    Optional<Comment> getCommentById(@Bind UUID id);
//
//    @SqlQuery("SELECT * FROM comments WHERE id = :id and author_id = :authorId")
//    Optional<Comment> getCommentByIdCheckUser(@Bind UUID id, @Bind UUID authorId);
//
//    @SqlUpdate("INSERT INTO comments (author_id, target_user_id, comment_text, anonymous, anonymous_animal, created_at, updated_at) " +
//            "VALUES (:authorId, :targetUserId, :comment, :anon, :anonAnimal, now(), now())" +
//            "on conflict (author_id, target_user_id) do update set comment_text = :comment, deleted_at = null, anonymous = :anon, anonymous_animal = :anonAnimal, updated_at = now()")
//    @GetGeneratedKeys
//    UUID upsertComment(@Bind UUID authorId,
//                       @Bind UUID targetUserId,
//                       @Bind String comment,
//                       @Bind boolean anon,
//                       @Bind String anonAnimal);
//
//    @SqlUpdate("UPDATE comments SET comment_text = :message WHERE id = :id")
//        // Return object
//    void updateComment(@Bind String message, @Bind UUID id);
//
//    @SqlUpdate("update comments set deleted_at = now() where id = :id")
//    void deleteCommentById(@Bind UUID id);
//
//    @SqlQuery("""
//            WITH vote_counts AS (
//              SELECT comment_id,
//                     SUM(CASE WHEN upvote = TRUE THEN 1 ELSE 0 END) AS upVotes
//              FROM comment_votes
//              GROUP BY comment_id
//            )
//            SELECT CASE
//                     WHEN c.author_id = :userId THEN CONCAT(u.first_name, ' ', u.last_name, ' (You)')
//                     WHEN c.anonymous = TRUE THEN CONCAT('Anonymous ', c.anonymous_animal, ' ', LEFT(u.id::text, 6))
//                     ELSE CONCAT(u.first_name, ' ', u.last_name)
//                   END AS authorName,
//                   COALESCE(vc.upVotes, 0) AS upVotes,
//                   SUM(CASE WHEN cv.upvote = FALSE THEN 1 ELSE 0 END) AS downVotes,
//                   SUM(CASE WHEN cv.user_id = :userId AND cv.upvote = TRUE THEN 1 ELSE 0 END) AS hasUpvoted,
//                   SUM(CASE WHEN cv.user_id = :userId AND cv.upvote = FALSE THEN 1 ELSE 0 END) AS hasDownvoted,
//                   c.*
//            FROM comments c
//            JOIN users u ON c.author_id = u.id
//                        AND c.target_user_id IN (SELECT id FROM users WHERE linkedin_url = :query)
//            LEFT JOIN vote_counts vc ON c.id = vc.comment_id
//            LEFT JOIN comment_votes cv ON c.id = cv.comment_id
//            WHERE c.id IS NOT NULL
//              AND c.deleted_at IS NULL
//            GROUP BY c.id,
//                     c.created_at,
//                     u.first_name,
//                     u.last_name,
//                     c.deleted_at,
//                     u.id,
//                     vc.upVotes
//            ORDER BY CASE WHEN :sortByUpvotes = TRUE THEN COALESCE(vc.upVotes, 0) END DESC NULLS LAST,
//                     c.created_at DESC
//            LIMIT :size OFFSET :offset""")
//    List<CommentResponse> searchCommentsByLinkedin(@Bind String query, @Bind UUID userId, @Bind int size, @Bind int offset, @Bind boolean sortByUpvotes);
//
//
//    @SqlQuery("SELECT count(*) FROM comments c where c.target_user_id in (select id from users where linkedin_url = :url) and c.deleted_at is null")
//    int countCommentsByLinkedin(@Bind String url);
//
//
//    @SqlUpdate("INSERT INTO comment_votes (comment_id, user_id, upvote) VALUES (:commentId, :userId, :upvote)")
//    @GetGeneratedKeys
//    UUID createCommentRating(
//            @Bind UUID commentId,
//            @Bind UUID userId,
//            @Bind boolean upvote
//    );
//
//    @SqlQuery("SELECT * FROM comment_votes WHERE user_id = :userId and comment_id = :commentId")
//    Optional<CommentRating> getCommentRating(@Bind UUID userId, @Bind UUID commentId);
//
//    // delete rating
//    @SqlUpdate("DELETE FROM comment_votes WHERE user_id = :userId and comment_id = :commentId")
//    void deleteCommentRating(@Bind UUID userId, @Bind UUID commentId);
//
//    @SqlUpdate("DELETE FROM comment_votes WHERE comment_id = :id")
//    void deleteVotes(@Bind UUID id);
//
//    @SqlUpdate("INSERT INTO comment_flags (comment_id, user_id, reason) VALUES (:commentId, :userId, :reason)" +
//            "on conflict (comment_id, user_id) do update set reason = :reason, updated_at = now()")
//    void flagComment(@Bind UUID userId, @Bind UUID commentId, @Bind String reason);
//
//    // Join and find flagged comments order by created desc.
//    @SqlQuery("SELECT cf.created_at as flagged_at, c.id, c.comment_text, u.linkedin_url FROM comments c " +
//            "JOIN comment_flags cf on c.id = cf.comment_id " +
//            "JOIN users u on c.target_user_id = u.id " +
//            "WHERE c.deleted_at is null " +
//            "ORDER BY cf.created_at DESC LIMIT :size OFFSET :offset")
//    List<FlaggedComment> getFlaggedComments(@Bind int size, @Bind int offset);
//
//
//    @SqlQuery("SELECT count(*) FROM comments c where c.author_id = :userId and c.updated_at > now() - interval '1 day'")
//    @SingleValue
//    int countCommentUpdatesInLastDay(@Bind UUID userId);
//
//    // Count comments in last amount of time based on time ms passed in
//    @SqlQuery("SELECT count(*) FROM comments c where c.deleted_at is null and c.created_at > now() - :daysAgo * '1 day'::interval")
//    @SingleValue
//    int countComments(@Bind long daysAgo);
//
//    // Get last 'numberComments' of comments by created at
//    @SqlQuery("SELECT * FROM comments c where c.deleted_at is null and c.created_at > now() - :daysAgo * '1 day'::interval ORDER BY c.created_at DESC LIMIT :limit")
//    List<Comment> getLastComments(@Bind int limit, @Bind int daysAgo);
//
//    // Create comment request
//    @SqlUpdate("INSERT INTO comment_requests (requester_id, linkedin_url) VALUES (:requesterId, :linkedinUrl)")
//    @GetGeneratedKeys
//    UUID createCommentRequest(@Bind UUID requesterId, @Bind String linkedinUrl);
//
//
//    // Get comment requests by user id in last 24 hours
//    @SqlQuery("SELECT count(*) FROM comment_requests cr where cr.requester_id = :requesterId and cr.created_at > now() - interval '1 day'")
//    int countCommentRequestsInLastDay(UUID requesterId);
//}
