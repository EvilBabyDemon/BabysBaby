package BabyBaby.Command.commands.Public;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;

import BabyBaby.Command.CommandContext;
import BabyBaby.Command.PublicCMD;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;

public class reminder implements PublicCMD {

    @Override
    public void handleAdmin(CommandContext ctx) {
        handlePublic(ctx);
    }

    @Override
    public MessageEmbed getAdminHelp(String prefix) {
        return getPublicHelp(prefix);
    }

    @Override
    public void handleOwner(CommandContext ctx) {
        handlePublic(ctx);
    }

    @Override
    public MessageEmbed getOwnerHelp(String prefix) {
        return getPublicHelp(prefix);
    }

    @Override
    public String getName() {
        return "reminder";
    }

    @Override
    public void handlePublic(CommandContext ctx) {
        Connection c = null;
        Statement stmt = null;
        MessageChannel channel = ctx.getChannel();
        List<String> cmds = ctx.getArgs();;
        String text = "";
        String time = cmds.remove(0);
        
        for (String var : cmds) {
            text += var + " ";
        }



        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:testone.db");
            c.setAutoCommit(false);
            System.out.println("Opened database successfully");
            
            stmt = c.createStatement();
            String sql = "INSERT INTO REMINDERS (USERID,TEXTS, GUILDID, CHANNELID, TIME) " +
                            "VALUES ('" + ctx.getMember().getId() + "', '" + text + 
                            ctx.getGuild().getId() + "', '" + ctx.getChannel().getId() +"', " + time + "');";
            ResultSet rs = stmt.executeQuery(sql);


            
            rs.close();
            stmt.close();
            c.close();
        } catch ( Exception e ) {
            channel.sendMessage(e.getClass().getName() + ": " + e.getMessage()).queue();
            return;
        }
        
    }

    @Override
    public MessageEmbed getPublicHelp(String prefix) {
        // TODO Auto-generated method stub
        return null;
    }
    
}
