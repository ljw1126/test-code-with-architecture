package com.example.demo.medium;

import com.example.demo.common.domain.exception.CertificationCodeNotMatchedException;
import com.example.demo.common.domain.exception.ResourceNotFoundException;
import com.example.demo.user.domain.User;
import com.example.demo.user.domain.UserCreate;
import com.example.demo.user.domain.UserStatus;
import com.example.demo.user.domain.UserUpdate;
import com.example.demo.user.service.UserServiceImpl;
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

@SpringBootTest
@TestPropertySource("classpath:test-application.properties")
@SqlGroup({
        @Sql(value = "/sql/user-repository-test-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
        @Sql(value = "/sql/delete-all-data.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
})
class UserServiceImplTest {

    @Autowired
    private UserServiceImpl userServiceImpl;

    @MockBean
    private JavaMailSender javaMailSender;

    @Test
    void getByEmail은_ACTIVE_상태인_유저를_찾아올수있다() {
        String email = "wkrdmsdmffn@naver.com";

        User result = userServiceImpl.getByEmail(email);

        assertThat(result.getNickname()).isEqualTo("wkrdms");
    }

    @Test
    void getByEmail은_PENDING_상태인_유저를_찾아올없다() {
        String email = "leejinwoo1126@gmail.com";

        assertThatThrownBy(() -> userServiceImpl.getByEmail(email))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void getById는_ACTIVE_상태인_유저를_찾아올수있다() {
        User result = userServiceImpl.getById(2L);

        assertThat(result.getNickname()).isEqualTo("wkrdms");
    }

    @Test
    void getById는_PENDING_상태인_유저를_찾아올수없다() {
        assertThatThrownBy(() -> userServiceImpl.getById(1L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void userCreateDto를_이용하여_유저생성_할수있다() {
        UserCreate userCreate = UserCreate.builder()
                .email("jeju1126@gmail.com")
                .address("Jeju")
                .nickname("jeju-jam")
                .build();

        BDDMockito.doNothing().when(javaMailSender).send(new SimpleMailMessage());

        User result = userServiceImpl.create(userCreate);

        assertThat(result.getId()).isNotNull();
        assertThat(result.getStatus()).isEqualTo(UserStatus.PENDING);
    }

    @Test
    void UserUpdateDto를_이용하여_유저수정_할수있다() {
        UserUpdate userUpdate = UserUpdate.builder()
                .address("Seoul")
                .nickname("updated-ljw1126")
                .build();

        User result = userServiceImpl.update(2L, userUpdate); // findById에서 ACTIVE 상태인 경우만 조회

        assertThat(result.getId()).isNotNull();
        assertThat(result.getAddress()).isEqualTo("Seoul");
        assertThat(result.getNickname()).isEqualTo("updated-ljw1126");
    }

    @Test
    void user를_로그인_시키면_마지막_로그인시간이_변경된다() {
        userServiceImpl.login(2L);

        User User = userServiceImpl.getById(2L);
        assertThat(User.getLastLoginAt()).isGreaterThan(0L); // TODO
    }

    @Test
    void PENDING_상태의_사용자는_인증코드로_ACTIVE_시킬수있다() {
        userServiceImpl.verifyEmail(1L, "test-code");

        User User = userServiceImpl.getById(1L);
        assertThat(User.getStatus()).isEqualTo(UserStatus.ACTIVE);
    }

    @Test
    void PENDING_상태의_사용자는_잘못된_인증코드를_받으면_에러를_던진다() {
        assertThatThrownBy(() -> userServiceImpl.verifyEmail(1L, "invalid-code"))
                .isInstanceOf(CertificationCodeNotMatchedException.class);
    }
}
