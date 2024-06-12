package com.example.demo.medium;

import com.example.demo.post.domain.PostCreate;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
@SqlGroup({
    @Sql(value = "/sql/user-repository-test-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
    @Sql(value = "/sql/delete-all-data.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
})
class PostCreateControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createPost로_포스트를_추가_할수있다() throws Exception {
        PostCreate postCreate = PostCreate.builder()
                .content("포스트 신규 추가")
                .writerId(2L)
                .build();

        mockMvc.perform(post("/api/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(postCreate))
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("2"))
                .andExpect(jsonPath("$.content").value("포스트 신규 추가"))
                .andExpect(jsonPath("$.writer.id").value("2"))
                .andExpect(jsonPath("$.writer.email").value("wkrdmsdmffn@naver.com"))
                .andExpect(jsonPath("$.writer.nickname").value("wkrdms"))
                .andExpect(jsonPath("$.writer.status").value("ACTIVE"))
                .andExpect(jsonPath("$.writer.lastLoginAt").value("0"));
    }
}
