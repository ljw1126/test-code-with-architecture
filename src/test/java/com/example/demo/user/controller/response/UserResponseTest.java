package com.example.demo.user.controller.response;

import com.example.demo.user.domain.User;
import com.example.demo.user.domain.UserStatus;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UserResponseTest {
    @Test
    void User로_응답을_생성할수있다() {
        User user = User.builder()
                .id(1L)
                .email("tester@gmail.com")
                .nickname("tester")
                .address("Busan")
                .status(UserStatus.ACTIVE)
                .lastLoginAt(100L)
                .certificationCode("certificate-code")
                .build();

        UserResponse userResponse = UserResponse.from(user);

        assertThat(userResponse.getId()).isEqualTo(1L);
        assertThat(userResponse.getEmail()).isEqualTo("tester@gmail.com");
        assertThat(userResponse.getNickname()).isEqualTo("tester");
        assertThat(userResponse.getStatus()).isEqualTo(UserStatus.ACTIVE);
        assertThat(userResponse.getLastLoginAt()).isEqualTo(100L);
    }
}
