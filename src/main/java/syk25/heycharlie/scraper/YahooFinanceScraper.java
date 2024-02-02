package syk25.heycharlie.scraper;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;
import syk25.heycharlie.model.Company;
import syk25.heycharlie.model.Dividend;
import syk25.heycharlie.model.ScrapedResult;
import syk25.heycharlie.persist.entity.constants.Month;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;

@Component
public class YahooFinanceScraper implements Scraper{

    // URL 정보
//    private static final String URL = "https://finance.yahoo.com/quote/COKE/history?period1=99100800&period2=1706659200&filter=div&frequency=1mo";

    // 특정 회사의 배당금 url
    private static final String STATISTICS_URL = "https://finance.yahoo.com/quote/%s/history?period1=%d&period2=%d&filter=div&frequency=1mo"; // 티커와 시간 부분 일반화
    private static final long START_TIME = 86400;

    // 회사 정보 url - 회사명 추출
    private static final String SUMMARY_URL = "https://finance.yahoo.com/quote/%s?p=%s";

    // 스크래핑
    @Override
    public ScrapedResult scrape(Company company) {
        ScrapedResult scrapResult = new ScrapedResult();
        scrapResult.setCompany(company);
        try {
            long now = System.currentTimeMillis() / 1000;
            String url = String.format(STATISTICS_URL, company.getTicker(), START_TIME, now);

            Connection connect = Jsoup.connect(url);
            Document document = connect.get();
            Elements elements = document.getElementsByAttributeValue("data-test", "historical-prices");
            Element element = elements.get(0);

            Element tbody = element.children().get(1);

            ArrayList<Dividend> dividends = new ArrayList<>();

            for (Element e : tbody.children()) {
                String txt = e.text();
                if (!txt.endsWith("Dividend")) {
                    continue;
                }
                String[] splits = txt.split(" ");
                int month = Month.stringToNumber(splits[0]);
                int day = Integer.parseInt(splits[1].replace(",", ""));
                int year = Integer.parseInt(splits[2]);
                String dividend = splits[3];

                if (month < 0) {
                    throw new RuntimeException("Unexpected Month enum value -> " + splits[0]);
                }

                dividends.add(
                        Dividend.builder()
                                .date(LocalDateTime.of(year, month, day, 0, 0))
                                .dividend(dividend)
                                .build()
                );
            }
            scrapResult.setDividends(dividends);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return scrapResult;
    }

    // 회사 티커와 이름
    @Override
    public Company scrapeCompanyByTicker(String ticker){
        String url = String.format(SUMMARY_URL,ticker,ticker);

        try{
            Document document = Jsoup.connect(url).get();
            Element titleElement = document.getElementsByTag("h1").get(0);
//            String title = titleElement.text().split("-")[1].trim();
            String title = titleElement.text();

            return Company.builder()
                    .ticker(ticker)
                    .name(title)
                    .build();
        } catch (IOException e){
            e.printStackTrace();
        }

        return null;
    }
}
