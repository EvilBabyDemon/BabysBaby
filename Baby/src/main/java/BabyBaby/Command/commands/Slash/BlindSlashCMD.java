package BabyBaby.Command.commands.Slash;

import BabyBaby.Command.ISlashCMD;
import BabyBaby.Command.commands.Public.BlindCMD;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.internal.interactions.CommandDataImpl;

public class BlindSlashCMD implements ISlashCMD{

    @Override
    public String getName() {
        return "blind";
    }

    @Override
    public void handle(SlashCommandInteractionEvent event, InteractionHook hook, boolean failed) {
        String unit = (event.getOption("unit")!=null) ? event.getOption("unit").getAsString() : null;
        boolean force = (event.getOption("force")!=null) ? event.getOption("force").getAsBoolean() : false;
        boolean semester = (event.getOption("semester")!=null) ? event.getOption("semester").getAsBoolean() : false;
        new BlindCMD().roleRemoval(event.getOption("time").getAsString(), event.getMember(), event.getGuild(), unit, force, event.getChannel(), semester);
    }

    @Override
    public CommandDataImpl initialise(Guild eth) {
        CommandDataImpl blind = new CommandDataImpl(getName(), "A command to blind yourself. You won't see any channels for this time.");
                        
        blind.addOption(OptionType.NUMBER, "time", "Length of the blind.", true);
        blind.addOption(OptionType.STRING, "unit", "Default is minutes. Seconds, minutes, hours, days.");
        blind.addOption(OptionType.BOOLEAN, "force", "If forceblind or not. Default is false.");
        blind.addOption(OptionType.BOOLEAN, "semester", "You will keep your Subject Channels. Default is false.");
        
        eth.upsertCommand(blind).complete();
        return blind;
    }

}
