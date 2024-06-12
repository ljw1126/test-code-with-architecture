package com.example.demo.post.domain;

import com.example.demo.mock.TestClockHolder;
import com.example.demo.user.domain.User;
import com.example.demo.user.domain.UserStatus;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PostTest {

    @Test
    void PostCreate로_게시물을_만들수있다() {
        PostCreate postCreate = PostCreate.builder()
                .writerId(1)
                .content("hello world")
                .build();

        User writer = User.builder()
                .email("tester@gmail.com")
                .nickname("tester")
                .address("Busan")
                .status(UserStatus.ACTIVE)
                .certificationCode("certificate-code")
                .build();

        Post post = Post.of(postCreate, writer, new TestClockHolder(100L));

        assertThat(post.getContent()).isEqualTo("hello world");
        assertThat(post.getWriter().getEmail()).isEqualTo("tester@gmail.com");
        assertThat(post.getCreatedAt()).isEqualTo(100L);
    }
}
