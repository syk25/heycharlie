package syk25.heycharlie.model;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
public class ScrapedResult {

    // 회사정보
    private Company company;

    // 배당금 정보
    private List<Dividend> dividends;

    // ScrapedResult 생성자
    public ScrapedResult() {
        this.dividends = new ArrayList<>();
    }
}
