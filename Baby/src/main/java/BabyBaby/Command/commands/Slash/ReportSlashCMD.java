package BabyBaby.Command.commands.Slash;

import BabyBaby.Command.ISlashCMD;
import BabyBaby.data.Data;
import BabyBaby.data.Helper;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.internal.interactions.CommandDataImpl;

public class ReportSlashCMD implements ISlashCMD {

    @Override
    public String getName() {
        return "report";
    }

    @Override
    public void handle(SlashCommandInteractionEvent event, InteractionHook hook, boolean failed) {
        String issue = "Report:\n" + event.getOption("issue").getAsString();
        Member member = event.getOption("user", OptionMapping::getAsMember);
        if(member != null){
            issue += " (Accused user: " + member.getAsMention() + ")"; 
        }
        
        while(issue.length()>2000){
            event.getGuild().getTextChannelById(Data.ADMIN_BOT_ID).sendMessage("a").complete().editMessage(issue.substring(0, 2000)).complete();
            issue = issue.substring(2000);
        }
        event.getGuild().getTextChannelById(Data.ADMIN_BOT_ID).sendMessage("a").complete().editMessage(issue).complete();

        String acknowledged = "The issue was sent to the admin team anonymously.";
        Helper.unhook(acknowledged, failed, hook, event.getUser());
    }

    @Override
    public CommandDataImpl initialise(Guild eth) {
        CommandDataImpl report = new CommandDataImpl(getName(), "A command to report a incident to Staff anonymously.");
        
        report.addOption(OptionType.STRING, "issue", "The isssue you have or the incident that occured.", true);
        report.addOption(OptionType.USER, "user", "If you want to report a User. This can be left empty.");
        
        return report;
    }
    
}
