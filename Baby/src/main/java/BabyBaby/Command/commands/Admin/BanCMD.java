package BabyBaby.Command.commands.Admin;

import java.util.LinkedList;

import BabyBaby.Command.AdminCMD;
import BabyBaby.Command.CommandContext;
import BabyBaby.Command.StandardHelp;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;

public class BanCMD implements AdminCMD{

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
        return "ban";
    }

    @Override
    public void handleAdmin(CommandContext ctx) {
        LinkedList<String> cmds = new LinkedList<>();
        MessageChannel channel = ctx.getChannel();

        for (String var : ctx.getArgs()) {
            cmds.add(var);
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

        Member bad = ctx.getGuild().getMemberById(person);

        if(reason==""){
            bad.kick().complete();
        } else {
            bad.kick(reason).complete();
        }

        EmbedBuilder eb = new EmbedBuilder();
        eb.setAuthor(ctx.getAuthor().getAsTag() + " (" + ctx.getAuthor().getId() + ")", ctx.getAuthor().getAvatarUrl(), ctx.getAuthor().getAvatarUrl());
        eb.setColor(0);
        eb.setThumbnail(ctx.getAuthor().getAvatarUrl());
        Member warned = ctx.getMember();
        eb.setDescription(":warning: **Banned** " + warned.getAsMention() + "(" + warned.getUser().getAsTag() +")"+ " \n :page_facing_up: **Reason:** " + reason);
        channel.sendMessage(eb.build()).queue();
    }

    @Override
    public MessageEmbed getAdminHelp(String prefix) {
        return StandardHelp.Help(prefix, getName(), "<User Ping> [Reason]", "Command to ban a person.");
    }
    
}
