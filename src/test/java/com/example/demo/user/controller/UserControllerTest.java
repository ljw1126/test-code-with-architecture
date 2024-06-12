package com.example.demo.user.controller;

import com.example.demo.common.domain.exception.CertificationCodeNotMatchedException;
import com.example.demo.common.domain.exception.ResourceNotFoundException;
import com.example.demo.mock.TestClockHolder;
import com.example.demo.mock.TestContainer;
import com.example.demo.user.controller.port.UserReadService;
import com.example.demo.user.controller.response.MyProfileResponse;
import com.example.demo.user.controller.response.UserResponse;
import com.example.demo.user.domain.User;
import com.example.demo.user.domain.UserStatus;
import com.example.demo.user.domain.UserUpdate;
import com.example.demo.user.infrastructure.UserEntity;
import com.example.demo.user.infrastructure.UserJpaRepository;
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
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


class UserControllerTest {

    @Test
    void 사용자는_특정유저의_주소제외한_정보를_전달_받을수있다() {
        TestContainer testContainer = TestContainer.builder().build();
        testContainer.userRepository.save(User.builder()
                .email("tester@gmail.com")
                .nickname("tester")
                .address("Busan")
                .status(UserStatus.ACTIVE)
                .lastLoginAt(100L)
                .certificationCode("certificate-code")
                .build());

        UserController userController = testContainer.userController;

        ResponseEntity<UserResponse> result = userController.getById(1); // ACTIVE 상태만 조회
        UserResponse body = result.getBody();

        assertThat(result.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(200));
        assertThat(body).isNotNull();
        assertThat(body.getEmail()).isEqualTo("tester@gmail.com");
        assertThat(body.getNickname()).isEqualTo("tester");
        assertThat(body.getStatus()).isEqualTo(UserStatus.ACTIVE);
    }

    @Test
    void 사용자는_존재하지않는_유저아이디로_호출할경우_404응답을받는다() {
        TestContainer testContainer = TestContainer.builder().build();
        UserController userController = testContainer.userController;

        assertThatThrownBy(() -> userController.getById(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void 사용자는_인증코드로_계정을_활성화시킬수있다() {
        TestContainer testContainer = TestContainer.builder().build();
        testContainer.userRepository.save(User.builder()
                .email("tester@gmail.com")
                .nickname("tester")
                .address("Busan")
                .status(UserStatus.PENDING)
                .lastLoginAt(100L)
                .certificationCode("certificate-code")
                .build());
        UserController userController = testContainer.userController;

        ResponseEntity<Void> result = userController.verify(1L, "certificate-code");
        User user = testContainer.userRepository.findById(1L).get();

        assertThat(result.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(302));
        assertThat(user.getStatus()).isEqualTo(UserStatus.ACTIVE);
    }

    @Test
    void 사용자_인증코드가_틀린경우_403을_반환한다() {
        TestContainer testContainer = TestContainer.builder().build();
        testContainer.userRepository.save(User.builder()
                .email("tester@gmail.com")
                .nickname("tester")
                .address("Busan")
                .status(UserStatus.PENDING)
                .lastLoginAt(100L)
                .certificationCode("certificate-code")
                .build());
        UserController userController = testContainer.userController;

        assertThatThrownBy(() -> userController.verify(1L, "invalid-code"))
                .isInstanceOf(CertificationCodeNotMatchedException.class);
    }

    @Test
    void 사용자는_내정보를_불러올때_개인정보인_주소도_갖고올수있다() {
        TestContainer testContainer = TestContainer.builder()
                .clockHolder(() -> 100L)
                .build();
        testContainer.userRepository.save(User.builder()
                .email("tester@gmail.com")
                .nickname("tester")
                .address("Busan")
                .status(UserStatus.ACTIVE)
                .certificationCode("certificate-code")
                .build());

        UserController userController = testContainer.userController;
        ResponseEntity<MyProfileResponse> result = userController.getMyInfo("tester@gmail.com"); // ACTIVE 상태만 조회가능
        MyProfileResponse body = result.getBody();

        assertThat(result.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(200));
        assertThat(body.getId()).isEqualTo(1L);
        assertThat(body.getNickname()).isEqualTo("tester");
        assertThat(body.getAddress()).isEqualTo("Busan");
        assertThat(body.getStatus()).isEqualTo(UserStatus.ACTIVE);
        assertThat(body.getLastLoginAt()).isEqualTo(100L);
    }

    @Test
    void 사용자는_내정보를_수정할수있다() throws Exception {
        TestContainer testContainer = TestContainer.builder()
                .build();
        testContainer.userRepository.save(User.builder()
                .email("tester@gmail.com")
                .nickname("tester")
                .address("Busan")
                .status(UserStatus.ACTIVE)
                .certificationCode("certificate-code")
                .build());

        UserUpdate userUpdate = UserUpdate.builder()
                .nickname("tester2")
                .address("test-city")
                .build();

        UserController userController = testContainer.userController;
        ResponseEntity<MyProfileResponse> result = userController.updateMyInfo("tester@gmail.com", userUpdate);
        MyProfileResponse body = result.getBody();

        assertThat(result.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(200));
        assertThat(body.getId()).isEqualTo(1L);
        assertThat(body.getNickname()).isEqualTo("tester2");
        assertThat(body.getAddress()).isEqualTo("test-city");
        assertThat(body.getStatus()).isEqualTo(UserStatus.ACTIVE);
        assertThat(body.getLastLoginAt()).isNull();
    }
}
