package BabyBaby.Command.commands.Slash;

import BabyBaby.Command.ISlashCMD;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.internal.interactions.CommandDataImpl;

public class RemindSlashCMD implements ISlashCMD {

    @Override
    public String getName() {
        return "remind";
    }

    @Override
    public void handle(SlashCommandInteractionEvent event, InteractionHook hook, boolean failed) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public CommandDataImpl initialise(Guild eth) {
        CommandDataImpl remind = new CommandDataImpl(getName(), "A command to remind yourself.");
                        
        remind.addOption(OptionType.NUMBER, "time", "In how many Time units do you want to get reminded?", true);
        remind.addOption(OptionType.STRING, "unit", "Default is minutes. Others are seconds, minutes, hours, days.");
        
        return remind;
    }
    
}
