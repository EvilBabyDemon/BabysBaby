package BabyBaby.Command.commands.Public;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;

import BabyBaby.Command.CommandContext;
import BabyBaby.Command.PublicCMD;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;

public class unmute implements PublicCMD {

    @Override
    public void handleAdmin(CommandContext ctx) {
        handlePublic(ctx);
    }

    @Override
    public MessageEmbed getAdminHelp(String prefix) {
        return getPublicHelp(prefix);
    }

    @Override
    public void handleOwner(CommandContext ctx) {
        handlePublic(ctx);
    }

    @Override
    public MessageEmbed getOwnerHelp(String prefix) {
        return getPublicHelp(prefix);
    }

    @Override
    public String getName() {
        return "unmute";
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
                        privchannel.sendMessage("Pls use +unmute <key> as there are multiple servers you are muted on. These are the keys:").queue();
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
        Statement stmt = null;
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:testone.db");
            
            stmt = c.createStatement();
            stmt.execute("DELETE FROM USERS WHERE ID = " + muted.getId() + " AND GUILDUSER = " + muteServ.getId() + ";");
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
        // TODO Auto-generated method stub
        return null;
    }
    
    
}
