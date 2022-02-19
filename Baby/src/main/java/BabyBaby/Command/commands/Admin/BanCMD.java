package BabyBaby.Command.commands.Admin;

import java.util.LinkedList;

import BabyBaby.Command.IAdminCMD;
import BabyBaby.Command.CommandContext;
import BabyBaby.Command.StandardHelp;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;

public class BanCMD implements IAdminCMD{

    @Override
    public String getName() {
        return "ban";
    }

    @Override
    public void handleAdmin(CommandContext ctx) {
        LinkedList<String> cmds = new LinkedList<>();
        MessageChannel channel = ctx.getChannel();

        
        if(!ctx.getMember().hasPermission(Permission.BAN_MEMBERS)){
            channel.sendMessage("Missing Permissions.").complete();
            return;
        }

        for (String arg : ctx.getArgs()) {
            cmds.add(arg);
        }

        String person = cmds.remove(0);

        String reason = "";
        if(cmds.size() != 0){
            reason = ctx.getMessage().getContentRaw().substring(getName().length() + person.length()+3);
        }

        person = person.replace("<", "");
        person = person.replace(">", "");
        person = person.replace("!", "");
        person = person.replace("@", "");

        Member bad;
        try {
            bad = ctx.getGuild().getMemberById(person);
        } catch (Exception e) {
            channel.sendMessage("This is no Member").complete();
            return;
        }
        

        if(bad.getRoles().get(0).getPosition() >= ctx.getMember().getRoles().get(0).getPosition()){
            channel.sendMessage("Can't ban someone with a higher or same role.").complete();
            return;
        }
                

        if(reason==""){
            bad.ban(0).complete();
        } else {
            bad.ban(0, reason).complete();
        }

        EmbedBuilder eb = new EmbedBuilder();
        eb.setAuthor(ctx.getAuthor().getAsTag() + " (" + ctx.getAuthor().getId() + ")", ctx.getAuthor().getAvatarUrl(), ctx.getAuthor().getAvatarUrl());
        eb.setColor(0);
        eb.setThumbnail(ctx.getAuthor().getAvatarUrl());
        Member warned = ctx.getMember();
        eb.setDescription(":warning: **Banned** " + warned.getAsMention() + "(" + warned.getUser().getAsTag() +")"+ " \n :page_facing_up: **Reason:** " + reason);
        channel.sendMessageEmbeds(eb.build()).queue();
    }

    @Override
    public MessageEmbed getAdminHelp(String prefix) {
        return StandardHelp.Help(prefix, getName(), "<User Ping> [Reason]", "Command to ban a person.");
    }
    
}
