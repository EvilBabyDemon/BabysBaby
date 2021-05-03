package BabyBaby.Command.commands.Public;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

import BabyBaby.data.data;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;

public class GetUnmute implements Runnable {
    public User muted;
    public Guild guild;
    public Role stfu;

    public GetUnmute(User user, Guild tempG, Role muteRole) {
        muted = user;
        guild = tempG;
        stfu = muteRole;
    }

    public void run() {	
        
        guild.removeRoleFromMember(guild.retrieveMember(muted).complete(), stfu).queue();
        MuteCMD.variables.remove(MuteCMD.userMuted.get(guild.getMember(muted)));
        MuteCMD.userMuted.remove(guild.getMember(muted));
        
        Connection c = null;
        PreparedStatement stmt = null;
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection(data.db);
            
            stmt = c.prepareStatement("DELETE FROM USERS WHERE ID = ? AND GUILDID = ?;");
            stmt.setString(1, muted.getId());
            stmt.setString(2, guild.getId());
            stmt.execute();
            stmt.close();
            c.close();
        } catch ( Exception e ) {
            e.printStackTrace(); 
            return;
        }
        muted.openPrivateChannel().queue((channel) -> {
            channel.sendMessage("You shall be unmuted! Hope this worked...").queue();
        });

        
        
    }
}