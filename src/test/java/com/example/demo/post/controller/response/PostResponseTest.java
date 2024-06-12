package com.example.demo.post.controller.response;

import com.example.demo.post.domain.Post;
import com.example.demo.user.domain.User;
import com.example.demo.user.domain.UserStatus;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PostResponseTest {

    @Test
    void Post로_응답을_생성할수있다() {
        Post post = Post.builder()
                .content("hello world")
                .writer(User.builder()
                        .email("tester@gmail.com")
                        .nickname("tester")
                        .address("Busan")
                        .status(UserStatus.ACTIVE)
                        .certificationCode("certificate-code")
                        .build())
                .build();

        PostResponse postResponse = PostResponse.from(post);

        assertThat(postResponse.getId()).isNull();
        assertThat(postResponse.getContent()).isEqualTo("hello world");
        assertThat(postResponse.getWriter().getId()).isNull();
        assertThat(postResponse.getWriter().getNickname()).isEqualTo("tester");
        assertThat(postResponse.getCreatedAt()).isNull();
        assertThat(postResponse.getModifiedAt()).isNull();
    }
}
