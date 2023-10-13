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
import yim.footballcatchinfo.pojo.Datas;
import yim.footballcatchinfo.pojo.JsonRootBean;
import yim.footballcatchinfo.pojo.Player;
import yim.footballcatchinfo.pojo.Team;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static yim.footballcatchinfo.service.WebPageService.getWebPageByChrome;
import static yim.footballcatchinfo.service.WebPageService.sendGetRequest;

/**
 * @author Yim
 * @version 1.0
 * @since 2023/10/13 17:05
 */
public class PaChong {
    static String url = "https://www.tzuqiu.cc";
    static String filePath = "C:\\Users\\mySpace\\workspace\\projects\\FootBallCatchInfo\\tooljar\\result\\";

    public static void main(String[] args) throws IOException {
        // 创建文件写入流
        BufferedWriter writer = new BufferedWriter(new FileWriter(filePath + LocalDate.now()));
        List<Datas> datas = _2daysMatches();
        for (Datas d : datas) {
            //比赛信息
            writer.write(d.getCompetitionId() + "  " + d.getHomeTeamName() + " VS " + d.getAwayTeamName() + "   " + d.getCompetitionName());
            writer.newLine(); // 换行
            Document homePage = getWebPageByChrome("https://www.tzuqiu.cc/teams/" + d.getHomeTeamId() + "/show.do");
            Document awayPage = getWebPageByChrome("https://www.tzuqiu.cc/teams/" + d.getAwayTeamId() + "/show.do");
            Team h = new Team(d.getHomeTeamName());
            Team a = new Team(d.getAwayTeamName());
            getTeamPlayers(homePage, h);
            getTeamPlayers(awayPage, a);
            List<Player> hList = h.getPlayers().stream().sorted(Comparator.comparingInt(Player::getTimeAll).reversed()).collect(Collectors.toList());


            List<Player> aList = a.getPlayers().stream().sorted(Comparator.comparingInt(Player::getTimeAll).reversed()).collect(Collectors.toList());
            Double hp = 0.0;
            Double ap = 0.0;
            for (int x = 0; x < 11; x++) {
                hp += hList.get(x).getPonit();
            }
            for (int x = 0; x < 11; x++) {
                ap += aList.get(x).getPonit();
            }
            if (hp - ap > 1000) {
                writer.write("    胜@@@@@" + hp + "#####" + ap);
                writer.newLine(); // 换行
            } else if (ap - hp > 1000) {
                writer.write("    负@@@@@" + hp + "#####" + ap);
                writer.newLine(); // 换行
            } else {
                writer.write("    平@@@@@" + hp + "#####" + ap);
                writer.newLine(); // 换行
            }
            writer.write("================================================================");
            writer.newLine(); // 换行
            writer.flush();
        }
        writer.close();
    }

    private static void getTeamPlayers(Document page, Team t) {
        // 查找具有指定 id 的表格
        Element table = page.getElementById("playersTable");

        // 获取该表格中的所有 <tr> 元素
        if (table != null) {
            Elements rows = table.select("tbody tr");

            // 遍历并输出每个 <tr> 元素的内容
            for (Element row : rows) {
                String name = row.children().get(1).child(0).attr("title");
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

                Player player = new Player(name, href, timeAll, score, value, score * value);
                t.add(player);
            }
        } else {
            System.out.println("Table not found with the specified id.");
        }
    }

    private static List<Datas> _2daysMatches() throws IOException {
        LocalDate date = LocalDate.now();
        System.out.println(date);
        LocalDate date1 = date.plusDays(1);
        //        https://www.tzuqiu.cc/matches/queryFixture.json?date=%s+%E8%87%B3+%s
        String url2 = "https://www.tzuqiu.cc/matches/queryFixture.json?date="
                + date.toString().replaceAll("-", ".") + "+%E8%87%B3+" + date1.toString().replaceAll("-", ".");
        String s = sendGetRequest(url2);
        JsonRootBean bean = new Gson().fromJson(s, JsonRootBean.class);
        return bean.getDatas();
    }

}
