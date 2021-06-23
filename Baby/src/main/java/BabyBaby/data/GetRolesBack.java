package BabyBaby.data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.LinkedList;
import java.util.concurrent.ScheduledExecutorService;

import BabyBaby.Command.commands.Admin.AdminMuteBlindCMD;
import BabyBaby.Command.commands.Public.BlindCMD;
import BabyBaby.Command.commands.Public.BlindForceCMD;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;

public class GetRolesBack implements Runnable {
    public User blind;
    public Guild guild;
    public String roles;

    public GetRolesBack(User user, Guild tempG, String s) {
        blind = user;
        guild = tempG;
        roles = s;
    }

    public void run() {	
        
        Member mem = guild.getMember(blind);

        LinkedList<Role> addRole = new LinkedList<>();
        LinkedList<Role> delRole = new LinkedList<>();

        for (String roleID : roles.split(" ")) {
            try {
                addRole.add(guild.getRoleById(roleID));
            } catch (Exception e) {
                System.out.println("Role doesnt exist anymore");
            }
        }

        try {
            delRole.add(guild.getRoleById("844136589163626526"));
        } catch (Exception e) {
            System.out.println("Role Blind doesnt exist anymore. This could be a serious issue.");
        }

        guild.modifyMemberRoles(mem, addRole, delRole).complete();
        

        ScheduledExecutorService blindex = BlindCMD.blind.get(mem);
        
        BlindForceCMD.force.remove(BlindCMD.blindexe.get(blindex));

        BlindCMD.blindexe.remove(blindex);
        BlindCMD.blind.remove(mem);


        if(AdminMuteBlindCMD.userBlinded.contains(mem)){

            MessageChannel log = guild.getTextChannelById(Data.modlog);
            
            EmbedBuilder eb = new EmbedBuilder();
            eb.setAuthor(guild.getSelfMember().getUser().getAsTag() + " (" + guild.getSelfMember().getId() + ")", guild.getSelfMember().getUser().getAvatarUrl(), guild.getSelfMember().getUser().getAvatarUrl());
            eb.setColor(0);
            eb.setThumbnail(mem.getUser().getAvatarUrl());

            eb.setDescription(":warning: **Muteblind** " + " stopped for " + mem.getAsMention() + "(" + mem.getUser().getAsTag() +")"+ " \n :page_facing_up: **Reason:** " + "Time ran out");

            log.sendMessage(eb.build()).queue();

            AdminMuteBlindCMD.userBlinded.remove(mem);
        }



        Connection c = null;
        PreparedStatement stmt = null;
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection(Data.db);
            
            stmt = c.prepareStatement("DELETE FROM ROLEREMOVAL WHERE USERID = ? AND GUILDID = ?;");
            stmt.setString(1, blind.getId());
            stmt.setString(2, guild.getId());
            stmt.execute();
            stmt.close();
            c.close();
        } catch ( Exception e ) {
            e.printStackTrace(); 
            return;
        }
        try{
            blind.openPrivateChannel().complete().sendMessage("You shall see light again! \n"+ 
            "If you were only shortly blinded: **I advise to press CTRL + R to reload Discord as you may not see some messages else!**\n"+
            "If you were blinded for a long time: **Right click on the server and click \"Mark read All\" or Shift + ESC**").queue();
        } catch (Exception e){
            System.out.println("Author didn't allow private message.");
        }
        
        
    }
}