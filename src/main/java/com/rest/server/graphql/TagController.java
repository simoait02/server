package com.rest.server.graphql;

import com.rest.server.models.Tag;
import com.rest.server.services.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.Optional;

@Controller
public class TagController {

    private final TagService tagService;

    @Autowired
    public TagController(TagService tagService) {
        this.tagService = tagService;
    }

    @QueryMapping
    public List<Tag> tags(@Argument Integer page, @Argument Integer limit) {
        int pageNum = page != null ? page : 0;
        int pageSize = limit != null ? limit : 10;

        Pageable pageable = PageRequest.of(pageNum, pageSize);
        Page<Tag> tagsPage = tagService.allTags(pageable);

        return tagsPage.getContent();
    }

    @QueryMapping
    public Tag tag(@Argument String id) {
        Optional<Tag> tag = tagService.singleTag(id);
        return tag.orElse(null);
    }

    @MutationMapping
    public Tag createTag(@Argument Tag input) {
        return tagService.createTag(input);
    }

    @MutationMapping
    public Tag updateTag(@Argument String id, @Argument Tag input) {
        return tagService.updateTag(id, input);
    }

    @MutationMapping
    public String deleteTag(@Argument String id) {
        tagService.deleteTag(id);
        return id;
    }
}