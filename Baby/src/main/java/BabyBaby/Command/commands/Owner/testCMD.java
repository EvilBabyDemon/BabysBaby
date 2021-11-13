package BabyBaby.Command.commands.Owner;




import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/*
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.Color;
*/
import BabyBaby.Command.CommandContext;
import BabyBaby.Command.OwnerCMD;
import BabyBaby.Command.StandardHelp;
import BabyBaby.data.Data;
import BabyBaby.data.Helper;
import net.dv8tion.jda.api.audit.ActionType;
import net.dv8tion.jda.api.audit.AuditLogEntry;
import net.dv8tion.jda.api.entities.Icon;
import net.dv8tion.jda.api.entities.Member;
//import net.dv8tion.jda.api.entities.Emoji;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.requests.restaction.pagination.AuditLogPaginationAction;
/*
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.selections.SelectionMenu;
import net.dv8tion.jda.api.interactions.components.selections.SelectionMenu.Builder;
import net.dv8tion.jda.api.requests.restaction.MessageAction;
*/
public class TestCMD implements OwnerCMD{

    @Override
    public String getName() {
        return "test";
    }

    @Override
    public void handleOwner(CommandContext ctx) {
        /*
        LinkedList<Member> allmem = new LinkedList<>(); 
        
        Role student = ctx.getGuild().getRoleById(Data.ethstudent);
        for (Member mem : ctx.getGuild().getMembers()) {   
            if(mem.getRoles().contains(student))
                allmem.add(mem);
        }

        Connection c = null;
        PreparedStatement pstmt = null;
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection(Data.db);
            for (Member mem : allmem) {
                pstmt = c.prepareStatement("INSERT OR IGNORE INTO VERIFIED (ID) VALUES (?);");
                pstmt.setString(1, mem.getId());
                pstmt.execute();    
                pstmt.close();
            }
            
            c.close();
        } catch ( Exception e ) {
            System.out.println(e.getClass().getName() + ": " + e.getMessage());
            return;
        }
        ctx.getMessage().reply("Done!").complete();
        */
        /*
        String desc = "";
        EmbedBuilder eb = new EmbedBuilder();
        
        
        eb.setTitle("Pruneable Members");
        
        for (int i = 1; i < 30; i+=2) {
            desc += i + " Days: " + ctx.getGuild().retrievePrunableMemberCount(i).complete() + "\n"; 
        }
        

        eb.setDescription((desc.equals("")?"None":desc));

        ctx.getChannel().sendMessageEmbeds(eb.build()).queue();
        */
        
        //System.out.println(ctx.getChannel().retrieveMessageById(ctx.getArgs().get(0)).complete().getContentRaw());
        /*
        MessageAction msg = ctx.getChannel().sendMessage("test");

        Builder menu = SelectionMenu.create("menu:class");
        menu.setPlaceholder("Choose your number");
        
        
        for (int i = 0; i < 20; i++) {
            menu.addOption("Selection " + i, ""+i);
        }
        LinkedList<ActionRow> tmp = new LinkedList<>();
        
        for (int i = 0; i < 5; i++) {
            tmp.add(ActionRow.of(menu.build()));
        }
        
        msg.setActionRows(tmp);
        msg.queue();
        */


        if(ctx.getArgs().get(0).equalsIgnoreCase("emote")) {
            
            Role rolesArr[] = new Role[ctx.getArgs().size()-2];
            
            for (int i = 2; i < ctx.getArgs().size(); i++) {
                rolesArr[i-2] = ctx.getGuild().getRoleById(ctx.getArgs().get(i));
            }
            
            try {
                ctx.getGuild().createEmote(ctx.getArgs().get(1), Icon.from(ctx.getMessage().getAttachments().get(0).downloadToFile().join()), rolesArr).complete();
            } catch (Exception e) {
                ctx.getChannel().sendMessage("File not found \n" + e).complete();
            }
            return;
        }

        if(ctx.getArgs().size() == 2 && ctx.getArgs().get(0).equalsIgnoreCase("student") && ctx.getArgs().get(1).equalsIgnoreCase("yes")) {
            List<Member> allMem = ctx.getGuild().getMembers();
            
            
            LinkedList<String> pings = new LinkedList<>();
            String tempSmall = "";

            for (Member mem : allMem) {
                if(mem.getRoles().size() != 1 || !mem.getRoles().get(0).getId().equals("747786383317532823")){
                    continue;
                }

                if(tempSmall.length() + mem.getAsMention().length() < 1999){
                    tempSmall += mem.getAsMention() + " ";
                } else {
                    pings.add(tempSmall);
                    tempSmall = mem.getAsMention() + " "; 
                }
            }
            pings.add(tempSmall);

            for (String ping : pings) {
               ctx.getChannel().sendMessage(ping).complete().delete().complete();
            }
            return;
        }
        

        Data.automaticRoleAddThingy = !Data.automaticRoleAddThingy;

        AuditLogPaginationAction logs = ctx.getGuild().retrieveAuditLogs();
        //logs.type()
        
        if(ctx.getArgs().size() == 0){
            String output = "";
            for (ActionType actType : ActionType.values()) {
                output += actType.name() + " ";
            }
            ctx.getChannel().sendMessage(output).complete();
            return;
        }


        String finalorso = ctx.getArgs().get(0);
        List<Object[]> minedit = List.of(ActionType.values()).parallelStream().map(actionType -> new Object[] {Helper.minDistance(finalorso, actionType.name()), actionType}).collect(Collectors.toList());
        
        LinkedList<Object[]> smallest = new LinkedList<Object[]>();    
        
        smallest.add(minedit.remove(0));
        int smallestint = (int) smallest.get(0)[0];
        for (Object[] minEditObj : minedit) {
            int x = (int) minEditObj[0];
            if(smallestint == x){
                smallest.add(minEditObj);
            } else if(smallestint > x){
                smallest = new LinkedList<>();
                smallest.add(minEditObj);
                smallestint = x;
            }
        }

        logs.type((ActionType) smallest.get(0)[1]);
        String msg = "";
        for (AuditLogEntry entry : logs) {
            String time = "" + entry.getTimeCreated().toEpochSecond();
            String user = entry.getUser().getAsMention();
            String changes = entry.getChanges().values().stream().map(change -> change.getOldValue() + " " + change.getNewValue()).collect(Collectors.joining("\n"));
            msg += user + " <t:" + time + ">" + "\n" + changes + "\n";
            entry.getOptions().values().stream().map(option -> option.getClass() + " " + option).collect(Collectors.joining(" "));
            
        }
        
        ctx.getChannel().sendMessage("give me a sec").complete().editMessage("AuditLog: " + (msg.length()>1980 ? msg.substring(0, 1980) : msg)).complete();

    }   

    @Override
    public MessageEmbed getOwnerHelp(String prefix) {
        return StandardHelp.Help(prefix, getName(), "(whatever it is atm)", "A cmd to test things out.");
    }
    /*
    private static String rgbToHex(Color c) {
        return String.format("#%02x%02x%02x", c.getRed(),                 c.getGreen(), c.getBlue());
    } 
    */
    
}
