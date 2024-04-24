package com.jixialunbi.controllers;

import com.jixialunbi.common.R;
import com.jixialunbi.dto.request.IdRequest;
import com.jixialunbi.dto.request.TagRequest;
import com.jixialunbi.model.Tag;
import com.jixialunbi.repository.TagRepository;
import com.jixialunbi.service.UserService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1")
public class TagController {

    @Autowired
    TagRepository tagRepository;

    @Autowired
    UserService userService;

    @GetMapping("/tags")
    public R fetchTags(@RequestParam(required = false, defaultValue = "1") int page, @RequestParam(required = false, defaultValue = "10") int pageSize) {
        return R.ok().data(tagRepository.findAll());
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @PostMapping("/tags/create")
    public R createTag(@Valid @RequestBody TagRequest tagRequest) {
        var tag = new Tag();
        tag.setName(tagRequest.getName().trim());
        tag.setDescription(tagRequest.getDescription().trim());
//        tag.setOrder(tagRequest.getOrder());
        return R.ok().data(tagRepository.save(tag));
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @PostMapping("/tags/update")
    public R updateTag(@Valid @RequestBody TagRequest tagRequest) {
        Tag tag = tagRepository.findById(tagRequest.getId()).get();
        if (tag != null) {
            tag.setName(tagRequest.getName().trim());
            tag.setDescription(tagRequest.getDescription().trim());
        }
        return R.ok().data(tagRepository.save(tag));
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @PostMapping("/delete-tag")
    @Transactional
    public R deleteTags(@Valid @RequestBody IdRequest idRequest) {
        tagRepository.deleteById(idRequest.getId());
        return R.ok().data(true);
    }

}