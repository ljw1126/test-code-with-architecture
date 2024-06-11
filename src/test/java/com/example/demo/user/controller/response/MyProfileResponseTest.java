package com.example.demo.user.controller.response;

import com.example.demo.user.domain.User;
import com.example.demo.user.domain.UserStatus;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MyProfileResponseTest {

    @Test
    void User로_응답을_생성할수있다() {
        User user = User.builder()
                .id(1L)
                .email("tester@gmail.com")
                .nickname("tester")
                .address("Busan")
                .status(UserStatus.ACTIVE)
                .certificationCode("certificate-code")
                .build();

        MyProfileResponse myProfileResponse = MyProfileResponse.from(user);

        assertThat(myProfileResponse.getId()).isEqualTo(1L);
        assertThat(myProfileResponse.getEmail()).isEqualTo("tester@gmail.com");
        assertThat(myProfileResponse.getNickname()).isEqualTo("tester");
        assertThat(myProfileResponse.getAddress()).isEqualTo("Busan");
        assertThat(myProfileResponse.getStatus()).isEqualTo(UserStatus.ACTIVE);
    }
}
