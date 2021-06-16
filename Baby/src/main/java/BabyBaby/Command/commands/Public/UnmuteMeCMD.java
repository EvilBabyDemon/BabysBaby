package BabyBaby.Command.commands.Public;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;

import BabyBaby.Command.CommandContext;
import BabyBaby.Command.PublicCMD;
import BabyBaby.Command.StandardHelp;
import BabyBaby.data.GetUnmute;
import BabyBaby.data.Data;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;

public class UnmuteMeCMD implements PublicCMD {

    @Override
    public String getName() {
        return "unmuteme";
    }

    @Override
    public void handlePublic(CommandContext ctx) {
        /*
        if(!MuteCMD.userMuted.containsKey(ctx.getAuthor())){
            ctx.getAuthor().openPrivateChannel().queue((channel) -> {
                channel.sendMessage("You were never muted. Or at least not by this bot. If you were pls report this problem to Lukas").queue();
            });
            return;
        }
        */
        actualcmd(ctx.getAuthor(), ctx.getArgs());
    }

    public void privhandle(User author, List<String> args){
        actualcmd(author, args);
    }

    private void actualcmd(User author, List<String> cmds){
        
        if(author.getId().equals("177498563637542921")){
            author.openPrivateChannel().queue(privchannel -> {
                privchannel.sendMessage("Nope, not getting unmuted till the timer runs out. You did this to yourself.").queue();
            });
            return;
        }

        LinkedList<GetUnmute> vars = new LinkedList<>();        

        String authorID = author.getId();
        for (Member var : MuteCMD.userMuted.keySet()) {
            if(var.getId().equals(authorID)){
                vars.add(MuteCMD.variables.get(MuteCMD.userMuted.get(var)));
            }
        }
        GetUnmute muteclass = null;
        if(cmds.size() == 0){
            switch(vars.size()){
                case 0:
                    author.openPrivateChannel().queue(privchannel -> {
                        privchannel.sendMessage("You were never muted. Or at least not by this bot. If you were pls report this problem to Lukas.").queue();
                    });
                    return;
                case 1:
                    muteclass = vars.get(0);
                    break;
                default:
                    author.openPrivateChannel().queue(privchannel -> {
                        privchannel.sendMessage("Pls use +" + getName()  + " <key> as there are multiple servers you are muted on. These are the keys:").queue();
                        for (GetUnmute var : vars) {
                            privchannel.sendMessage("Key: " + var.guild.getId() + " " + var.guild.getName()).queue();
                        }
                        //TO DO FIX FOR multiple server
                    });
                    return;
            }
        } else {
            for (GetUnmute var : vars) {
                if(var.guild.getId().equals(cmds.get(0))){
                    muteclass = var;
                    break;
                }
            }
        }  
       
        Guild muteServ = muteclass.guild;
        Member muted = muteServ.getMember(muteclass.muted);
        muteServ.removeRoleFromMember(muted, muteclass.stfu).queue();

        ScheduledExecutorService mute = MuteCMD.userMuted.get(muted);
        MuteCMD.variables.remove(mute);
        mute.shutdownNow();
        MuteCMD.userMuted.remove(muted);

        Connection c = null;
        PreparedStatement stmt = null;
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection(Data.db);
            
            stmt = c.prepareStatement("DELETE FROM USERS WHERE ID = ? AND GUILDID = ?;");
            stmt.setString(1, muted.getId());
            stmt.setString(2, muteServ.getId());
            stmt.execute();
            stmt.close();
            c.close();
        } catch ( Exception e ) {
            e.printStackTrace(); 
            return;
        }

        
        author.openPrivateChannel().queue((channel) -> {
            channel.sendMessage("You shall be unmuted!").queue();
        });
    }

    @Override
    public MessageEmbed getPublicHelp(String prefix) {
        return StandardHelp.Help(prefix, getName(), "", "Command to unmute you early after using " + new MuteCMD().getName() + ".");
    }
    
    
}
