package BabyBaby.Command.commands.Admin;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;

import BabyBaby.Command.AdminCMD;
import BabyBaby.Command.CommandContext;
import BabyBaby.data.data;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.Role;

public class removeRoles implements AdminCMD{

    @Override
    public void handleOwner(CommandContext ctx) {
        handleAdmin(ctx);
    }

    @Override
    public MessageEmbed getOwnerHelp(String prefix) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getName() {
        return "removeroles";
    }

    @Override
    public void handleAdmin(CommandContext ctx) {

            
            List<String> cmds = ctx.getArgs();

            Member silenced = ctx.getMember();
            MessageChannel channel = ctx.getChannel();
            List<Role> begone = silenced.getRoles();
            LinkedList<Role> rolewithPerm = new LinkedList<>();
            List<Role> bot = ctx.getGuild().getMemberById("").getRoles();
            Role highestbot = null;
            for (Role var : begone) {
                highestbot = var;
                break;
            }
            String roleIds = "";
            for (Role var : begone) {
                if(var.hasPermission()){
                    if(var.getPosition()< highestbot.getPosition()){
                        channel.sendMessage("Sry you have a higher Role, than this bot with viewing permissions. Can't take your roles away").queue();
                        return;
                    }
                    rolewithPerm.add(var);
                    roleIds += var.getId() + " ";
                }
            }

            Connection c = null;
            Statement stmt = null;
            
            try {
                Class.forName("org.sqlite.JDBC");
                c = DriverManager.getConnection(data.db);
                c.setAutoCommit(false);
                System.out.println("Opened database successfully");
                
                for (Role var : rolewithPerm) {
                    
                }

                stmt = c.createStatement();
                String sql = "INSERT INTO USERHASROLE (USERID, GUILDID, ROLES) " +
                                "VALUES ('" + ctx.getMember().getId() + "', '" + ctx.getGuild().getId() + "', '"+ cmds.get(0) + "');";
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
    public MessageEmbed getAdminHelp(String prefix) {
        // TODO Auto-generated method stub
        return null;
    }
    
}
