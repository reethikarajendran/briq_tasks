import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.io.File;
import java.util.List;
import java.util.regex.Pattern;

public class FileDownloadAndUpload {
    public static void main(String[] args) {
        System.setProperty("webdriver.chrome.driver", "chromedriver.exe");
        ChromeOptions options = new ChromeOptions();
        options.addArguments("download.default_directory=/");
        WebDriver driver = new ChromeDriver(options);
        driver.get("https://the-internet.herokuapp.com/download");
        List<WebElement> downloadLinks = driver.findElements(By.tagName("a"));
        Pattern pngPattern = Pattern.compile(".png$", Pattern.CASE_INSENSITIVE);
        File lastDownloadedFile = null;
        for (WebElement link : downloadLinks) {
            String href = link.getAttribute("href");
            if (href != null && pngPattern.matcher(href).find()) {
                link.click();
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                lastDownloadedFile = getLatestDownloadedFile("/");
            }
        }

        driver.get("https://the-internet.herokuapp.com/upload");
        WebElement fileInput = driver.findElement(By.id("file-upload"));
        if (lastDownloadedFile != null) {
            String filePath = lastDownloadedFile.getAbsolutePath();
            fileInput.sendKeys(filePath);
            WebElement uploadButton = driver.findElement(By.id("file-submit"));
            uploadButton.click();
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("No file with '.png' extension was downloaded.");
        }
        driver.quit();
    }

    private static File getLatestDownloadedFile(String directoryPath) {
        File dir = new File(directoryPath);
        File[] files = dir.listFiles();
        if (files == null || files.length == 0) {
            return null;
        }

        File lastModifiedFile = files[0];
        for (File file : files) {
            if (file.isFile() && file.lastModified() > lastModifiedFile.lastModified()) {
                lastModifiedFile = file;
            }
        }

        return lastModifiedFile;
    }
}
