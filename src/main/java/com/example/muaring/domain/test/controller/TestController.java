package com.example.muaring.domain.test.controller;

import com.example.muaring.common.response.ApiResponse;
import com.example.muaring.domain.test.service.TestService;
import com.example.muaring.domain.test.dto.TestRequestDTO;
import com.example.muaring.domain.test.dto.TestResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/test")
@RequiredArgsConstructor
@Tag(name = "0. Test API", description = "테스트용 API")
public class TestController {

    private final TestService testService;

    @GetMapping
    @Operation(summary = "테스트 데이터 전체 조회", description = "테스트 데이터 전체 조회입니다.")
    public ResponseEntity<ApiResponse<List<TestResponseDTO>>> getAllTests() {
        List<TestResponseDTO> tests = testService.getAllTests();
        return ResponseEntity.ok(
                ApiResponse.ok(tests, "전체 데이터가 성공적으로 조회되었습니다.")
        );
    }

    @GetMapping("/{testSeq}")
    @Operation(summary = "테스트 데이터 상세 조회", description = "테스트 데이터 상세 조회 입니다.")
    public ResponseEntity<ApiResponse<TestResponseDTO>> getTestById(@PathVariable Long testSeq) {
        TestResponseDTO test = testService.getTestById(testSeq);
        return ResponseEntity.ok(
                ApiResponse.ok(test, "데이터가 성공적으로 조회되었습니다.")
        );
    }

    @PostMapping
    @Operation(summary = "테스트 데이터 생성", description = "테스트 데이터 생성입니다.")
    public ResponseEntity<ApiResponse<TestResponseDTO>> createTest(
            @Valid @RequestBody TestRequestDTO testRequestDTO) {
        TestResponseDTO createdTest = testService.createTest(testRequestDTO);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.created(createdTest, "데이터가 성공적으로 생성되었습니다."));
    }

    @PutMapping("/{testSeq}")
    @Operation(summary = "테스트 데이터 수정", description = "테스트 데이터 수정입니다.")
    public ResponseEntity<ApiResponse<TestResponseDTO>> updateTest(
            @PathVariable Long testSeq,
            @Valid @RequestBody TestRequestDTO testRequestDTO) {
        TestResponseDTO updatedTest = testService.updateTest(testSeq, testRequestDTO);
        return ResponseEntity.ok(
                ApiResponse.ok(updatedTest, "데이터가 성공적으로 수정되었습니다.")
        );
    }

    @DeleteMapping("/{testSeq}")
    @Operation(summary = "테스트 데이터 삭제", description = "테스트 데이터 삭제입니다.")
    public ResponseEntity<ApiResponse<Void>> deleteTest(@PathVariable Long testSeq) {
        testService.deleteTest(testSeq);
        return ResponseEntity.ok(
                ApiResponse.ok("데이터가 성공적으로 삭제되었습니다.")
        );
    }
}