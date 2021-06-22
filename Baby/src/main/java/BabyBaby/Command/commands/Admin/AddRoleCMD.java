package BabyBaby.Command.commands.Admin;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;

import BabyBaby.Command.AdminCMD;
import BabyBaby.Command.CommandContext;
import BabyBaby.Command.StandardHelp;
import BabyBaby.data.Data;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;

public class AddRoleCMD implements AdminCMD {



    @Override
    public String getName() {
        return "addrole";
    }

    @Override
    public void handleAdmin(CommandContext ctx) {
        LinkedList<String> cmds = new LinkedList<>();
        for (String arg : ctx.getArgs()) {
            cmds.add(arg);
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
            for (String strCateg : cmds) {
                strCateg += strCateg + " "; 
            }
            categ = categ.substring(0, categ.length()-1);
        }
        

        try { 	
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection(Data.db);

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

        Data.emoteassign.put(emote, id);

        ctx.getMessage().addReaction(":checkmark:769279808244809798").queue();
        
    }

    @Override
    public MessageEmbed getAdminHelp(String prefix) {
        return StandardHelp.Help(prefix, getName(), "<RoleID> <emote> [category]", "Command to add a selfassignable role.");
    }

}
