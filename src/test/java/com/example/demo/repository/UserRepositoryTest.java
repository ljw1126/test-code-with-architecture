package com.example.demo.repository;

import com.example.demo.user.domain.UserStatus;
import com.example.demo.user.infrastructure.UserEntity;
import com.example.demo.user.infrastructure.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest(showSql = true)
@TestPropertySource("classpath:test-application.properties")
@Sql("/sql/user-repository-test-data.sql")
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void findByIdAndStatus로_유저데이터를_찾아올수있다() {
        Optional<UserEntity> result = userRepository.findByIdAndStatus(1L, UserStatus.PENDING);

        assertThat(result).isPresent();
    }

    @Test
    void findByIdAndStatus는_데이터가없으면_Optional_empty를반환한다() {
        Optional<UserEntity> result = userRepository.findByIdAndStatus(2, UserStatus.PENDING);

        assertThat(result).isEmpty();
    }

    @Test
    void findByEmailAndStatus로_유저데이터를_찾아올수있다() {
        Optional<UserEntity> result = userRepository.findByEmailAndStatus("leejinwoo1126@gmail.com", UserStatus.PENDING);

        assertThat(result).isPresent();
    }

    @Test
    void findByEmailAndStatus는_데이터가없으면_Optional_empty를반환한다() {
        Optional<UserEntity> result = userRepository.findByEmailAndStatus("leejinwoo1126@gmail.com", UserStatus.ACTIVE);

        assertThat(result).isEmpty();
    }
}
