package com.example.demo.post.controller;

import com.example.demo.mock.TestContainer;
import com.example.demo.post.controller.response.PostResponse;
import com.example.demo.post.domain.Post;
import com.example.demo.post.domain.PostUpdate;
import com.example.demo.user.domain.User;
import com.example.demo.user.domain.UserStatus;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


class PostControllerTest {

    @Test
    void getById로_포스트_한건_조회할수있다() {
        TestContainer testContainer = TestContainer.builder()
                .uuidHolder(() -> "test-uuid")
                .clockHolder(() -> 100L)
                .build();
        User user = User.builder()
                .id(1L)
                .email("tester@gmail.com")
                .nickname("tester")
                .address("Busan")
                .status(UserStatus.ACTIVE)
                .certificationCode("certificate-code")
                .build();
        testContainer.userRepository.save(user);
        testContainer.postRepository.save(Post.builder()
                        .writer(user)
                        .content("신규 포스트")
                .build());


        PostController postController = testContainer.postController;
        ResponseEntity<PostResponse> result = postController.getById(1L);
        PostResponse body = result.getBody();

        assertThat(result.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(200));
        assertThat(body).isNotNull();
        assertThat(body.getId()).isEqualTo(1L);
        assertThat(body.getContent()).isEqualTo("신규 포스트");
        assertThat(body.getWriter().getId()).isEqualTo(1L);
        assertThat(body.getWriter().getNickname()).isEqualTo("tester");
    }

    @Test
    void update로_포스트_한건_수정할수있다() {
        TestContainer testContainer = TestContainer.builder()
                .uuidHolder(() -> "test-uuid")
                .clockHolder(() -> 100L)
                .build();
        User user = User.builder()
                .id(1L)
                .email("tester@gmail.com")
                .nickname("tester")
                .address("Busan")
                .status(UserStatus.ACTIVE)
                .certificationCode("certificate-code")
                .build();
        testContainer.userRepository.save(user);
        testContainer.postRepository.save(Post.builder()
                .writer(user)
                .content("신규 포스트")
                .build());

        PostUpdate postUpdate = PostUpdate.builder()
                .content("내용수정")
                .build();

        PostController postController = testContainer.postController;
        ResponseEntity<PostResponse> result = postController.update(1L, postUpdate);
        PostResponse body = result.getBody();

        assertThat(result.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(200));
        assertThat(body.getId()).isEqualTo(1L);
        assertThat(body.getContent()).isEqualTo("내용수정");
        assertThat(body.getWriter().getId()).isEqualTo(1L);
        assertThat(body.getWriter().getNickname()).isEqualTo("tester");
    }
}
