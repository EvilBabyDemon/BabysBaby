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

public class UpdateRoleCMD implements AdminCMD {

    @Override
    public String getName() {
        return "updaterole";
    }

    @Override
    public void handleAdmin(CommandContext ctx) {
        List<String> cmds2 = ctx.getArgs();
        MessageChannel channel = ctx.getChannel();
        Connection c = null;
        Statement stmt = null;

        LinkedList<String> cmds = new LinkedList<>();

        for (String var : cmds2) {
            cmds.add(var);
        }

        String casing = cmds.remove(0);
        String id = cmds.remove(0);
        String update = "";
        for (String var : cmds) {
            update += var + " "; 
        }
        

        //ASSIGNROLES ID categories emote
        try {
            update = update.substring(0, update.length()-1);
        } catch (Exception e) {
            ctx.getChannel().sendMessage("You forgot an arg.").queue();
            return;
        }
        //TODO add new HashSet and also old HashMap to update
        switch(casing){
            case "emote":
                try {
                    Class.forName("org.sqlite.JDBC");
                    c = DriverManager.getConnection(Data.db);
                    update = update.replace("<", "");
                    update = update.replace(">", "");
                    stmt = c.createStatement();
                    String sql= "UPDATE ASSIGNROLES SET EMOTE = '" + update + "' where ID=" + id + ";";
                    stmt.executeUpdate(sql); 
                    
                    stmt.close();
                    c.close();
                } catch ( Exception e ) {
                    channel.sendMessage(e.getClass().getName() + ": " + e.getMessage()).queue();
                    return;
                }
                String oldemo = "";
                for (String var : Data.emoteassign.keySet()) {
                    if(Data.emoteassign.get(var).equals(id)){
                        oldemo = var;
                        break;
                    }
                }
                Data.emoteassign.remove(oldemo);
                Data.emoteassign.put(update, id);


                break;
            
            case "id":
                
                try {
                    Class.forName("org.sqlite.JDBC");
                    c = DriverManager.getConnection(Data.db);
                    
                    stmt = c.createStatement(); 
                    String sql = "UPDATE ASSIGNROLES SET ID = " + update + " where ID=" + id + ";";
                    stmt.executeUpdate(sql);
            
                    stmt.close();
                    c.close();
                } catch ( Exception e ) {
                    channel.sendMessage(e.getClass().getName() + ": " + e.getMessage()).queue();
                    return;
                }
                Data.roles.remove(update);
                Data.roles.add(id);


                break;
            
            case "category":
                
                try {
                    Class.forName("org.sqlite.JDBC");
                    c = DriverManager.getConnection(Data.db);
            
                    stmt = c.createStatement();
                    String sql = "UPDATE ASSIGNROLES SET categories = '" + update + "' where ID=" + id + ";";
                    stmt.executeUpdate(sql);
            
                    stmt.close();
                    c.close();
                } catch ( Exception e ) {
                    channel.sendMessage(e.getClass().getName() + ": " + e.getMessage()).queue();
                    return;
                }
                
                break;
            default:
                ctx.getMessage().addReaction(Data.xmark).queue();
                return;
        }	
    
        ctx.getMessage().addReaction(Data.check).queue();
        
    }

    @Override
    public MessageEmbed getAdminHelp(String prefix) {
        return StandardHelp.Help(prefix, getName(), "<id/category/emote> <roleID atm> <new emote/id/...>", "Command to update the assignable roles if a emote or something changed.");
    }
    
}
