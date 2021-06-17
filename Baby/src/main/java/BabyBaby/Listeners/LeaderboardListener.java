package BabyBaby.Listeners;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import BabyBaby.data.Data;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRoleAddEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRoleRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class LeaderboardListener extends ListenerAdapter{
    
    //Role Add for Stats
    @Override
    public void onGuildMemberRoleAdd(GuildMemberRoleAddEvent event) {
        if(!event.getGuild().getId().equals(Data.ethid))
            return;
        
        Role blind = event.getGuild().getRoleById(Data.blindID);
        if(!event.getRoles().contains(blind))
            return;

        Connection c = null;
        PreparedStatement pstmt = null;

        c = null;
        pstmt = null;
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection(Data.db);
            pstmt = c.prepareStatement("INSERT OR REPLACE INTO STATS (ID, TIME) VALUES (?, ?);");
            pstmt.setString(1, event.getUser().getId());
            pstmt.setString(2, System.currentTimeMillis() + "");
            
            pstmt.execute();
            pstmt.close();
            c.close();
        } catch ( Exception e ) {
            System.out.println(e.getClass().getName() + ": " + e.getMessage());
        }

        Data.stats.add(event.getUser().getId());
        
    }


    //Role Removal for Stats
    @Override
    public void onGuildMemberRoleRemove(GuildMemberRoleRemoveEvent event) {
        if(!event.getGuild().getId().equals(Data.ethid) || !event.getRoles().contains(event.getGuild().getRoleById(Data.blindID)) || !Data.stats.contains(event.getUser().getId()))
            return;
    
        Connection c = null;
        PreparedStatement pstmt = null;
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection(Data.db);
            pstmt = c.prepareStatement("UPDATE STATS SET RANK = Case When RANK Is Null THEN 0 ELSE RANK END + (? - TIME) WHERE ID=?;");
            pstmt.setLong(1, System.currentTimeMillis());
            pstmt.setString(2, event.getUser().getId());
            pstmt.execute();
            pstmt.close();
            c.close();
        } catch ( Exception e ) {
            System.out.println(e.getClass().getName() + ": " + e.getMessage());
        }
        Data.stats.remove(event.getUser().getId());

    }

    

}
