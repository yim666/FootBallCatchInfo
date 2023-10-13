package yim.footballcatchinfo;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.Date;

@SpringBootTest
class FootBallCatchInfoApplicationTests {

    @Test
    void contextLoads() {
        LocalDate date = LocalDate.now();
        System.out.println(date);
        LocalDate date1 = date.plusDays(1);
        System.out.println(date);
        System.out.println(date1);
    }

}
