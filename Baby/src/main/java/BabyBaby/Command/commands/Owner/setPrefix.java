package BabyBaby.Command.commands.Owner;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;

import BabyBaby.Command.CommandContext;
import BabyBaby.Command.OwnerCMD;
import BabyBaby.Command.StandardHelp;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;

public class setPrefix implements OwnerCMD{

    @Override
    public String getName() {
        return "setprefix";
    }

    @Override
    public void handleOwner(CommandContext ctx) {

        List<String> cmds = ctx.getArgs();

        Connection c = null;
        Statement stmt = null;
        MessageChannel channel = ctx.getChannel();

        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:testone.db");
            c.setAutoCommit(false);
            System.out.println("Opened database successfully");
            
            stmt = c.createStatement();
            String sql = "INSERT INTO GUILD (ID,PREFIX) " +
                            "VALUES (" + ctx.getGuild().getId() + ", '"+ cmds.get(0) + "');";
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
    public MessageEmbed getOwnerHelp(String prefix) {
        return StandardHelp.Help(prefix, getName(), "<prefix>", "To set the Prefix of this server");
    }
    
}
