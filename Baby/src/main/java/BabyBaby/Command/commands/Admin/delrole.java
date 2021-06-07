package BabyBaby.Command.commands.Admin;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.List;


import BabyBaby.Command.AdminCMD;
import BabyBaby.Command.CommandContext;
import BabyBaby.Command.StandardHelp;
import BabyBaby.data.data;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;

public class delrole implements AdminCMD {

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
        return "delrole";
    }

    @Override
    public void handleAdmin(CommandContext ctx) {
        List<String> cmds = ctx.getArgs();
        if(cmds.size()==0){
            ctx.getChannel().sendMessage("gib args").queue();
            return;
        }
        

        if(!data.roles.contains(cmds.get(0))){
            ctx.getChannel().sendMessage("Doesnt exist sry").queue();
            return;
        }
        data.roles.remove(cmds.get(0));
        for (String var : data.emoteassign.keySet()) {
            if(data.emoteassign.get(var).equals(cmds.get(0))){
                data.emoteassign.remove(var);
                break;
            }
        }


        MessageChannel channel = ctx.getChannel();
        Connection c = null;
        Statement stmt = null;



        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection(data.db);
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

        ctx.getMessage().addReaction(data.check).queue();
        
    }

    @Override
    public MessageEmbed getAdminHelp(String prefix) {
        return StandardHelp.Help(prefix, getName(), "<RoleID>", "Command to delete an assignable role. (Not the role itself obv.)");
    }
    
}