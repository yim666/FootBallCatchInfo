package yim.footballcatchinfo.pojo;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class Player {
    private String name;
    private String href;
    private Integer timeAll;
    //评分
    private Double score;
    //身价
    private Double value;
    // *
    private Double ponit;

    public Player(String name, String href, Integer timeAll, Double score, Double value,Double ponit) {
        this.name = name;
        this.href = href;
        this.timeAll = timeAll;
        this.score = score;
        this.value = value;
        this.ponit =ponit;
    }
}
