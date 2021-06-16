package BabyBaby.Listeners;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

import BabyBaby.data.Data;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRoleAddEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRoleRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class Leaderboard extends ListenerAdapter{
    
    //Role Add for Stats
    @Override
    public void onGuildMemberRoleAdd(GuildMemberRoleAddEvent event) {
        if(!event.getGuild().getId().equals(Data.ethid))
            return;
        
        Role blind = event.getGuild().getRoleById(Data.blindID);
        if(event.getRoles().contains(blind))
            return;
        
            Connection c = null;
            PreparedStatement pstmt = null;
            try {
                Class.forName("org.sqlite.JDBC");
                c = DriverManager.getConnection(Data.db);
                pstmt = c.prepareStatement("INSERT OR REPLACE INTO STATS (ID, TIME) VALUES (?, ?);");
                pstmt.setString(1, event.getUser().getId());
                pstmt.setString(3, System.currentTimeMillis() + "");
                
                pstmt.execute();
                pstmt.close();
                c.close();
            } catch ( Exception e ) {
                System.out.println(e.getClass().getName() + ": " + e.getMessage());
            }

        
    }


    //Role Removal for Stats
    @Override
    public void onGuildMemberRoleRemove(GuildMemberRoleRemoveEvent event) {
        if(!event.getGuild().getId().equals(Data.ethid))
            return;
        
        Role blind = event.getGuild().getRoleById(Data.blindID);
        if(!event.getRoles().contains(blind))
            return;

        if(!Data.stats.contains(event.getUser().getId()))
            return;


        Connection c = null;
        PreparedStatement pstmt = null;
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection(Data.db);
            pstmt = c.prepareStatement("UPDATE STATS RANK = RANK + (? - TIME);");
            pstmt.setLong(1, System.currentTimeMillis());
            pstmt.execute();
            pstmt.close();
            c.close();
        } catch ( Exception e ) {
            System.out.println(e.getClass().getName() + ": " + e.getMessage());
        }

        

    }

    

}
