package BabyBaby.Command.commands.Admin;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import BabyBaby.Command.AdminCMD;
import BabyBaby.Command.CommandContext;
import BabyBaby.Command.StandardHelp;
import BabyBaby.data.data;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.Role;

public class UnmutePersonCMD implements AdminCMD {
    public static HashMap<Member, ScheduledExecutorService> userMuted = new HashMap<>();
    public static HashMap<ScheduledExecutorService, GetUnmutePerson> variables = new HashMap<>();


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
        return "unmute";
    }

    @Override
    public void handleAdmin(CommandContext ctx) {
        if(!ctx.getGuild().getId().equals(data.ethid))
            return;
        
        LinkedList<String> cmds = new LinkedList<>();

        for (String var : ctx.getArgs()) {
            cmds.add(var);
        }
        

        String person = cmds.remove(0);
        
        person = person.replace("<", "");
        person = person.replace(">", "");
        person = person.replace("!", "");
        person = person.replace("@", "");


        MessageChannel log = ctx.getGuild().getTextChannelById(data.modlog);

        EmbedBuilder eb = new EmbedBuilder();
        eb.setAuthor(ctx.getAuthor().getAsTag() + " (" + ctx.getAuthor().getId() + ")", ctx.getAuthor().getAvatarUrl(), ctx.getAuthor().getAvatarUrl());
        eb.setColor(0);
        eb.setThumbnail(ctx.getGuild().getMemberById(person).getUser().getAvatarUrl());
        Member warned = ctx.getGuild().getMemberById(person);

        eb.setDescription(":loud_sound: **Unmuted for** " + warned.getAsMention() + "(" + warned.getUser().getAsTag() +")"+ " \n :page_facing_up: **Reason:** Manually unmuted with CMD");

        log.sendMessage(eb.build()).queue();

        //ctx.getChannel().sendMessage(eb.build()).queue();


        Role muteR = ctx.getGuild().getRoleById(data.stfuID);

        ctx.getGuild().removeRoleFromMember(warned, muteR).queue();

        Connection c = null;
        PreparedStatement stmt = null;

        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection(data.db);
            
            stmt = c.prepareStatement("DELETE FROM ADMINMUTE WHERE USER = ?;");
            stmt.setString(1, warned.getId());
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
            MutePersonCMD.variables.remove(MutePersonCMD.userMuted.get(warned));
            MutePersonCMD.userMuted.remove(warned);
        }

        ctx.getMessage().addReaction(data.check).queue();

    }

    @Override
    public MessageEmbed getAdminHelp(String prefix) {
        return StandardHelp.Help(prefix, getName(), "<User Ping>", "Command to unmute a person.");
    }
    
}
