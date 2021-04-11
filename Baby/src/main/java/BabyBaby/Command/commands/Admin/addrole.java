package BabyBaby.Command.commands.Admin;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;

import BabyBaby.Command.AdminCMD;
import BabyBaby.Command.CommandContext;
import BabyBaby.Command.StandardHelp;
import BabyBaby.data.data;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;

public class addrole implements AdminCMD {

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
        return "addrole";
    }

    @Override
    public void handleAdmin(CommandContext ctx) {
        List<String> cmds2 = ctx.getArgs();
        LinkedList<String> cmds = new LinkedList<>();
        for (String var : cmds2) {
            cmds.add(var);
        }


        String id = cmds.remove(0);
        String emote = cmds.remove(0);
        emote = emote.replace("<", "");
        emote = emote.replace(">", "");
        MessageChannel channel = ctx.getChannel();
        Connection c = null;
        Statement stmt = null;
        String categ = "";
        if(cmds.size() == 0){
            categ = "Other";
        } else {
            for (String var : cmds) {
                categ += var + " "; 
            }
            categ = categ.substring(0, categ.length()-1);
        }
        

        try { 	
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:testone.db");

            stmt = c.createStatement();
            String sql = "INSERT INTO ASSIGNROLES (ID,CATEGORIES,EMOTE) " +
                            "VALUES (" + id + ", '" + categ + "', '" + emote + "');"; 
            stmt.executeUpdate(sql);

            stmt.close();
            c.close();
        } catch ( Exception e ) {
            channel.sendMessage(e.getClass().getName() + ": " + e.getMessage()).queue();
            return;
        }

        data.emoteassign.put(emote, id);

        ctx.getMessage().addReaction(":checkmark:769279808244809798").queue();
        
    }

    @Override
    public MessageEmbed getAdminHelp(String prefix) {
        return StandardHelp.Help(prefix, getName(), "<RoleID> <emote> [category]", "Command to add a selfassignable role.");
    }

}
