package yim.footballcatchinfo.service;

import com.google.gson.Gson;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import yim.footballcatchinfo.pojo.*;
import yim.footballcatchinfo.uitls.Utils;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
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
    static String filePath = "C:\\Users\\mySpace\\workspace\\projects\\FootBallCatchInfo\\tooljar\\result\\";

    public static void main(String[] args) throws Exception {

        // 创建文件写入流
        BufferedWriter writer = new BufferedWriter(new FileWriter(filePath + LocalDate.now() + ".txt"));
        List<Datas> datas = _2daysMatches();
        List<CompletableFuture<String>> futures = new ArrayList<>();

        for (Datas d : datas) {
            CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
                try {

                    // 创建 Chrome WebDriver 实例
                    WebDriver driver = new ChromeDriver(options);
                    StringBuilder b = new StringBuilder();
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
                    getTeamPlayers(homePage, h);
                    getTeamPlayers(awayPage, a);

                    List<Player> hList = h.getPlayers().stream().sorted(Comparator.comparingInt(Player::getTimeAll).reversed()).collect(Collectors.toList());


                    List<Player> aList = a.getPlayers().stream().sorted(Comparator.comparingInt(Player::getTimeAll).reversed()).collect(Collectors.toList());
                    Double hp = 0.0;
                    Double ap = 0.0;

                    for (int x = 0; x < 11; x++) {
                        Player ph = hList.get(x);
                        //计算近一个月比赛的平均分
                        Document page = getWebPageByChrome(ph.getHref(), driver);
                        Element table = page.getElementsByClass("player-fixture").get(0);
                        Elements rows = table.select("tbody tr");
                        Double countPF = 0.0;
                        Integer count = 0;
                        // 遍历并输出每个 <tr> 元素的内容
                        for (Element row : rows) {
                            String date = row.children().get(1).text();
                            LocalDate localDate = Utils.transferDate(date);
                            if (localDate.isAfter(now.minusDays(30))) {
                                if (!"-".equals(row.children().get(10).text())) {
                                    double v = Double.parseDouble(row.children().get(10).text());
                                    countPF += v;
                                    count++;
                                }
                            }
                        }
                        if (count > 0) {
                            ph.setScore(countPF / count);
                            ph.setPonit(ph.getScore() * ph.getValue());
                        }
                        hp += ph.getPonit();
                    }


                    for (int x = 0; x < 11; x++) {
                        Player pa = aList.get(x);
                        //计算近一个月比赛的平均分
                        Document page = getWebPageByChrome(pa.getHref(), driver);
                        Element table = page.getElementsByClass("player-fixture").get(0);
                        Elements rows = table.select("tbody tr");
                        Double countPF = 0.0;
                        Integer count = 0;
                        // 遍历并输出每个 <tr> 元素的内容
                        for (Element row : rows) {
                            String date = row.children().get(1).text();
                            LocalDate localDate = Utils.transferDate(date);
                            if (localDate.isAfter(now.minusDays(30))) {
                                if (!"-".equals(row.children().get(10).text())) {
                                    double v = Double.parseDouble(row.children().get(10).text());
                                    countPF += v;
                                    count++;
                                }
                            }
                        }
                        if (count > 0) {
                            pa.setScore(countPF / count);
                            pa.setPonit(pa.getScore() * pa.getValue());
                        }
                        ap += pa.getPonit();
                    }


                    if (hp / ap > 1.2) {

                        b.append("  《胜胜胜》    " + "倍数: " + String.valueOf(hp / ap).substring(0, 5) + "     @");
                        b.append(hp + "   ###   " + ap);
                        b.append("\n");
                    } else if (ap / hp > 1.2) {

                        b.append("  《负负负》    " + "倍数: " + String.valueOf(ap / hp).substring(0, 5) + "     @");
                        b.append(hp + "   ###   " + ap);
                        b.append("\n"); // 换行
                    } else {

                        b.append("  《平平平》    " + "倍数: " + String.valueOf(hp / ap).substring(0, 5) + "     @" + ap / hp + "--->");
                        b.append(hp + "   ###   " + ap);
                        b.append("\n"); // 换行
                    }
                    b.append("=======================================================================");
                    b.append("\n"); // 换行

                    return b.toString();
                } catch (Exception e) {
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

    }

    private static void getTeamPlayers(Document page, Team t) throws IOException {
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
                Double score = !"-".equals(row.children().get(8).text()) ? Double.valueOf(row.children().get(8).text()) : 1.0;
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
        String url2 = "https://www.tzuqiu.cc/matches/queryFixture.json?date="
                + now.toString().replaceAll("-", ".") + "+%E8%87%B3+" + date1.toString().replaceAll("-", ".");
        String s = sendGetRequest(url2);
        JsonRootBean bean = new Gson().fromJson(s, JsonRootBean.class);

        //当天竞彩官方比赛
        String jc = sendGetRequest("https://webapi.sporttery.cn/gateway/jc/football/getMatchListV1.qry?clientCode=3001");
        JCJson jcMatches = new Gson().fromJson(jc, JCJson.class);
        List<Datas> collect = bean.getDatas().stream().filter(itemA ->
                        jcMatches.getValue().getMatchInfoList().get(0).getSubMatchList()
                                .stream().anyMatch(itemB -> itemA.getHomeTeamName().equals(itemB.getHomeTeamAllName())
                                        && itemA.getAwayTeamName().equals(itemB.getAwayTeamAllName()) && itemA.setGfId(itemB.getMatchNum())))
                .collect(Collectors.toList());
        List<Datas> collect1 = collect.stream().sorted(Comparator.comparingInt(Datas::getGfId)).collect(Collectors.toList());
        System.out.println(collect1);
        return collect1;
    }

}
