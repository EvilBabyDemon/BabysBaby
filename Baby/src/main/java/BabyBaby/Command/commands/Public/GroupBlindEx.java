package BabyBaby.Command.commands.Public;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import BabyBaby.data.data;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildChannel;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.PrivateChannel;
import net.dv8tion.jda.api.entities.Role;

public class GroupBlindEx implements Runnable {
    public int id;
    public Guild guild;
    public boolean switcher;
    public int breaktime;
    public int learntime;
    public int counter;

    public GroupBlindEx(int id, Guild tempG, boolean b, int bt, int lt, int c) {
        this.id = id;
        guild = tempG;
        switcher = b;
        breaktime = bt;
        learntime = lt;
        counter = c;
    }

    public void run() {
        ArrayList<String> group = new ArrayList<>(BlindGroupCMD.groups.get(id));
        if(group.size()==0){
            counter++;
        } else {
            counter = 0;
        }
        GroupBlindEx blindEx = new GroupBlindEx(id, guild, !switcher, breaktime, learntime, counter);

        if(switcher){
            LinkedList<GuildChannel> gchan = new LinkedList<>();

            if(counter > 5){
                BlindGroupCMD.groups.remove(id);
                System.out.println("Removed old group" + id);
                return;
            }

            for (GuildChannel var : guild.getChannels()) {
                if(!var.getId().equals("769261792491995176") && !var.getId().equals("815881148307210260") && var.getParent() != null){
                    gchan.add(var);
                }
            }

            main: for (int i = 0; i < group.size(); i++) {
                try {
                    Role highestbot = guild.getSelfMember().getRoles().get(0);
                    Member blinded = guild.getMemberById(group.get(0));
                    List<Role> begone = blinded.getRoles();
                    LinkedList<Role> permrole = new LinkedList<>();

                    PrivateChannel priv = blinded.getUser().openPrivateChannel().complete();

                    for (Role var : begone) {
                        for (GuildChannel var2 : gchan) {
                            if(var.hasAccess(var2)){
                                if(var.getPosition()>=highestbot.getPosition()){
                                    priv.sendMessage("Sry you have a higher Role than this bot with viewing permissions. Can't take your roles away").queue();
                                    continue main;
                                }
                                permrole.add(var);
                                break;
                            }
                        }
                    }


                    Connection c = null;
                    PreparedStatement stmt = null;
                    String role = "";

                    try {
                        Class.forName("org.sqlite.JDBC");
                        c = DriverManager.getConnection(data.db);
                        
                        for (Role var : permrole) {
                            role += var.getId() + " ";
                        }

                        stmt = c.prepareStatement("INSERT INTO ROLEREMOVAL (USERID, GUILDID, MUTETIME, ROLES, ADMINMUTE) VALUES (?, ?, ?, ?, ?);");
                        stmt.setString(1, blinded.getId());
                        stmt.setString(2, guild.getId());
                        stmt.setString(3, System.currentTimeMillis() + breaktime*1000*60 + "");
                        stmt.setString(4, role);
                        stmt.setString(5, "false");
                        
                        stmt.executeUpdate();

                        stmt.close();
                        c.close();
                    } catch ( Exception e ) {
                        System.out.println(e.getClass().getName() + ": " + e.getMessage());
                        group.remove(i--);
                        continue main;
                    }

                    
                    LinkedList<Role> tmp = new LinkedList<>();
                    try {
                        tmp.add(guild.getRoleById("844136589163626526"));
                    } catch (Exception e) {
                        System.out.println("Role Blind doesnt exist anymore. This could be a serious issue.");
                    }
                    

                    RemoveRoles.blind.put(blinded, null);
                    guild.modifyMemberRoles(blinded, tmp, permrole).complete();
                    try {
                        priv.sendMessage("You got blinded for ~" + learntime + " minutes. Either wait out the timer or write me here \n+" + new UnblindCMD().getName()+ ". This will kick you from the group.").queue();
                    } catch (Exception e) {
                        System.out.println("Author didn't allow private message.");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    group.remove(i--);
                }
            }

            BlindGroupCMD.skedula.schedule(blindEx, learntime, TimeUnit.MINUTES);
            BlindGroupCMD.groups.put(id, group);

            return;
        } 

        main2: for (int i = 0; i < group.size(); i++) {
            try {
                Member blinded = guild.getMemberById(group.get(0));

                String roles = "";
                
                Connection c = null;
                PreparedStatement stmt = null;
                try {
                    Class.forName("org.sqlite.JDBC");
                    c = DriverManager.getConnection(data.db);
                    
                    stmt = c.prepareStatement("SELECT ROLES FROM ROLEREMOVAL WHERE USERID = ? AND GUILDID = ?;");
                    stmt.setString(1, blinded.getId());
                    stmt.setString(2, guild.getId());
                    ResultSet rs = stmt.executeQuery();

                    roles = rs.getString("ROLES");

                    stmt.close();
                    c.close();
                } catch ( Exception e ) {
                    System.out.println("ERROR");
                    e.printStackTrace();
                    group.remove(i--);
                    continue main2;
                }


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
                guild.modifyMemberRoles(blinded, addRole, delRole).complete();
                RemoveRoles.blind.remove(blinded);
                
                try {
                    Class.forName("org.sqlite.JDBC");
                    c = DriverManager.getConnection(data.db);
                    
                    stmt = c.prepareStatement("DELETE FROM ROLEREMOVAL WHERE USERID = ? AND GUILDID = ?;");
                    stmt.setString(1, blinded.getId());
                    stmt.setString(2, guild.getId());
                    stmt.execute();
                    stmt.close();
                    c.close();
                } catch ( Exception e ) {
                    e.printStackTrace();
                    group.remove(i--);
                    continue main2;
                }
                try {
                    blinded.getUser().openPrivateChannel().complete().sendMessage("Its break time! <:yay:778745219733520426> \nYou shall see light again! \n "+ 
                    "If you were only shortly blinded: **I advise to press CTRL + R to reload Discord as you may not see some messages else!**\n "+
                    "If you were blinded for a long time: **Right click on the server and click \"Mark read All\" or Shift + ESC**").queue();
                } catch (Exception e) {
                    System.out.println("Author didn't allow private message."); 
                }
                
            } catch (Exception e) {
                e.printStackTrace();
                group.remove(i--);
            }
        }
        BlindGroupCMD.groups.put(id, group);
        BlindGroupCMD.skedula.schedule(blindEx, breaktime, TimeUnit.MINUTES);
        
    }
}