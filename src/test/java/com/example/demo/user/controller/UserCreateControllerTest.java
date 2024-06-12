package com.example.demo.user.controller;

import com.example.demo.mock.TestContainer;
import com.example.demo.user.controller.response.UserResponse;
import com.example.demo.user.domain.UserCreate;
import com.example.demo.user.domain.UserStatus;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


class UserCreateControllerTest {

    @Test
    void 사용자는_내정보를_수정할수있다() throws Exception {
        TestContainer testContainer = TestContainer.builder()
                .uuidHolder(() -> "test-uuid")
                .build();

        UserCreate userCreate = UserCreate.builder()
                .email("tester@gmail.com")
                .nickname("tester")
                .address("test-city")
                .build();

        UserCreateController userCreateController = testContainer.userCreateController;
        ResponseEntity<UserResponse> result = userCreateController.create(userCreate);
        UserResponse body = result.getBody();

        assertThat(result.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(201));
        assertThat(body.getId()).isEqualTo(1L);
        assertThat(body.getNickname()).isEqualTo("tester");
        assertThat(body.getStatus()).isEqualTo(UserStatus.PENDING);

    }
}
