package BabyBaby.Command.commands.Admin;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.List;


import BabyBaby.Command.IAdminCMD;
import BabyBaby.Command.CommandContext;
import BabyBaby.Command.StandardHelp;
import BabyBaby.data.Data;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;

public class DelRoleCMD implements IAdminCMD {

    @Override
    public String getName() {
        return "delrole";
    }

    @Override
    public void handleAdmin(CommandContext ctx) {
        List<String> cmds = ctx.getArgs();
        if(cmds.size()==0){
            ctx.getChannel().sendMessage("gib args").queue();
            return;
        }
        

        if(!Data.roles.contains(cmds.get(0))){
            ctx.getChannel().sendMessage("Doesnt exist sry").queue();
            return;
        }
        Data.roles.remove(cmds.get(0));
        for (String emoteID : Data.emoteassign.keySet()) {
            if(Data.emoteassign.get(emoteID).equals(cmds.get(0))){
                Data.emoteassign.remove(emoteID);
                break;
            }
        }


        MessageChannel channel = ctx.getChannel();
        Connection c = null;
        Statement stmt = null;



        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection(Data.db);
            c.setAutoCommit(false);
    
            stmt = c.createStatement();
            String sql = "DELETE from ASSIGNROLES where ID=" + cmds.get(0) + ";";
            stmt.executeUpdate(sql);
            c.commit();
                
            stmt.close();
            c.close();
        } catch ( Exception e ) {
            channel.sendMessage(e.getClass().getName() + ": " + e.getMessage()).queue();
            return;
        }

        ctx.getMessage().addReaction(Data.check).queue();
        
    }

    @Override
    public MessageEmbed getAdminHelp(String prefix) {
        return StandardHelp.Help(prefix, getName(), "<RoleID>", "Command to delete an assignable role. (Not the role itself obv.)");
    }
    
}