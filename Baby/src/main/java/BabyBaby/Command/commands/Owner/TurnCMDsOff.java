package BabyBaby.Command.commands.Owner;

import java.io.IOException;

import BabyBaby.CmdHandler;
import BabyBaby.Command.CommandContext;
import BabyBaby.Command.IOwnerCMD;
import BabyBaby.Command.StandardHelp;
import BabyBaby.data.Data;
import net.dv8tion.jda.api.entities.MessageEmbed;

public class TurnCMDsOff implements IOwnerCMD {

    @Override
    public String getName() {
        return "stop";
    }

    @Override
    public void handleOwner(CommandContext ctx) {
        try {
            CmdHandler tmp = new CmdHandler(ctx.getJDA());

            IOwnerCMD switsch = tmp.searchOwnerCommand(ctx.getArgs().get(0));
            String output = "Couldn't find the Command.";
            if (switsch != null) {
                if (CmdHandler.offCMD.contains(switsch.getName())) {
                    CmdHandler.offCMD.remove(switsch.getName());
                    output = "Turned the cmd on.";
                } else {
                    CmdHandler.offCMD.add(switsch.getName());
                    output = "Turned the cmd off.";
                }
            }
            ctx.getChannel().sendMessage(output).queue();
        } catch (IOException e) {
            ctx.getMessage().addReaction(Data.xmark).queue();
            e.printStackTrace();
        }

    }

    @Override
    public MessageEmbed getOwnerHelp(String prefix) {
        return StandardHelp.Help(prefix, getName(), "<cmd name>", "Turn Commands off or on.");
    }

}
