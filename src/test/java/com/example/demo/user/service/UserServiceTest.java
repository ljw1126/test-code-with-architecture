package com.example.demo.user.service;

import com.example.demo.common.domain.exception.CertificationCodeNotMatchedException;
import com.example.demo.common.domain.exception.ResourceNotFoundException;
import com.example.demo.common.service.port.ClockHolder;
import com.example.demo.common.service.port.UuidHolder;
import com.example.demo.mock.FakeMailSender;
import com.example.demo.mock.FakeUserRepository;
import com.example.demo.mock.TestClockHolder;
import com.example.demo.mock.TestUuidHolder;
import com.example.demo.user.domain.User;
import com.example.demo.user.domain.UserCreate;
import com.example.demo.user.domain.UserStatus;
import com.example.demo.user.domain.UserUpdate;
import com.example.demo.user.service.port.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


class UserServiceTest {

    private UserService userService;

    @BeforeEach
    void setUp() {
        FakeMailSender fakeMailSender = new FakeMailSender();
        ClockHolder testClockHolder = new TestClockHolder(100L);
        UuidHolder testUuidHolder = new TestUuidHolder("test-uuid");
        UserRepository fakeUserRepository = new FakeUserRepository();

        this.userService = UserService.builder()
                .certificationService(new CertificationService(fakeMailSender))
                .uuidHolder(testUuidHolder)
                .clockHolder(testClockHolder)
                .userRepository(fakeUserRepository)
                .build();

        User user1 = User.builder()
                .email("leejinwoo1126@gmail.com")
                .nickname("ljw1126")
                .address("Busan")
                .status(UserStatus.PENDING)
                .certificationCode("test-code")
                .build();

        User user2 = User.builder()
                .email("wkrdmsdmffn@naver.com")
                .nickname("wkrdms")
                .address("Busan")
                .status(UserStatus.ACTIVE)
                .certificationCode("test-code2")
                .build();

        fakeUserRepository.save(user1);
        fakeUserRepository.save(user2);
    }

    @Test
    void getByEmail은_ACTIVE_상태인_유저를_찾아올수있다() {
        String email = "wkrdmsdmffn@naver.com";

        User result = userService.getByEmail(email);

        assertThat(result.getNickname()).isEqualTo("wkrdms");
    }

    @Test
    void getByEmail은_PENDING_상태인_유저를_찾아올없다() {
        String email = "leejinwoo1126@gmail.com";

        assertThatThrownBy(() -> userService.getByEmail(email))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void getById는_ACTIVE_상태인_유저를_찾아올수있다() {
        User result = userService.getById(2L);

        assertThat(result.getNickname()).isEqualTo("wkrdms");
    }

    @Test
    void getById는_PENDING_상태인_유저를_찾아올수없다() {
        assertThatThrownBy(() -> userService.getById(1L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void userCreateDto를_이용하여_유저생성_할수있다() {
        UserCreate userCreate = UserCreate.builder()
                .email("jeju1126@gmail.com")
                .address("Jeju")
                .nickname("jeju-jam")
                .build();

        User result = userService.create(userCreate);

        assertThat(result.getStatus()).isEqualTo(UserStatus.PENDING);
    }

    @Test
    void UserUpdateDto를_이용하여_유저수정_할수있다() {
        UserUpdate userUpdate = UserUpdate.builder()
                .address("Seoul")
                .nickname("updated-tester")
                .build();

        User result = userService.update(2L, userUpdate); // findById에서 ACTIVE 상태인 경우만 조회

        assertThat(result.getId()).isNotNull();
        assertThat(result.getAddress()).isEqualTo("Seoul");
        assertThat(result.getNickname()).isEqualTo("updated-tester");
    }

    @Test
    void user를_로그인_시키면_마지막_로그인시간이_변경된다() {
        userService.login(2L);

        User User = userService.getById(2L);
        assertThat(User.getLastLoginAt()).isEqualTo(100L);
    }

    @Test
    void PENDING_상태의_사용자는_인증코드로_ACTIVE_시킬수있다() {
        userService.verifyEmail(2L, "test-code2");

        User User = userService.getById(2L);
        assertThat(User.getStatus()).isEqualTo(UserStatus.ACTIVE);
    }

    @Test
    void PENDING_상태의_사용자는_잘못된_인증코드를_받으면_에러를_던진다() {
        assertThatThrownBy(() -> userService.verifyEmail(2L, "invalid-code"))
                .isInstanceOf(CertificationCodeNotMatchedException.class);
    }
}
