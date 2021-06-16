package BabyBaby.Command.commands.Admin;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

import BabyBaby.Command.AdminCMD;
import BabyBaby.Command.CommandContext;
import BabyBaby.Command.StandardHelp;
import BabyBaby.data.Data;
import net.dv8tion.jda.api.entities.MessageEmbed;

public class SaveMSGID implements AdminCMD {


    @Override
    public String getName() {
        return "savemsgid";
    }

    @Override
    public void handleAdmin(CommandContext ctx) {

        Connection c = null;
        PreparedStatement pstmt = null;
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection(Data.db);
            pstmt = c.prepareStatement("INSERT INTO MSGS (GUILDID,MSGID) VALUES (?, ?);");
            pstmt.setString(1, ctx.getGuild().getId()); 
            pstmt.setString(2, ctx.getArgs().get(0));
            pstmt.executeUpdate();
            pstmt.close();
            c.close();
        } catch ( Exception e ) {
            e.printStackTrace(); 
            return;
        }
    }

    @Override
    public MessageEmbed getAdminHelp(String prefix) {
        return StandardHelp.Help(prefix, getName(), "<msgid>", "Use this cmd to save a getrole cmd message in a Channel like RoleAssignement.");
    }
    
}
