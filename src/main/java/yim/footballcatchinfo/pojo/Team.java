package yim.footballcatchinfo.pojo;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;

@Data
@Accessors(chain = true)
public class Team {
    private String teamName;
    private List<Player> players;
    public Team(String teamName){
        this.teamName=teamName;
        players=new ArrayList<Player>();
    }
    public void add(Player p){
        players.add(p);
    }
}
