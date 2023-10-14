package yim.footballcatchinfo.uitls;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * @author Yim
 * @version 1.0
 * @since 2023/10/13 17:59
 */
public class Utils {
    // 日期格式
    public static DateTimeFormatter formatter=DateTimeFormatter.ofPattern("yyyy-MM-dd");
    public static LocalDate transferDate(String d){


        // 将字符串解析为LocalDate对象
       return LocalDate.parse(d, formatter);
    }
}
