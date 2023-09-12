import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.text.SimpleDateFormat;
import java.util.Date;

public class WebTableToCSV {
    public static void main(String[] args) {
        System.setProperty("webdriver.chrome.driver", "chromedriver.exe");
        WebDriver driver = new ChromeDriver();

        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);

        try {
            driver.get("http://the-internet.herokuapp.com/challenging_dom");

            WebElement tableElement = driver.findElement(By.cssSelector("table"));
            Document document = Jsoup.parse(tableElement.getAttribute("outerHTML"));

            List<String[]> data = new ArrayList<>();
            Elements rows = document.select("tr");
            for (int i = 1; i < rows.size(); i++) {
                Element row = rows.get(i);
                Elements cells = row.select("td");
                String[] rowData = new String[cells.size()];
                for (int j = 0; j < cells.size(); j++) {
                    rowData[j] = cells.get(j).text();
                }
                data.add(rowData);
            }

            SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yy-HH-mm-ss");
            String timestamp = dateFormat.format(new Date());
            String csvFileName = "/HOME/briq/webtable_" + timestamp + ".csv";

            try (FileWriter csvWriter = new FileWriter(csvFileName)) {
                for (String[] rowData : data) {
                    csvWriter.append(String.join(",", rowData));
                    csvWriter.append("\n");
                }
                System.out.println("CSV file generated successfully: " + csvFileName);
            } catch (IOException e) {
                e.printStackTrace();
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            driver.quit();
        }
    }
}
