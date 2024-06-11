package com.example.demo.post.service;

import com.example.demo.common.domain.exception.ResourceNotFoundException;
import com.example.demo.post.domain.Post;
import com.example.demo.post.domain.PostCreate;
import com.example.demo.post.domain.PostUpdate;
import com.example.demo.post.service.port.PostRepository;
import com.example.demo.user.domain.User;
import com.example.demo.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final UserService userService;

    @Transactional(readOnly = true)
    public Post findById(long id) {
        return postRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Posts", id));
    }

    @Transactional
    public Post create(PostCreate postCreate) {
        User user = userService.getById(postCreate.getWriterId());
        Post post = Post.of(postCreate, user);
        return postRepository.save(post);
    }

    @Transactional
    public Post update(long id, PostUpdate postUpdate) {
        Post post = findById(id);
        post = post.update(postUpdate);
        return postRepository.save(post);
    }
}
