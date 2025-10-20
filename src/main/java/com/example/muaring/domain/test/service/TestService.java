package com.example.muaring.domain.test.service;

import com.example.muaring.common.response.CommonErrorCode;
import com.example.muaring.common.response.ErrorCode;
import com.example.muaring.domain.test.dto.TestRequestDTO;
import com.example.muaring.domain.test.dto.TestResponseDTO;
import com.example.muaring.domain.test.entity.Test;
import com.example.muaring.domain.test.exception.TestException;
import com.example.muaring.domain.test.mapper.TestMapper;
import com.example.muaring.domain.test.repository.TestRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class TestService {

    private final TestRepository testRepository;
    private final TestMapper testMapper;

    // ⚪ 전체 조회
    public List<TestResponseDTO> getAllTests() {
        List<Test> tests = testRepository.findAll();
        return testMapper.toResponseDTO(tests);
    }

    // ⚪ 단일 조회
    public TestResponseDTO getTestById(Long testSeq) {
        Test test = testRepository.findById(testSeq)
                .orElseThrow(() -> new TestException(CommonErrorCode.NOT_FOUND_TEST));
        return testMapper.toResponseDTO(test);
    }

    // ⚪ 등록
    @Transactional
    public TestResponseDTO createTest(@Valid TestRequestDTO testRequestDTO) {
        Test test = testMapper.toEntity(testRequestDTO);
        Test savedTest = testRepository.save(test);
        return testMapper.toResponseDTO(savedTest);
    }

    // ⚪ 수정
    @Transactional
    public TestResponseDTO updateTest(Long testSeq, @Valid TestRequestDTO testRequestDTO) {
        Test test = testRepository.findById(testSeq)
                .orElseThrow(() -> new TestException(CommonErrorCode.NOT_FOUND_TEST));

        test.updateContent(testRequestDTO.content());
        return testMapper.toResponseDTO(test);
    }

    // ⚪ 삭제
    @Transactional
    public void deleteTest(Long testSeq) {
        Test test = testRepository.findById(testSeq)
                .orElseThrow(() -> new TestException(CommonErrorCode.NOT_FOUND_TEST));
        testRepository.delete(test);
    }
}