package yim.footballcatchinfo.pojo;

/**
 * Copyright 2023 bejson.com
 */

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * Auto-generated: 2023-10-13 19:14:47
 *
 * @author bejson.com (i@bejson.com)
 * @website http://www.bejson.com/java2pojo/
 */
@Data
@Accessors(chain = true)
public class Datas {
    private Integer gfId;
    public boolean setGfId(Integer id){
        gfId =id;
        return true;
    }
    private Date matchDate;
    private String hasPreview;
    private int competitionId;
    private double competitionPositiion;
    private String competitionName;
    private boolean isFinish;
    private String startHMStr;
    private String awayTeamCountry;
    private String score;
    private boolean hasReport;
    private String competitionCountry;
    private String stageName;
    private String homeTeamCountry;
    private String competitionNameFull;
    private String awayTeamName;
    private long id;
    private String dateNumOfWeek;
    private String homeTeamName;
    private int homeTeamId;
    private int awayTeamId;
    private String competitionTeamType;

}