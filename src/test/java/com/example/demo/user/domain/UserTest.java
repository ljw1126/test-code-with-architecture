package com.example.demo.user.domain;

import com.example.demo.common.domain.exception.CertificationCodeNotMatchedException;
import com.example.demo.mock.TestClockHolder;
import com.example.demo.mock.TestUuidHolder;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class UserTest {

    @Test
    void UserCreate_객체로_생성할수있다() {
        UserCreate userCreate = UserCreate.builder()
                .email("tester@gmail.com")
                .nickname("tester")
                .address("Busan")
                .build();

        User user = User.from(userCreate, new TestUuidHolder("test-uuid"));

        assertThat(user.getId()).isNull();
        assertThat(user.getEmail()).isEqualTo("tester@gmail.com");
        assertThat(user.getNickname()).isEqualTo("tester");
        assertThat(user.getAddress()).isEqualTo("Busan");
        assertThat(user.getStatus()).isEqualTo(UserStatus.PENDING);
        assertThat(user.getCertificationCode()).isEqualTo("test-uuid");
    }

    @Test
    void UserUpdate로_데이터를_수정할수있다() {
        User user = User.builder()
                .id(1L)
                .email("tester@gmail.com")
                .nickname("tester")
                .address("Busan")
                .status(UserStatus.ACTIVE)
                .lastLoginAt(100L)
                .certificationCode("certificate-code")
                .build();

        UserUpdate userUpdate = UserUpdate.builder()
                .nickname("updated-tester")
                .address("Seoul")
                .build();

        user = user.update(userUpdate);

        assertThat(user.getId()).isEqualTo(1L);
        assertThat(user.getEmail()).isEqualTo("tester@gmail.com");
        assertThat(user.getNickname()).isEqualTo("updated-tester");
        assertThat(user.getAddress()).isEqualTo("Seoul");
        assertThat(user.getStatus()).isEqualTo(UserStatus.ACTIVE);
        assertThat(user.getCertificationCode()).isEqualTo("certificate-code");
    }

    @Test
    void 로그인시_마지막_로그인시간이_변경된다() {
        User user = User.builder()
                .id(1L)
                .email("tester@gmail.com")
                .nickname("tester")
                .address("Busan")
                .status(UserStatus.ACTIVE)
                .lastLoginAt(100L)
                .certificationCode("certificate-code")
                .build();

        user = user.login(new TestClockHolder(99L));

        assertThat(user.getLastLoginAt()).isEqualTo(99L);
    }

    @Test
    void 유효한_인증코드로_계정을_활성화_할수있다() {
        User user = User.builder()
                .id(1L)
                .email("tester@gmail.com")
                .nickname("tester")
                .address("Busan")
                .status(UserStatus.PENDING)
                .lastLoginAt(100L)
                .certificationCode("certificate-code")
                .build();

        user = user.certificate("certificate-code");

        assertThat(user.getStatus()).isEqualTo(UserStatus.ACTIVE);
    }

    @Test
    void 잘못된_인증코드로_계정을_활성화_하려하면_에러를_던진다() {
        User user = User.builder()
                .id(1L)
                .email("tester@gmail.com")
                .nickname("tester")
                .address("Busan")
                .status(UserStatus.PENDING)
                .lastLoginAt(100L)
                .certificationCode("certificate-code")
                .build();

        assertThatThrownBy(() -> user.certificate("error-code"))
                .isInstanceOf(CertificationCodeNotMatchedException.class);
    }
}
