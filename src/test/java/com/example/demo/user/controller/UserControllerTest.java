package com.example.demo.user.controller;

import com.example.demo.user.domain.UserStatus;
import com.example.demo.user.domain.UserUpdate;
import com.example.demo.user.infrastructure.UserEntity;
import com.example.demo.user.infrastructure.UserJpaRepository;
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

import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
@SqlGroup({
        @Sql(value = "/sql/user-repository-test-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
        @Sql(value = "/sql/delete-all-data.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
})
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserJpaRepository userJpaRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void 사용자는_특정유저의_주소제외한_정보를_전달_받을수있다() throws Exception {
        mockMvc.perform(get("/api/users/2")) // ACTIVE 유저만 호출 가능
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(2))
                .andExpect(jsonPath("$.email").value("wkrdmsdmffn@naver.com"))
                .andExpect(jsonPath("$.address").doesNotExist());
    }

    @Test
    void 사용자는_존재하지않는_유저아이디로_호출할경우_404응답을받는다() throws Exception {
        mockMvc.perform(get("/api/users/99")) // ACTIVE 유저만 호출 가능
                .andExpect(status().isNotFound())
                .andExpect(content().string("Users에서 ID 99를 찾을 수 없습니다."));
    }

    @Test
    void 사용자는_인증코드로_계정을_활성화시킬수있다() throws Exception {
        mockMvc.perform(get("/api/users/1/verify").queryParam("certificationCode", "test-code"))
                .andExpect(status().isFound());

        UserEntity userEntity = userJpaRepository.findById(2L).get();
        assertThat(userEntity.getStatus()).isEqualTo(UserStatus.ACTIVE);
    }

    @Test
    void 사용자_인증코드가_틀린경우_403을_반환한다() throws Exception {
        mockMvc.perform(get("/api/users/1/verify").queryParam("certificationCode", "invalid-code"))
                .andExpect(status().isForbidden());
    }

    @Test
    void 사용자는_내정보를_불러올때_개인정보인_주소도_갖고올수있다() throws Exception {
        mockMvc.perform(get("/api/users/me").header("EMAIL", "wkrdmsdmffn@naver.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("2"))
                .andExpect(jsonPath("$.email").value("wkrdmsdmffn@naver.com"))
                .andExpect(jsonPath("$.nickname").value("wkrdms"))
                .andExpect(jsonPath("$.address").value("Busan"));
    }

    @Test
    void 사용자는_내정보를_수정할수있다() throws Exception {
        UserUpdate userUpdate = UserUpdate.builder()
                .nickname("tester")
                .address("test-city")
                .build();

        mockMvc.perform(put("/api/users/me")
                .header("EMAIL", "wkrdmsdmffn@naver.com")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userUpdate)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("2"))
                .andExpect(jsonPath("$.email").value("wkrdmsdmffn@naver.com"))
                .andExpect(jsonPath("$.nickname").value("tester"))
                .andExpect(jsonPath("$.address").value("test-city"));
    }
}
