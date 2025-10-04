package com.example.muaring.config;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@Slf4j
@Component
@RequiredArgsConstructor
public class DatabaseHealthCheck {

    private final DataSource dataSource;

    @PostConstruct
    public void testConnection() {
        try (Connection conn = dataSource.getConnection()) {
            String rawUrl = conn.getMetaData().getURL();
            log.info("✅ DB 연결에 성공했습니다. 연결 URL: {}", maskJdbcUrl(rawUrl));
            log.info("DB 드라이버: {}", conn.getMetaData().getDriverName());
        } catch (SQLException e) {
            log.error("❌ DB 연결에 실패했습니다.: {}", e.getMessage());
        }
    }

    // ⚪ JDBC URL에서 민감한 정보를 마스킹하는 메서드
    private static String maskJdbcUrl(String url) {
        if (url == null) return null;
        // 쿼리 파라미터에 비밀번호가 있으면 password=****로 마스킹
        String masked = url.replaceAll("(?i)(password=)[^&;]+", "$1****");
        // RL 권한(user:pass@host)에 비밀번호가 있으면 user:****@로 마스킹
        masked = masked.replaceAll("(?i)(//[^:@/]+:)[^@/]+@", "$1****@");
        return masked;
    }
}