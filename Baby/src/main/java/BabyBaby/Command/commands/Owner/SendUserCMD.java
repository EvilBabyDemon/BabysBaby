package BabyBaby.Command.commands.Owner;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import BabyBaby.Command.CommandContext;
import BabyBaby.Command.OwnerCMD;
import BabyBaby.Command.StandardHelp;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;

public class SendUserCMD implements OwnerCMD{

    @Override
    public String getName() {
        return "sendollie";
    }

    @Override
    public void handleOwner(CommandContext ctx) {
        List<String> cmds = ctx.getArgs();
        ScheduledExecutorService ollie = Executors.newScheduledThreadPool(1);
        User ollieUser = ctx.getGuild().getMemberById(cmds.get(0)).getUser();

        ollie.schedule(new schribollie(ollieUser), Integer.parseInt(cmds.get(1)), TimeUnit.SECONDS);
        
    }

    @Override
    public MessageEmbed getOwnerHelp(String prefix) {
        return StandardHelp.Help(prefix, getName(), "<UserID> <Time in Seconds>", "Send a Private Message to a User, only meant for ollie rly.");
    }
    
}


class schribollie implements Runnable {
    User ollie;

    public schribollie(User user) {
        ollie = user;
    }

    public void run() {	
        ollie.openPrivateChannel().queue((channel) -> {
            channel.sendMessage("Dis Mami (This message is sponsored by Modernwarfare Aaron").queue();
        });
    }
}
