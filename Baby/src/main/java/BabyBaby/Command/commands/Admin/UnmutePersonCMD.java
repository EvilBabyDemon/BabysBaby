package BabyBaby.Command.commands.Admin;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.LinkedList;
import java.util.concurrent.ScheduledExecutorService;

import BabyBaby.Command.AdminCMD;
import BabyBaby.Command.CommandContext;
import BabyBaby.Command.StandardHelp;
import BabyBaby.data.Data;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.Role;

public class UnmutePersonCMD implements AdminCMD {

    @Override
    public String getName() {
        return "unmute";
    }

    @Override
    public void handleAdmin(CommandContext ctx) {
        if(!ctx.getGuild().getId().equals(Data.ethid))
            return;        

        LinkedList<String> cmds = new LinkedList<>();

        for (String arg : ctx.getArgs()) {
            cmds.add(arg);
        }
        
        String person = cmds.remove(0);
        
        person = person.replace("<", "");
        person = person.replace(">", "");
        person = person.replace("!", "");
        person = person.replace("@", "");

        try {
            if(!MutePersonCMD.userMuted.containsKey(ctx.getGuild().getMemberById(person))){
                System.out.println("Not in the Map.");
                return;
            }
        } catch (Exception e) {
            ctx.getChannel().sendMessage("This is not a snowflake ID or this user is not on this server.").queue();
            return;
        }
        


        MessageChannel log = ctx.getGuild().getTextChannelById(Data.modlog);

        EmbedBuilder eb = new EmbedBuilder();
        eb.setAuthor(ctx.getAuthor().getAsTag() + " (" + ctx.getAuthor().getId() + ")", ctx.getAuthor().getAvatarUrl(), ctx.getAuthor().getAvatarUrl());
        eb.setColor(0);
        eb.setThumbnail(ctx.getGuild().getMemberById(person).getUser().getAvatarUrl());
        Member warned = ctx.getGuild().getMemberById(person);

        eb.setDescription(":loud_sound: **Unmuted for** " + warned.getAsMention() + "(" + warned.getUser().getAsTag() +")"+ " \n :page_facing_up: **Reason:** Manually unmuted with CMD");

        log.sendMessageEmbeds(eb.build()).queue();

        //ctx.getChannel().sendMessage(eb.build()).queue();


        Role muteR = ctx.getGuild().getRoleById(Data.stfuID);

        ctx.getGuild().removeRoleFromMember(warned, muteR).queue();

        Connection c = null;
        PreparedStatement stmt = null;

        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection(Data.db);
            
            stmt = c.prepareStatement("DELETE FROM ADMINMUTE WHERE USERID = ? AND GUILDID = ?;");
            stmt.setString(1, warned.getId());
            stmt.setString(2, ctx.getGuild().getId());
            stmt.execute();
            stmt.close();
            c.close();
        } catch ( Exception e ) {
            e.printStackTrace(); 
            return;
        }
        
        

        if(MutePersonCMD.userMuted.get(warned)==null){
            MutePersonCMD.userMuted.remove(warned);
        } else {
            ScheduledExecutorService stopper = MutePersonCMD.userMuted.remove(warned);
            stopper.shutdown();
        }

        ctx.getMessage().addReaction(Data.check).queue();

    }

    @Override
    public MessageEmbed getAdminHelp(String prefix) {
        return StandardHelp.Help(prefix, getName(), "<User Ping>", "Command to unmute a person.");
    }
    
}
