package com.jixialunbi.controllers;

import com.jixialunbi.common.R;
import com.jixialunbi.common.result.ResultCode;
import com.jixialunbi.dto.request.UserRequest;
import com.jixialunbi.dto.request.UserUpdateRequest;
import com.jixialunbi.model.User;
import com.jixialunbi.repository.UserRepository;
import com.jixialunbi.security.UserDetailsImpl;
import com.jixialunbi.service.FollowUserService;
import com.jixialunbi.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@RestController
@RequestMapping("/api/v1")
public class UserController {

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserService userService;

    @Autowired
    FollowUserService followUserService;

    @GetMapping("/users")
    public R fetchUsers(@RequestParam int page, @RequestParam int pageSize) {
        Pageable pageable = PageRequest.of(page, pageSize);
        Page<User> data = userRepository.findAll(pageable);
        if (data.getSize() <= 0) {
            return R.ok().data(new ArrayList());
        }
        return R.ok().data(data);
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("/login-user-info")
    public R loginUserInfo(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return R.ok().data(userRepository.findById(userDetails.getId()));
    }

    @PostMapping("/user/create")
    public R create(@Valid @RequestBody UserRequest request) {
        if (userRepository.existsByEmailIgnoreCase(request.getEmail())) {
            return R.error(ResultCode.PARAM_ERROR).message("用户已存在！");
        }
        return R.ok().data(userService.create(request));
    }

    @GetMapping("/user-info/{id}")
    public R getUserInfo(@PathVariable Long id, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        if (userDetails == null) {
            var user = userRepository.findById(id);
            return R.ok().data(user);
        }
        User user = userService.getById(id);
        boolean followed = followUserService.isFollow(userDetails.getId(), id);
        user.setFollowed(followed);
        return R.ok().data(user);
    }

    @PostMapping("/user/update")
    public R update(@Valid @RequestBody UserUpdateRequest request, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        var user = userService.getById(userDetails.getId());
        if (request.getImage() != null) {
            user.setImage(request.getImage());
        }
        if (request.getEmail() != null) {
            user.setEmail(request.getEmail());
        }
        if (request.getUsername() != null) {
            user.setUsername(request.getUsername());
        }
        if (request.getAbout() != null) {
            user.setAbout(request.getAbout());
        }
        userRepository.save(user);
        return R.ok().data(true);
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @PostMapping("/follow-user/{userId}")
    public R followUser(@PathVariable Long userId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        boolean res = followUserService.followUser(userDetails.getId(), userId);
        return R.ok().data(res);
    }

    @GetMapping("/recent-users")
    public R fetchRecentUsers() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<User> data = userRepository.findAll(pageable);
        if (data.getSize() <= 0) {
            return R.ok().data(new ArrayList());
        }
        return R.ok().data(data);
    }

    @GetMapping("/hot-users")
    public R fetchHotUsers(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        Pageable pageable = PageRequest.of(0, 50);
        Page<User> data = userRepository.findAll(pageable);
        if (data.getSize() <= 0) {
            return R.ok().data(new ArrayList());
        }
        if(userDetails != null){
            data.map(v->{
                boolean followed = followUserService.isFollow(userDetails.getId(), v.getId());
                v.setFollowed(followed);
                return v;
            });
        }
        return R.ok().data(data);
    }

    @GetMapping("/follow-users")
    public R fetchFollowUsers(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        if(userDetails!=null){
            var data = followUserService.fetchFollowUsers(userDetails.getId());
            return R.ok().data(data);
        }
        return R.ok().data(new ArrayList());
    }

}