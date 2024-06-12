package com.example.demo.user.infrastructure;

import com.example.demo.common.domain.exception.ResourceNotFoundException;
import com.example.demo.user.domain.User;
import com.example.demo.user.domain.UserStatus;
import com.example.demo.user.service.port.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {

    private final UserJpaRepository userJpaRepository;

    @Override
    public User getById(long writerId) {
        return findByIdAndStatus(writerId, UserStatus.ACTIVE)
                .orElseThrow(() -> new ResourceNotFoundException("Users", writerId));
    }

    @Override
    public User save(User user) {
        UserEntity saved = userJpaRepository.save(UserEntity.from(user)); // 도메인은 인프라 레이어 정보를 모르는게 좋다
        return saved.toUser();
    }

    @Override
    public Optional<User> findById(long id) {
        return userJpaRepository.findById(id).map(UserEntity::toUser);
    }

    @Override
    public Optional<User> findByIdAndStatus(long id, UserStatus userStatus) {
        return userJpaRepository.findByIdAndStatus(id, userStatus).map(UserEntity::toUser);
    }

    @Override
    public Optional<User> findByEmailAndStatus(String email, UserStatus userStatus) {
        return userJpaRepository.findByEmailAndStatus(email, userStatus).map(UserEntity::toUser);
    }
}
