package yim.footballcatchinfo.service;

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
import java.time.LocalDate;
import java.util.Date;

import static yim.footballcatchinfo.uitls.Utils.sendGetRequest;

/**
 * @author Yim
 * @version 1.0
 * @since 2023/10/13 17:05
 */
public class PaChong {
    public static void main(String[] args) throws IOException {
        _2daysMatches();
//        System.setProperty("webdriver.chrome.bin", "C:/Users/yimen/IdeaProjects/FootBallCatchInfo/tooljar/chrome-win64/chrome.exe");
        // 设置 Chrome WebDriver 的路径
        System.setProperty("webdriver.chrome.driver", "C:/Users/yimen/IdeaProjects/FootBallCatchInfo/tooljar/chromedriver.exe");

        // 配置 Chrome WebDriver 选项
        ChromeOptions options = new ChromeOptions();
        // 隐藏浏览器窗口
        options.addArguments("--headless");
        options.addArguments("--remote-allow-origins=*");
        // 创建 Chrome WebDriver 实例
        WebDriver driver = new ChromeDriver(options);
        try {
            // 打开网页
            driver.get("https://translate.google.com/?hl=zh-CN&sl=auto&tl=en&op=translate");

            // 查找页面元素
            WebElement element = driver.findElement(By.tagName("body"));

            // 获取页面内容
            String pageContent = element.getText();
            System.out.println(pageContent);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 关闭浏览器
            driver.quit();
        }
    }

    private  static void  _2daysMatches() throws IOException {
        LocalDate date = LocalDate.now();
        System.out.println(date);
        LocalDate date1 = date.plusDays(1);
        //        https://www.tzuqiu.cc/matches/queryFixture.json?date=%s+%E8%87%B3+%s
        String url2 = "https://www.tzuqiu.cc/matches/queryFixture.json?date="+date.toString()+"+%E8%87%B3+"+date1.toString();
        String s = sendGetRequest(url2);
//        MatchOddListResponse matchOdd = gson.fromJson(s, MatchOddListResponse.class);
    }

}
