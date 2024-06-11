package com.example.demo.user.infrastructrue;

import com.example.demo.user.domain.UserStatus;
import com.example.demo.user.infrastructure.UserEntity;
import com.example.demo.user.infrastructure.UserJpaRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest(showSql = true)
@TestPropertySource("classpath:test-application.properties")
@SqlGroup({
        @Sql(value = "/sql/user-repository-test-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
        @Sql(value = "/sql/delete-all-data.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
})
class UserRepositoryTest {

    @Autowired
    private UserJpaRepository userJpaRepository;

    @Test
    void findByIdAndStatus로_유저데이터를_찾아올수있다() {
        Optional<UserEntity> result = userJpaRepository.findByIdAndStatus(1L, UserStatus.PENDING);

        assertThat(result).isPresent();
    }

    @Test
    void findByIdAndStatus는_데이터가없으면_Optional_empty를반환한다() {
        Optional<UserEntity> result = userJpaRepository.findByIdAndStatus(2, UserStatus.PENDING);

        assertThat(result).isEmpty();
    }

    @Test
    void findByEmailAndStatus로_유저데이터를_찾아올수있다() {
        Optional<UserEntity> result = userJpaRepository.findByEmailAndStatus("leejinwoo1126@gmail.com", UserStatus.PENDING);

        assertThat(result).isPresent();
    }

    @Test
    void findByEmailAndStatus는_데이터가없으면_Optional_empty를반환한다() {
        Optional<UserEntity> result = userJpaRepository.findByEmailAndStatus("leejinwoo1126@gmail.com", UserStatus.ACTIVE);

        assertThat(result).isEmpty();
    }
}
