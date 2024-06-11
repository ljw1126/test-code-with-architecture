package com.example.demo.post.service;

import com.example.demo.common.domain.exception.ResourceNotFoundException;
import com.example.demo.post.domain.PostCreate;
import com.example.demo.post.domain.PostUpdate;
import com.example.demo.post.service.PostService;
import com.example.demo.post.infrastructure.PostEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@TestPropertySource("classpath:test-application.properties")
@SqlGroup({
        @Sql(value = "/sql/user-repository-test-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
        @Sql(value = "/sql/delete-all-data.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
})
class PostServiceTest {

    @Autowired
    private PostService postService;

    @Test
    void findById() {
        PostEntity postEntity = postService.findById(1L);

        assertThat(postEntity).isNotNull();
        assertThat(postEntity.getContent()).isEqualTo("내용없음");
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

        PostEntity result = postService.create(postCreateDto);

        assertThat(result).extracting("id", "content")
                .containsExactly(2L, "두번째 포스트");
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

        PostEntity updated = postService.update(postId, updateDto);
        assertThat(updated.getContent()).isEqualTo("내용수정");
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
