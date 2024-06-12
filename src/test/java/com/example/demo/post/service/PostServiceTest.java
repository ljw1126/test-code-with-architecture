package com.example.demo.post.service;

import com.example.demo.common.domain.exception.ResourceNotFoundException;
import com.example.demo.common.service.port.ClockHolder;
import com.example.demo.common.service.port.UuidHolder;
import com.example.demo.mock.FakeMailSender;
import com.example.demo.mock.FakePostRepository;
import com.example.demo.mock.FakeUserRepository;
import com.example.demo.mock.TestClockHolder;
import com.example.demo.mock.TestUuidHolder;
import com.example.demo.post.domain.Post;
import com.example.demo.post.domain.PostCreate;
import com.example.demo.post.domain.PostUpdate;
import com.example.demo.post.service.port.PostRepository;
import com.example.demo.user.domain.User;
import com.example.demo.user.domain.UserStatus;
import com.example.demo.user.service.CertificationService;
import com.example.demo.user.service.UserService;
import com.example.demo.user.service.port.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PostServiceTest {

    private PostService postService;


    @BeforeEach
    void setUp() {
        UserRepository fakeUserRepository = new FakeUserRepository();
        PostRepository fakePostRepository = new FakePostRepository();
        ClockHolder testClockHolder = new TestClockHolder(100L);
        this.postService = PostService.builder()
                .userRepository(fakeUserRepository)
                .postRepository(fakePostRepository)
                .clockHolder(testClockHolder)
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

        Post post = Post.builder()
                .writer(user2)
                .content("내용없음")
                .createdAt(1678530673958L)
                .build();

        fakePostRepository.save(post);
    }

    @Test
    void findById() {
        Post post = postService.findById(1L);

        assertThat(post).isNotNull();
        assertThat(post.getContent()).isEqualTo("내용없음");
    }

    @Test
    void findById_데이터가_없는경우_예외를던진다() {
        assertThatThrownBy(() -> postService.findById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Posts에서 ID 99를 찾을 수 없습니다.");
    }

    @Test
    void create() {
        PostCreate postCreateDto = PostCreate.builder()
                .writerId(2L)
                .content("두번째 포스트")
                .build();

        Post result = postService.create(postCreateDto);

        assertThat(result).extracting("id", "content", "createdAt")
                .containsExactly(2L, "두번째 포스트", 100L);
    }

    @Test
    void create_ACTIVE_상태가_아닌유저가_작성할경우_예외를던진다() {
        PostCreate postCreate = PostCreate.builder()
                .writerId(1L)
                .content("두번째 포스트")
                .build();

        assertThatThrownBy(() -> postService.create(postCreate))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Users에서 ID 1를 찾을 수 없습니다.");
    }

    @Test
    void update() {
        long postId = 1L;
        PostUpdate updateDto = PostUpdate.builder()
                .content("내용수정")
                .build();

        Post updated = postService.update(postId, updateDto);
        assertThat(updated.getContent()).isEqualTo("내용수정");
        assertThat(updated.getModifiedAt()).isEqualTo(100L);
    }

    @Test
    void update할_포스트가_없으면_예외를_던진다() {
        long postId = 99L;
        PostUpdate updateDto = PostUpdate.builder()
                .content("내용수정")
                .build();

        assertThatThrownBy(() -> postService.update(postId, updateDto))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Posts에서 ID 99를 찾을 수 없습니다.");
    }
}
