package BabyBaby.Command.commands.Public;

import java.util.ArrayList;

import BabyBaby.Command.CommandContext;
import BabyBaby.Command.IPublicCMD;
import BabyBaby.Command.StandardHelp;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;

public class BotsOnlineCMD implements IPublicCMD {

    @Override
    public String getName() {
        return "online";
    }

    @Override
    public void handlePublic(CommandContext ctx) {
        ArrayList<Member> online = new ArrayList<>();
        ArrayList<Member> offline = new ArrayList<>();
        for (Member member : ctx.getGuild().getMembers()) {
            if (member.getUser().isBot()) {
                OnlineStatus stat = member.getOnlineStatus();
                if (stat.equals(OnlineStatus.OFFLINE)) {
                    offline.add(member);
                } else {
                    online.add(member);
                }
            }
        }

        String on = "";
        String off = "";
        String pingstr = "";

        for (Member onMember : online) {
            on += onMember.getAsMention() + "\n";
            pingstr += onMember.getAsMention() + " ";
        }

        for (Member offMember : offline) {
            off += offMember.getAsMention() + "\n";
            pingstr += offMember.getAsMention() + " ";
        }

        Message ping = ctx.getChannel().sendMessage("wait").complete();
        ping.editMessage(pingstr).complete().delete().queue();

        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Bots on this server:");
        eb.addField("Online Bots: " + online.size(), on, true);
        eb.addField("Offline Bots: " + offline.size(), off, true);

        String nickname = (ctx.getMember().getNickname() != null) ? ctx.getMember().getNickname()
                : ctx.getMember().getEffectiveName();
        eb.setFooter("Summoned by: " + nickname, ctx.getAuthor().getAvatarUrl());
        ctx.getChannel().sendMessageEmbeds(eb.build()).complete();
    }

    @Override
    public MessageEmbed getPublicHelp(String prefix) {
        return StandardHelp.Help(prefix, getName(), "", "Get all bots and see if they are online or not");
    }

}
