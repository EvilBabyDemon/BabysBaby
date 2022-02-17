package BabyBaby.Command;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.internal.interactions.CommandDataImpl;

public interface ISlashCMD {
    
    String getName();

    void handle(SlashCommandInteractionEvent event, InteractionHook hook, boolean failed);
    
    CommandDataImpl initialise(Guild eth);

    default void load(CommandDataImpl cmd, Guild eth) {
        eth.upsertCommand(cmd).complete();
    }

}