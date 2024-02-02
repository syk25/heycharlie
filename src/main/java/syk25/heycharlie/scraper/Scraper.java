package syk25.heycharlie.scraper;

import syk25.heycharlie.model.Company;
import syk25.heycharlie.model.ScrapedResult;

public interface Scraper {


    // 스크래핑
    ScrapedResult scrape(Company company);

    // 회사 티커와 이름
    Company scrapeCompanyByTicker(String ticker);
}
