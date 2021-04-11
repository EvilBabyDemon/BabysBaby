package BabyBaby.Command.commands.Admin;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

import BabyBaby.Command.AdminCMD;
import BabyBaby.Command.CommandContext;
import BabyBaby.Command.StandardHelp;
import net.dv8tion.jda.api.entities.MessageEmbed;

public class saveMSGID implements AdminCMD {

    @Override
    public void handleOwner(CommandContext ctx) {
       handleAdmin(ctx);
    }

    @Override
    public MessageEmbed getOwnerHelp(String prefix) {
        return getAdminHelp(prefix);
    }

    @Override
    public String getName() {
        return "savemsgid";
    }

    @Override
    public void handleAdmin(CommandContext ctx) {

        Connection c = null;
        Statement stmt = null;
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:testone.db");
            stmt = c.createStatement();
            stmt.executeUpdate("INSERT INTO MSGS (GUILDID,MSGID) " +
            "VALUES (" + ctx.getGuild().getId() + ", " + ctx.getArgs().get(0) + " );");
            stmt.close();
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
