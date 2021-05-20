package BabyBaby.data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.LinkedList;
import java.util.concurrent.ScheduledExecutorService;

import BabyBaby.Command.commands.Public.RemoveRoles;
import BabyBaby.Command.commands.Public.RemoveRolesForce;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
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

        for (String var : roles.split(" ")) {
            try {
                addRole.add(guild.getRoleById(var));
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
        

        ScheduledExecutorService blindex = RemoveRoles.blind.get(mem);
        
        RemoveRolesForce.force.remove(RemoveRoles.blindexe.get(blindex));

        RemoveRoles.blindexe.remove(blindex);
        blindex.shutdownNow();
        RemoveRoles.blind.remove(mem);



        Connection c = null;
        PreparedStatement stmt = null;
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection(data.db);
            
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
            blind.openPrivateChannel().complete().sendMessage("You shall see light again! Hope this worked... **I advise to press CTRL + R to reload Discord as you may not see some messages else!**").queue();
        } catch (Exception e){
            System.out.println("Author didn't allow private message.");
        }
        
        
    }
}