package BabyBaby.Command.commands.Public;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;

import BabyBaby.Command.CommandContext;
import BabyBaby.Command.PublicCMD;
import BabyBaby.Command.StandardHelp;
import BabyBaby.Command.commands.Admin.AdminMuteBlindCMD;
import BabyBaby.data.GetRolesBack;
import BabyBaby.data.data;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;

public class UnblindCMD implements PublicCMD {

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
        return "unblind";
    }

    @Override
    public void handlePublic(CommandContext ctx) {
        /*
        if(!MuteCMD.userMuted.containsKey(ctx.getAuthor())){
            ctx.getAuthor().openPrivateChannel().queue((channel) -> {
                channel.sendMessage("You were never blinded. Or at least not by this bot. If you were pls report this problem to Lukas").queue();
            });
            return;
        }
        */
        actualcmd(ctx.getAuthor(), ctx.getArgs());
    }

    public void privhandle(User author, List<String> args){
        actualcmd(author, args);
    }

    private void actualcmd(User author, List<String> cmds){
        /*
        if(author.getId().equals("177498563637542921")){
            author.openPrivateChannel().queue(privchannel -> {
                privchannel.sendMessage("Nope, not getting unmuted till the timer runs out. You did this to yourself.").queue();
            });
            return;
        }
        */
        
        String authorID = author.getId();
        boolean group = false;
        //remove from a group
        for (int ids : BlindGroupCMD.groups.keySet()) {
            ArrayList<String> var = BlindGroupCMD.groups.get(ids);
            if(var.contains(authorID)){
                var.remove(authorID);
                group = true;
                break;
            }
        }
        

        if(!group){
            LinkedList<GetRolesBack> vars = new LinkedList<>();        
            
            for (Member var : RemoveRoles.blind.keySet()) {
                if(var.getId().equals(authorID)){
                    vars.add(RemoveRoles.blindexe.get(RemoveRoles.blind.get(var)));
                }
            }
            GetRolesBack blindclass = null;
            if(cmds.size() == 0){
                switch(vars.size()){
                    case 0:
                        author.openPrivateChannel().queue(privchannel -> {
                            privchannel.sendMessage("You were never blinded. Or at least not by this bot. If you were pls report this problem to Lukas.").queue();
                        });
                        return;
                    case 1:
                        blindclass = vars.get(0);
                        break;
                    default:
                        author.openPrivateChannel().queue(privchannel -> {
                            privchannel.sendMessage("Pls use +" + getName() + " <key> as there are multiple servers you are blinded on. These are the keys:").queue();
                            for (GetRolesBack var : vars) {
                                privchannel.sendMessage("Key: " + var.guild.getId() + " " + var.guild.getName()).queue();
                            }
                            //TO DO FIX FOR multiple server
                        });
                        return;
                }
            } else {
                for (GetRolesBack var : vars) {
                    if(var.guild.getId().equals(cmds.get(0))){
                        blindclass = var;
                        break;
                    }
                }
            }
            

            


            Member mem = blindclass.guild.getMember(blindclass.blind);
            if(AdminMuteBlindCMD.userBlinded.contains(mem)){
                author.openPrivateChannel().complete().sendMessage("You got blinded by admins. You can't unblind yourself.").complete();
                return;
            }

            if(RemoveRolesForce.force.contains(blindclass)){
                author.openPrivateChannel().queue(privchannel -> {
                    privchannel.sendMessage("You did a Force Blind. That are the consequences to your actions. Do not contact the admins! If it is an emergency contact Lukas, same if it is pobably a Bug.").queue();
                });
                return;
            }

            Guild blindServ = blindclass.guild;
            Member blinded = blindServ.getMember(blindclass.blind);

            ScheduledExecutorService blind = RemoveRoles.blind.get(blinded);
            RemoveRoles.blindexe.remove(blind);
            blind.shutdownNow();
            RemoveRoles.blind.remove(blinded);
        }

        String roles = "";

        Connection c = null;
        PreparedStatement stmt = null;
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection(data.db);
            
            stmt = c.prepareStatement("SELECT ROLES FROM ROLEREMOVAL WHERE USERID = ? AND GUILDID = ?;");
            stmt.setString(1, authorID);
            stmt.setString(2, data.ethid);
            ResultSet rs = stmt.executeQuery();

            roles = rs.getString("ROLES");

            stmt.close();
            c.close();
        } catch ( Exception e ) {
            e.printStackTrace(); 
            return;
        }
        
        Guild blindServ = author.getJDA().getGuildById(data.ethid);
        Member blinded = blindServ.getMember(author);

        LinkedList<Role> addRole = new LinkedList<>();
        LinkedList<Role> delRole = new LinkedList<>();

        for (String var : roles.split(" ")) {
            try {
                addRole.add(blindServ.getRoleById(var));
            } catch (Exception e) {
                System.out.println("Role doesnt exist anymore");
            }
        }

        try {
            delRole.add(blindServ.getRoleById("844136589163626526"));
        } catch (Exception e) {
            System.out.println("Role Blind doesnt exist anymore. This could be a serious issue.");
        }

        blindServ.modifyMemberRoles(blinded, addRole, delRole).complete();
        
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection(data.db);
            
            stmt = c.prepareStatement("DELETE FROM ROLEREMOVAL WHERE USERID = ? AND GUILDID = ?;");
            stmt.setString(1, blinded.getId());
            stmt.setString(2, blindServ.getId());
            stmt.execute();
            stmt.close();
            c.close();
        } catch ( Exception e ) {
            e.printStackTrace(); 
            return;
        }
        
        try {
            author.openPrivateChannel().complete().sendMessage("You shall see light again! \n "+ 
            "If you were only shortly blinded: **I advise to press CTRL + R to reload Discord as you may not see some messages else!**\n "+
            " If you were blinded for a long time: **Right click on the server and click \"Mark read All\" or Shift + ESC**").queue();
        } catch (Exception e) {
            System.out.println("Author didn't allow private message.");
        }
    }

    @Override
    public MessageEmbed getPublicHelp(String prefix) {
        return StandardHelp.Help(prefix, getName(), "", "Command to unblind you early after using " + new MuteCMD().getName() + ".");
    }
    
    
}
