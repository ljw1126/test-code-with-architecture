package com.example.demo.medium;

import com.example.demo.post.domain.PostUpdate;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
@SqlGroup({
    @Sql(value = "/sql/user-repository-test-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
    @Sql(value = "/sql/delete-all-data.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
})
class PostControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getById로_포스트_한건_조회할수있다() throws Exception {
        mockMvc.perform(get("/api/posts/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.content").value("내용없음"))
                .andExpect(jsonPath("$.createdAt").value("1678530673958"))
                .andExpect(jsonPath("$.modifiedAt").value("0"))
                .andExpect(jsonPath("$.writer.id").value("2"))
                .andExpect(jsonPath("$.writer.email").value("wkrdmsdmffn@naver.com"))
                .andExpect(jsonPath("$.writer.nickname").value("wkrdms"))
                .andExpect(jsonPath("$.writer.status").value("ACTIVE"))
                .andExpect(jsonPath("$.writer.lastLoginAt").value("0"));
    }

    @Test
    void update로_포스트_한건_수정할수있다() throws Exception {
        PostUpdate postUpdate = PostUpdate.builder()
                .content("내용수정")
                .build();


        mockMvc.perform(put("/api/posts/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(postUpdate))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.content").value("내용수정"))
                .andExpect(jsonPath("$.createdAt").value("1678530673958"))
                .andExpect(jsonPath("$.writer.id").value("2"))
                .andExpect(jsonPath("$.writer.email").value("wkrdmsdmffn@naver.com"))
                .andExpect(jsonPath("$.writer.nickname").value("wkrdms"))
                .andExpect(jsonPath("$.writer.status").value("ACTIVE"))
                .andExpect(jsonPath("$.writer.lastLoginAt").value("0"));
    }
}
