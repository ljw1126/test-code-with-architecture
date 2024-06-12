package com.example.demo.post.controller;

import com.example.demo.mock.TestContainer;
import com.example.demo.post.controller.response.PostResponse;
import com.example.demo.post.domain.PostCreate;
import com.example.demo.user.domain.User;
import com.example.demo.user.domain.UserStatus;
import com.fasterxml.jackson.databind.ObjectMapper;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
class PostCreateControllerTest {

    @Test
    void createPost로_포스트를_추가_할수있다() {
        TestContainer testContainer = TestContainer.builder()
                .uuidHolder(() -> "test-uuid")
                .clockHolder(() -> 100L)
                .build();
        testContainer.userRepository.save(User.builder()
                .email("tester@gmail.com")
                .nickname("tester")
                .address("Busan")
                .status(UserStatus.ACTIVE)
                .certificationCode("certificate-code")
                .build());

        PostCreate postCreate = PostCreate.builder()
                .content("포스트 신규 추가")
                .writerId(1L)
                .build();


        PostCreateController postCreateController = testContainer.postCreateController;
        ResponseEntity<PostResponse> result = postCreateController.create(postCreate);
        PostResponse body = result.getBody();

        assertThat(result.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(201));
        assertThat(body.getId()).isEqualTo(1L);
        assertThat(body.getContent()).isEqualTo("포스트 신규 추가");
        assertThat(body.getWriter().getId()).isEqualTo(1L);
    }
}
