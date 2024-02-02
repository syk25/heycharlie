package syk25.heycharlie.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import syk25.heycharlie.model.Company;
import syk25.heycharlie.model.ScrapedResult;
import syk25.heycharlie.persist.entity.CompanyEntity;
import syk25.heycharlie.persist.entity.DividendEntity;
import syk25.heycharlie.persist.repository.CompanyRepository;
import syk25.heycharlie.persist.repository.DividendRepository;
import syk25.heycharlie.scraper.Scraper;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class CompanyService {

    private final Scraper yahooFinanceScraper;
    private final CompanyRepository companyRepository;
    private final DividendRepository dividendRepository;

    public Company save(String ticker) {
        boolean exists = this.companyRepository.existsByTicker(ticker);
        if(exists){
            throw new RuntimeException("Already existing ticker:" + ticker);
        }
        return this.storeCompanyAndDividend(ticker);
    }

    private Company storeCompanyAndDividend(String ticker) {
        // 티커 기준 회사 스크래핑
        Company company = this.yahooFinanceScraper.scrapeCompanyByTicker(ticker);
        if(ObjectUtils.isEmpty(company)){
            throw new RuntimeException("Failed to scrape ticker -> " + ticker);
        }
        // 회사 존재시 배당금 정보 스크래핑
        ScrapedResult result = this.yahooFinanceScraper.scrape(company);

        // 스크래핑 결과 반환
        CompanyEntity companyEntity = this.companyRepository.save(new CompanyEntity(company));
        List<DividendEntity> dividendEntities = result.getDividends().stream()
                .map(e -> new DividendEntity(companyEntity.getId(), e)).collect(Collectors.toList());

        this.dividendRepository.saveAll(dividendEntities);
        return company;
    }
}
