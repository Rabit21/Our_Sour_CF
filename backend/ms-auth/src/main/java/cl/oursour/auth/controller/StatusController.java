package cl.oursour.auth.controller;

import java.util.Map;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class StatusController {
    private final JdbcTemplate jdbc;
    public StatusController(JdbcTemplate jdbc) { this.jdbc = jdbc; }
    @GetMapping("/api/auth/estado")
    public Map<String, Object> status() {
        return Map.of("servicio", "ms-auth", "mysql", jdbc.queryForObject("SELECT 1", Integer.class) == 1);
    }
}
