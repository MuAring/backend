package com.example.muaring.domain.test.mapper;

import com.example.muaring.domain.test.dto.TestRequestDTO;
import com.example.muaring.domain.test.dto.TestResponseDTO;
import com.example.muaring.domain.test.entity.Test;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import java.util.List;

// ✨ Test 엔티티와 DTO 간의 데이터 변환을 담당하는 MapStruct mapper 인터페이스
// ⚪ componentModel = "spring" 설정을 통해 Spring Bean으로 등록되게 한다.
// ⚪ 매핑되지 않은 필드는 무시하고 통과한다.
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TestMapper {

    // ⚪ RequestDTO -> 엔티티로 변환 (생성 시 사용)
    Test toEntity(TestRequestDTO dto);

    // ⚪ 엔티티 -> ResponseDTO로 변환
    // 필드 이름이 같으면 @Mapping이 필요 없지만, 명시적으로 지정할 수도 있다.
    TestResponseDTO toResponseDTO(Test test);

    // ⚪ 엔티티 리스트 -> ResponseDTO 리스트로 변환
    List<TestResponseDTO> toResponseDTO(List<Test> entities);
}