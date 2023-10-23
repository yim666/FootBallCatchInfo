package yim.footballcatchinfo.service;

import com.google.gson.Gson;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import yim.footballcatchinfo.pojo.*;
import yim.footballcatchinfo.uitls.Utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.ConnectException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static yim.footballcatchinfo.service.WebPageService.*;

/**
 * @author Yim
 * @version 1.0
 * @since 2023/10/13 17:05
 */
public class PaChong {
    static String url = "https://www.tzuqiu.cc";
    static String filePath = ".\\tooljar\\result\\";
    static int playerSize = 13;

    public static void main(String[] args) throws Exception {
        // 设置 Chrome WebDriver 的路径
        System.setProperty("webdriver.chrome.driver", ".\\tooljar\\chromedriver.exe");

        // 配置 Chrome WebDriver 选项
        ChromeOptions   options = new ChromeOptions();
        // 隐藏浏览器窗口
        options.addArguments("--headless");
        options.addArguments("--remote-allow-origins=*");
//        options.addArguments("--ignore-certificate-errors");
//        options.addArguments("--test-type");
        String proxyServer = "127.0.0.1:6666";
//       proxy
        Proxy proxy = new Proxy().setHttpProxy(proxyServer).setSslProxy(proxyServer);
        options.setProxy(proxy);

        // 设置变量ACCEPT_SSL_CERTS的值为True
        options.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);

        // 创建文件写入流
        BufferedWriter writer = new BufferedWriter(new FileWriter(filePath + LocalDate.now() + "_" + LocalTime.now().getHour() + ".txt"));
        BufferedWriter writerERR = new BufferedWriter(new FileWriter(filePath + LocalDate.now() + "_" + LocalTime.now().getHour() + "ERROR.txt"));
        List<Datas> datas = _2daysMatches();
//        System.setProperty("http.proxyHost", "127.0.0.1");
//        System.setProperty("http.proxyPort", "6666");
        List<CompletableFuture<String>> futures = new ArrayList<>();
        writer.write("===================身价>6600一切皆有可能============");
        writer.flush();
        writer.newLine();
        for (Datas d : datas) {
            CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
                StringBuilder b = new StringBuilder();

                try {

                    // 创建 Chrome WebDriver 实例
                    ChromeDriverService service = new ChromeDriverService.Builder()
                            .usingDriverExecutable(new File(".\\tooljar\\chromedriver.exe"))
                            .usingAnyFreePort().build();
                    service.start();
//                    ChromeOptions cap = options;

                    WebDriver driver = new ChromeDriver(service, options);
                    driver.manage().timeouts().implicitlyWait(Duration.ofMillis(2000));

                    //比赛信息
                    String firstL = d.getGfId() + "  " + d.getCompetitionName() + "  " + d.getHomeTeamName() + " VS " + d.getAwayTeamName();
                    while (firstL.length() < 30) {
                        firstL += "一";
                    }
                    b.append(firstL);
                    Document homePage = getWebPageByChrome("https://www.tzuqiu.cc/teams/" + d.getHomeTeamId() + "/show.do", driver);
                    Document awayPage = getWebPageByChrome("https://www.tzuqiu.cc/teams/" + d.getAwayTeamId() + "/show.do", driver);
                    Team h = new Team(d.getHomeTeamName());
                    Team a = new Team(d.getAwayTeamName());
                    Thread.sleep(1500);
                    getTeamPlayers(homePage, h);
                    Thread.sleep(1500);
                    getTeamPlayers(awayPage, a);

                    List<Player> hList = h.getPlayers().stream().sorted(Comparator.comparingInt(Player::getTimeAll).reversed()).collect(Collectors.toList());

                    List<Player> aList = a.getPlayers().stream().sorted(Comparator.comparingInt(Player::getTimeAll).reversed()).collect(Collectors.toList());

                    if (hList.size() < playerSize || aList.size() < playerSize) {
                        writerERR.write(homePage.toString());
                        writerERR.write(awayPage.toString());
                        writerERR.flush();
                        b.append("===ERROR!!!!!人数不足 ====" + "https://www.tzuqiu.cc/teams/" + d.getHomeTeamId() + "/show.do" + "   " + "https://www.tzuqiu.cc/teams/" + d.getAwayTeamId() + "/show.do");
                        b.append("\n"); // 换行
                        b.append("=======================================================================");
                        b.append("\n"); // 换行

//                        driver.quit();
                        service.stop();
                        return b.toString();
                    }

                    Double hp = 0.0;
                    Double ap = 0.0;

                    Double hScore = 0.0;
                    Double aScore = 0.0;

                    Double hValue = 0.0;
                    Double aValue = 0.0;
                    //计算球员近一个月的平均评分

                    for (int x = 0; x < playerSize; x++) {
                        Player ph = hList.get(x);
                        System.out.println("球员：" + ph.toString());
//                        //计算近一个月比赛的平均分
//                        Document page = getWebPageByChrome(ph.getHref(), driver);
//                        if(page.getElementsByClass("player-fixture").size() == 0){
//                            Thread.sleep(2000);
//                            page = getWebPageByChrome(ph.getHref(), driver);
//                        }
//                        if (page.getElementsByClass("player-fixture").size() == 0) {
//                            writerERR.write(page.toString());
//                            writerERR.flush();
//                            b.append("===ERROR!!!!!近期无比赛 home负 ====" + "球员：" + ph.toString());
//                            b.append("\n"); // 换行
//                            b.append("=======================================================================");
//                            b.append("\n"); // 换行
//
////                            driver.quit();
//                            service.stop();
//                            return b.toString();
//                        }
//                        Element table = page.getElementsByClass("player-fixture").get(0);
//                        Elements rows = table.select("tbody tr");
//                        Double countPF = 0.0;
//                        Integer count = 0;
//                        // 遍历并输出每个 <tr> 元素的内容
//                        for (Element row : rows) {
//                            String date = row.children().get(1).text();
//                            LocalDate localDate = Utils.transferDate(date);
//                            if (localDate.isAfter(now.minusDays(30))) {
//                                if (!"-".equals(row.children().get(10).text())) {
//                                    double v = Double.parseDouble(row.children().get(10).text());
//                                    countPF += v;
//                                    count++;
//                                }
//                            }
//                        }
//                        if (count > 0) {
//                            ph.setScore(countPF / count);
//                            ph.setPonit(ph.getScore() * ph.getValue());
//                        }
                        hp += ph.getPonit();
                        hScore += ph.getScore();
                        hValue += ph.getValue();
                    }


                    for (int x = 0; x < playerSize; x++) {
                        Player pa = aList.get(x);
                        System.out.println("球员：" + pa.toString());
//                        //计算近一个月比赛的平均分
//                        Document page = getWebPageByChrome(pa.getHref(), driver);
//                        if(page.getElementsByClass("player-fixture").size() == 0){
//                            Thread.sleep(2000);
//                            page = getWebPageByChrome(pa.getHref(), driver);
//                        }
//                        if (page.getElementsByClass("player-fixture").size() == 0) {
//                            writerERR.write(page.toString());
//                            writerERR.flush();
//                            System.out.println("球员：" + pa.toString());
//                            b.append("===ERROR!!!!!近期无比赛 home胜 ====" + "球员：" + pa.toString());
//                            b.append("\n"); // 换行
//                            b.append("=======================================================================");
//                            b.append("\n"); // 换行
////                            driver.quit();
//                            service.stop();
//
//                            return b.toString();
//                        }
//                        Element table = page.getElementsByClass("player-fixture").get(0);
//                        Elements rows = table.select("tbody tr");
//                        Double countPF = 0.0;
//                        Integer count = 0;
//                        // 遍历并输出每个 <tr> 元素的内容
//                        for (Element row : rows) {
//                            String date = row.children().get(1).text();
//                            LocalDate localDate = Utils.transferDate(date);
//                            if (localDate.isAfter(now.minusDays(30))) {
//                                if (!"-".equals(row.children().get(10).text())) {
//                                    double v = Double.parseDouble(row.children().get(10).text());
//                                    countPF += v;
//                                    count++;
//                                }
//                            }
//                        }
//                        if (count > 0) {
//                            pa.setScore(countPF / count);
//                            pa.setPonit(pa.getScore() * pa.getValue());
//                        }
                        ap += pa.getPonit();
                        aScore += pa.getScore();
                        aValue += pa.getValue();
                    }


                    if (hp / ap > 1.2) {

                        b.append("  《胜胜胜》    " + "倍数: " + String.valueOf(hp / ap).substring(0, 4) + "     @");
                        b.append(hp + "   ###   " + ap);
                        b.append("  球员状态: " + hScore + "   ###   " + aScore);
                        b.append("  总身价: " + hValue + "   ###   " + aValue);
                        b.append("\n");
                    } else if (ap / hp > 1.2) {

                        b.append("  《负负负》    " + "倍数: " + String.valueOf(ap / hp).substring(0, 4) + "     @");
                        b.append(hp + "   ###   " + ap);
                        b.append("  球员状态: " + hScore + "   ###   " + aScore);
                        b.append("  总身价: " + hValue + "   ###   " + aValue);
                        b.append("\n"); // 换行
                    } else {

                        b.append("  《平平平》    " + "倍数: " + String.valueOf(hp / ap).substring(0, 4) + "     @");
                        b.append(hp + "   ###   " + ap);
                        b.append("  球员状态: " + hScore + "   ###   " + aScore);
                        b.append("  总身价: " + hValue + "   ###   " + aValue);
                        b.append("\n"); // 换行
                    }
                    b.append("=======================================================================");
                    b.append("\n"); // 换行
//                    driver.quit();
                    service.stop();
                    return b.toString();
                } catch (Exception e) {
//                    b.append("===ERROR!!!!!====");
//                    b.append("\n"); // 换行
//                    b.append("=======================================================================");
//                    b.append("\n"); // 换行
//                    System.out.println(e);
//                    return b.toString();

                    throw new RuntimeException(e);

                }
            });
            futures.add(future);

        }
        CompletableFuture<Void> allFutures = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
        CompletableFuture<String> combinedFuture = allFutures.thenApply(v -> {
            StringBuilder sb = new StringBuilder();
            for (CompletableFuture<String> future : futures) {
                sb.append(future.join());
            }
            return sb.toString();
        });
        String result = combinedFuture.join();
        writer.write(result);
        writer.flush();

        writer.close();
        writerERR.close();


    }

    private static void getTeamPlayers(Document page, Team t)  {
        // 查找具有指定 id 的表格
        Element table = page.getElementById("playersTable");

        // 获取该表格中的所有 <tr> 元素
        if (table != null) {
            Elements rows = table.select("tbody tr");

            // 遍历并输出每个 <tr> 元素的内容
            for (Element row : rows) {
                String name = row.children().get(1).child(0).attr("title");
                //受伤停赛
                boolean suspension = row.children().get(1).child(0).toString().contains("suspension");
                if (suspension) continue;

                String href = url + row.children().get(1).child(0).attr("href");
                Integer timeAll = !"-".equals(row.children().get(3).text()) ? Integer.valueOf(row.children().get(3).text()) : 1;
                Double score = !"-".equals(row.children().get(8).text()) ? Double.valueOf(row.children().get(8).text()) : 6.0;
                String sj = !"-".equals(row.children().get(9).text()) ? row.children().get(9).text() : "";
                Double value = 1.0;
                if (sj.endsWith("万")) {
                    value = Double.valueOf(sj.substring(0, sj.length() - 1));
                } else if (sj.endsWith("亿")) {
                    value = Double.valueOf(sj.substring(0, sj.length() - 1)) * 10000;
                }

                //计算近一个月比赛的平均分
//                Document page1 = getWebPageByChrome(href);


                Player player = new Player(name, href, timeAll, score, value, score * value);
                t.add(player);
            }
        } else {
            System.out.println("Table not found with the specified id.");
        }
    }

    static LocalDate now = LocalDate.now();

    private static List<Datas> _2daysMatches() throws IOException {
//        LocalDate date = LocalDate.now();
        System.out.println(now);
        LocalDate date1 = now.plusDays(1);
        //        https://www.tzuqiu.cc/matches/queryFixture.json?date=%s+%E8%87%B3+%s
        String url1 = "https://www.tzuqiu.cc/matches/queryFixture.json?date="
                + now.toString().replaceAll("-", ".") + "+%E8%87%B3+" + now.toString().replaceAll("-", ".");
        String s = sendGetRequest(url1);

        String url2 = "https://www.tzuqiu.cc/matches/queryFixture.json?date="
                + date1.toString().replaceAll("-", ".") + "+%E8%87%B3+" + date1.toString().replaceAll("-", ".");
        String s2 = sendGetRequest(url2);
        if ("{}".equals(s) || "{}".equals(s2)) {
            System.out.println("request Error");
            throw new ConnectException();

        }
        JsonRootBean bean = new Gson().fromJson(s, JsonRootBean.class);
        JsonRootBean bean2 = new Gson().fromJson(s2, JsonRootBean.class);
        List<Datas> collect1 = bean.getDatas();
        List<Datas> collect2 = bean2.getDatas();
        collect1.addAll(collect2);
//         //只看当天竞彩官方比赛
        String jc = sendGetRequest("https://webapi.sporttery.cn/gateway/jc/football/getMatchListV1.qry?clientCode=3001");
        JCJson jcMatches = new Gson().fromJson(jc, JCJson.class);
        List<Datas> collect = collect1.stream().filter(itemA ->
                        jcMatches.getValue().getMatchInfoList().get(0).getSubMatchList()
                                .stream().anyMatch(itemB ->itemA.getCompetitionName().equals(itemB.getLeagueAbbName())&& (itemA.getHomeTeamName().equals(itemB.getHomeTeamAllName())
                                        || itemA.getAwayTeamName().equals(itemB.getAwayTeamAllName())) && itemA.setGfId(itemB.getMatchNum())))
                .collect(Collectors.toList());
        collect1 = collect.stream().sorted(Comparator.comparingInt(Datas::getGfId)).collect(Collectors.toList());
        System.out.println(collect1);


        return collect1;
    }

}
