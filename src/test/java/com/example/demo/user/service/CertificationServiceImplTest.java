package com.example.demo.user.service;

import com.example.demo.mock.FakeMailSender;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class CertificationServiceImplTest {


    @Test
    void send를_통해_이메일과_컨텐츠가_만들어진다() {
        FakeMailSender mailSender = new FakeMailSender();
        CertificationServiceImpl certificationServiceImpl = new CertificationServiceImpl(mailSender);

        certificationServiceImpl.send("tester@gmail.com", 1, "test-certification-code");

        Assertions.assertThat(mailSender.email).isEqualTo("tester@gmail.com");
        Assertions.assertThat(mailSender.title).isEqualTo("Please certify your email address");
        Assertions.assertThat(mailSender.content).isEqualTo("Please click the following link to certify your email address: http://localhost:8080/api/users/1/verify?certificationCode=test-certification-code");
    }
}
