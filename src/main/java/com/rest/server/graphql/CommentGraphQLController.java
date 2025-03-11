package com.rest.server.graphql;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.rest.server.models.Comment;
import com.rest.server.services.CommentService;
import com.rest.server.services.PostService;
import com.rest.server.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Controller
public class CommentGraphQLController {

    @Autowired
    private CommentService commentService;

    @Autowired
    private UserService userService;

    @Autowired
    private PostService postService;

    // Query methods
    @QueryMapping
    public Map<String, Object> comments(
            @Argument int page,
            @Argument int limit,
            @Argument String sortBy) {

        Page<Comment> commentsPage = commentService.allComments(
                PageRequest.of(page - 1, limit, Sort.by(sortBy).descending()));

        Map<String, Object> result = new HashMap<>();
        result.put("data", commentsPage.getContent());
        result.put("total", commentsPage.getTotalElements());
        result.put("page", page);
        result.put("limit", limit);

        return result;
    }

    @QueryMapping
    public Comment comment(@Argument String id) {
        return commentService.singleComment(id).orElse(null);
    }

    @QueryMapping
    public Map<String, Object> commentsByPost(
            @Argument String postId,
            @Argument int page,
            @Argument int limit,
            @Argument String sortBy) {

        Page<Comment> commentsPage = commentService.findCommentsByPostId(
                postId, page - 1, limit);

        Map<String, Object> result = new HashMap<>();
        result.put("data", commentsPage.getContent());
        result.put("total", commentsPage.getTotalElements());
        result.put("page", page);
        result.put("limit", limit);

        return result;
    }

    @QueryMapping
    public Map<String, Object> commentsByUser(
            @Argument String userId,
            @Argument int page,
            @Argument int limit,
            @Argument String sortBy) {

        Page<Comment> commentsPage = commentService.findCommentsByUserId(
                userId, page - 1, limit);

        Map<String, Object> result = new HashMap<>();
        result.put("data", commentsPage.getContent());
        result.put("total", commentsPage.getTotalElements());
        result.put("page", page);
        result.put("limit", limit);

        return result;
    }

    // Mutation methods
    @MutationMapping
    public Comment createComment(@Argument CommentCreateInput input) {
        Comment comment = new Comment();
        comment.setCommentMessage(input.getCommentMessage());

        // Assign IDs directly as Strings
        comment.setCommentOwnerId(input.getCommentOwner());
        comment.setCommentPostId(input.getCommentPost());

        // Set other fields (e.g., commentPublishDate)
        comment.setCommentPublishDate(OffsetDateTime.now(ZoneOffset.UTC)
                .format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));

        return commentService.createComment(comment);
    }

    @MutationMapping
    public Comment updateComment(
            @Argument String id,
            @Argument CommentUpdateInput input) {

        Comment comment = commentService.singleComment(id)
                .orElseThrow(() -> new RuntimeException("Comment not found"));

        // Update fields
        if (input.getCommentMessage() != null) {
            comment.setCommentMessage(input.getCommentMessage());
        }

        // Only update owner ID if provided
        if (input.getCommentOwner() != null) {
            comment.setCommentOwnerId(input.getCommentOwner());
        }

        // Only update post ID if provided
        if (input.getCommentPost() != null) {
            comment.setCommentPostId(input.getCommentPost());
        }

        return commentService.updateComment(id, comment);
    }

    @MutationMapping
    public String deleteComment(@Argument String id) {
        commentService.deleteComment(id);
        return id;
    }

    // Field resolver for "commentOwner" returning ID as string
    @SchemaMapping(typeName = "Comment", field = "commentOwner")
    public String getCommentOwner(Comment comment) {
        return comment.getCommentOwnerId();
    }

    // Field resolver for "commentPost" returning ID as string
    @SchemaMapping(typeName = "Comment", field = "commentPost")
    public String getCommentPost(Comment comment) {
        return comment.getCommentPostId();
    }

    // Keep the "id" resolver
    @SchemaMapping(typeName = "Comment", field = "id")
    public String getId(Comment comment) {
        return comment.getCommentId();
    }

    // Input class for comment creation
    public static class CommentCreateInput {

        @JsonProperty("commentMessage")
        private String commentMessage;

        @JsonProperty("commentOwner")
        private String commentOwner;

        @JsonProperty("commentPost")
        private String commentPost;

        // Getters and setters
        public String getCommentMessage() { return commentMessage; }
        public void setCommentMessage(String commentMessage) { this.commentMessage = commentMessage; }

        public String getCommentOwner() { return commentOwner; }
        public void setCommentOwner(String commentOwner) { this.commentOwner = commentOwner; }

        public String getCommentPost() { return commentPost; }
        public void setCommentPost(String commentPost) { this.commentPost = commentPost; }
    }

    // Input class for comment updates
    public static class CommentUpdateInput {

        @JsonProperty("commentMessage")
        private String commentMessage;

        @JsonProperty("commentOwner")
        private String commentOwner;

        @JsonProperty("commentPost")
        private String commentPost;

        // Getters and setters
        public String getCommentMessage() { return commentMessage; }
        public void setCommentMessage(String commentMessage) { this.commentMessage = commentMessage; }

        public String getCommentOwner() { return commentOwner; }
        public void setCommentOwner(String commentOwner) { this.commentOwner = commentOwner; }

        public String getCommentPost() { return commentPost; }
        public void setCommentPost(String commentPost) { this.commentPost = commentPost; }
    }
}