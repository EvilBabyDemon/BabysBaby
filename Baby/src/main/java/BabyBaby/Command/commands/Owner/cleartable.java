package BabyBaby.Command.commands.Owner;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

import BabyBaby.Command.CommandContext;
import BabyBaby.Command.OwnerCMD;
import BabyBaby.Command.StandardHelp;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;

public class cleartable implements OwnerCMD{

    @Override
    public String getName() {
        return "cleartable";
    }

    @Override
    public void handleOwner(CommandContext ctx) {
        Connection c = null;
        Statement stmt = null;
        MessageChannel channel = ctx.getChannel();
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:testone.db");
            c.setAutoCommit(false);
            System.out.println("Opened database successfully");
            
            stmt = c.createStatement();

            stmt.executeQuery("DELETE FROM" + ctx.getArgs().get(0) + ";");
            
            stmt.close();
            c.close();
            } catch ( Exception e ) {
            channel.sendMessage(e.getClass().getName() + ": " + e.getMessage()).queue();
            return;
            }
        System.out.println("Get done successfully");

        channel.deleteMessageById(ctx.getMessage().getId()).queue();
        
    }

    @Override
    public MessageEmbed getOwnerHelp(String prefix) {
        return StandardHelp.Help(prefix, getName(), "<tablename>", "Command to clear all entries in a table.");
    }
    
}
