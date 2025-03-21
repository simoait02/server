package com.rest.server.graphql;

import com.rest.server.models.Post;
import com.rest.server.models.User;
import com.rest.server.models.UserDTO;
import com.rest.server.services.PostService;
import com.rest.server.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.util.BsonUtils;
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
import java.util.Optional;

@Controller
public class PostController {
    private static final Logger logger = LoggerFactory.getLogger(PostController.class);
    @Autowired
    private PostService postService;

    @Autowired
    private UserService userService;

    @SchemaMapping(typeName = "Post", field = "ownerId")
    public UserDTO owner(Post post) {
        UserDTO user = userService.findUserById(post.getPostOwnerId());
        if (user == null) {
            throw new RuntimeException("User not found");
        }
        if (user.getFirstName() == null || user.getLastName() == null) {
            throw new RuntimeException("User data is incomplete");
        }
        return user;
    }
    @QueryMapping
    public Map<String, Object> posts(
            @Argument Integer page,
            @Argument Integer limit,
            @Argument String sortBy) {

        int pageNum = page != null ? page : 1;
        int pageSize = limit != null ? limit : 10;
        String sortField = sortBy != null ? sortBy : "postPublishDate";

        Pageable pageable = PageRequest.of(pageNum - 1, pageSize, Sort.by(sortField));

        Page<Post> postsPage = postService.allPosts(pageable);

        Map<String, Object> response = new HashMap<>();
        response.put("data", postsPage.getContent());
        response.put("total", postsPage.getTotalElements());
        response.put("page", pageNum);
        response.put("limit", pageSize);

        return response;
    }

    @QueryMapping
    public Post post(@Argument String id) {
        Optional<Post> postOptional = postService.singlePost(id);
        return postOptional.orElse(null);
    }

    @QueryMapping
    public Map<String, Object> postsByUser(
            @Argument String userId,
            @Argument Integer page,
            @Argument Integer limit,
            @Argument String sortBy) {
        System.out.println("**********************************************************************************************");
        int pageNum = page != null ? page : 1;
        int pageSize = limit != null ? limit : 10;

        Page<Post> postsPage = postService.findPostsByUserId(userId, pageNum - 1, pageSize);

        Map<String, Object> response = new HashMap<>();
        response.put("data", postsPage.getContent());
        response.put("total", postsPage.getTotalElements());
        response.put("page", pageNum);
        response.put("limit", pageSize);

        return response;
    }

    @QueryMapping
    public Map<String, Object> postsByTag(
            @Argument String tag,
            @Argument Integer page,
            @Argument Integer limit,
            @Argument String sortBy) {

        int pageNum = page != null ? page : 1;
        int pageSize = limit != null ? limit : 10;

        Page<Post> postsPage = postService.findPostsByTag(tag, pageNum - 1, pageSize);

        Map<String, Object> response = new HashMap<>();
        response.put("data", postsPage.getContent());
        response.put("total", postsPage.getTotalElements());
        response.put("page", pageNum);
        response.put("limit", pageSize);

        return response;
    }

    @MutationMapping
    public Post createPost(@Argument("input") PostCreateInput input) {
        Post post = new Post();
        post.setPostText(input.getPostText());
        post.setPostImage(input.getPostImage());
        post.setPostTags(input.getPostTags());
        post.setPostOwnerId(input.getPostOwnerId());
        post.setPostPublishDate(OffsetDateTime.now(ZoneOffset.UTC)
                .format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));
        post.setPostLikes(0);

        Optional<User> userOpt = userService.singleUser(input.getPostOwnerId());
        if (userOpt.isEmpty()) {
            throw new IllegalArgumentException("User with ID " + input.getPostOwnerId() + " not found");
        }

        return postService.createPost(post);
    }

    @MutationMapping
    public Post updatePost(@Argument String id, @Argument("input") PostCreateInput input) {
        Post post = new Post();
        post.setPostText(input.getPostText());
        post.setPostImage(input.getPostImage());
        post.setPostTags(input.getPostTags());
        post.setPostOwnerId(input.getPostOwnerId());

        return postService.updatePost(id, post);
    }

    @MutationMapping
    public String deletePost(@Argument String id) {
        postService.deletePost(id);
        return id;
    }

// Resolver for User field in Post - This maps to the ownerId field defined in the schema

//    @SchemaMapping(typeName = "Post", field = "ownerId")
//    public User owner(Post post) {
//        // Fetch the user using the UserService
//        return userService.singleUser(post.getPostOwnerId()).orElse(null);
//    }
}

// Input class for Post creation/update
class PostCreateInput {
    private String postText;
    private String postImage;
    private java.util.List<String> postTags;
    private String postOwnerId;
    public String getPostText() {
        return postText;
    }

    public void setPostText(String postText) {
        this.postText = postText;
    }

    public String getPostImage() {
        return postImage;
    }

    public void setPostImage(String postImage) {
        this.postImage = postImage;
    }

    public java.util.List<String> getPostTags() {
        return postTags;
    }

    public void setPostTags(java.util.List<String> postTags) {
        this.postTags = postTags;
    }

    public String getPostOwnerId() {
        return postOwnerId;
    }

    public void setPostOwnerId(String postOwnerId) {
        this.postOwnerId = postOwnerId;
    }

}