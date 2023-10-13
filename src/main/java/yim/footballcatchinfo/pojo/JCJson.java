package yim.footballcatchinfo.pojo;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;
import java.util.List;

@Data
@Accessors(chain = true)
public class JCJson {
    private Value value;

    @Data
    @Accessors(chain = true)
    public class Value{
        private List<MatchInfoList> matchInfoList;
    }
    @Data
    @Accessors(chain = true)
    public class MatchInfoList{
        private List<MatchInfo> subMatchList;
        private String weekday;
    }
    @Data
    @Accessors(chain = true)
    public class MatchInfo{
        private String awayTeamAbbName;
        private String awayTeamAllName;

        private String homeTeamAbbName;
        private String homeTeamAllName;

        private String leagueAbbName;
        private String leagueAllName;

        private int matchNum;
        private String matchNumStr;

    }
}
