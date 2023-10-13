package yim.footballcatchinfo.service;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class WebPageService {

    public static  WebDriver driver =null;
    static {
        //        System.setProperty("webdriver.chrome.bin", "C:/Users/yimen/IdeaProjects/FootBallCatchInfo/tooljar/chrome-win64/chrome.exe");
        // 设置 Chrome WebDriver 的路径
        System.setProperty("webdriver.chrome.driver", "C:\\Users\\mySpace\\workspace\\projects\\FootBallCatchInfo\\tooljar\\chromedriver.exe");

        // 配置 Chrome WebDriver 选项
        ChromeOptions options = new ChromeOptions();
        // 隐藏浏览器窗口
        options.addArguments("--headless");
        options.addArguments("--remote-allow-origins=*");
        // 创建 Chrome WebDriver 实例
        driver = new ChromeDriver(options);
    }
    public static String sendGetRequest(String url) throws IOException {
        HttpURLConnection connection = null;
        BufferedReader reader = null;
        StringBuilder response = new StringBuilder();

        try {
            URL apiUrl = new URL(url);
            connection = (HttpURLConnection) apiUrl.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
            reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
        } finally {
            if (reader != null) {
                reader.close();
            }
            if (connection != null) {
                connection.disconnect();
            }
        }

        return response.toString();
    }

    public static Document getWebPageByChrome(String url){
        // 打开网页
        driver.get(url);

        // 获取页面内容
        String pageContent = driver.getPageSource();

        Document parse = Jsoup.parse(pageContent);
        return parse;
    }

}
