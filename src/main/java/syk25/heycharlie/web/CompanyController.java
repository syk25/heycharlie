package syk25.heycharlie.web;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/company")
public class CompanyController {

    // 자동완성
    @GetMapping("/autocomplete")
    public ResponseEntity<?> autoComplete(@RequestParam String keyword) {
        return null;
    }

    // 회사 조회
    @GetMapping
    public ResponseEntity<?> searchCompany() {
        return null;
    }

    // 회사 저장
    @PostMapping
    public ResponseEntity<?> addCompany() {
        return null;
    }

    // 회사 삭제
    @DeleteMapping
    public ResponseEntity<?> deleteCompany() {
        return null;
    }
}
