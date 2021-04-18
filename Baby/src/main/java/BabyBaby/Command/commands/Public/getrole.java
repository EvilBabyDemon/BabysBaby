package BabyBaby.Command.commands.Public;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import BabyBaby.Command.CommandContext;
import BabyBaby.Command.PublicCMD;
import BabyBaby.Command.StandardHelp;
import BabyBaby.data.data;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.Role;

public class getrole implements PublicCMD{

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
        return "role";
    }

    @Override
    public void handlePublic(CommandContext ctx) {

        List<String> cmds = ctx.getArgs();
        
        if(cmds.size() != 0){

            








        }

        Connection c = null;
        Statement stmt = null;
        MessageChannel channel = ctx.getChannel();
        HashSet<String> cats = new HashSet<String>(); 

        ResultSet rs;

        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:testone.db");
            
            stmt = c.createStatement();

            rs = stmt.executeQuery("SELECT categories FROM ASSIGNROLES;");
            while ( rs.next() ) {
                String cat = rs.getString("categories");
                cats.add(cat);
            }
            rs.close();
        } catch ( Exception e ) {
            channel.sendMessage( e.getClass().getName() + ": " + e.getMessage()).queue();
            e.printStackTrace(); 
            return;
        } finally{
            try {
                stmt.close();
                c.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            
        }
        
        

        //TODO do embeds with each category

        String msg = "";



        LinkedList<LinkedList<String>> emotes = new LinkedList<>();
        LinkedList<String> categ = new LinkedList<>();
        LinkedList<String> roles = new LinkedList<>();

        for (String var : cats) {
            HashMap<Role, Object[]> sorting = new HashMap<>();
            try {
                Class.forName("org.sqlite.JDBC");
                c = DriverManager.getConnection("jdbc:sqlite:testone.db");
                
                stmt = c.createStatement();

                Guild called = ctx.getGuild();
                rs = stmt.executeQuery("SELECT * FROM ASSIGNROLES WHERE categories='" + var + "';");
                while ( rs.next() ) {
                    String rcat = rs.getString("ID");
                    String emote = rs.getString("EMOTE");
                    String orig = emote;

                    if(emote == null || emote.length() == 0){
                        emote = "";
                    } else {
                        emote = emote.contains(":") ? "<" + emote + ">" : emote;
                    }
                    msg = emote + " : "+ called.getRoleById(rcat).getAsMention() + "\n";
                    sorting.put(called.getRoleById(rcat), new Object[] {orig, msg});
                }
                rs.close();
                stmt.close();
                c.close();
            } catch ( Exception e ) {
                channel.sendMessage(e.getClass().getName() + ": " + e.getMessage()).queue();
                return;
            }

            LinkedList<Object[]> sorted = rolesorter(sorting);
            LinkedList<String> tempo = new LinkedList<>();
            msg = "";
            for (Object[] obj : sorted) {
                tempo.add((String) obj[0]);
                msg += (String) obj[1];
            }

            emotes.add(tempo);
            categ.add(var);
            roles.add(msg);
            msg = "";
        }

        LinkedList<EmbedBuilder> emb = new LinkedList<>();
        int count = 0;
        HashMap<String, String> tmp = new HashMap<>();
        LinkedList<Integer> emotewhen = new LinkedList<>();


        for (int i = 0; i < categ.size(); i++) {
            int x = roles.get(i).split("\n").length;
            count += x;
            if(count>20){
                emotewhen.add(i);
                emb.add(embeds(tmp, ctx));
                count = x;
                tmp = new HashMap<>();
            }
            tmp.put(categ.get(i), roles.get(i));
        }
        emotewhen.add(categ.size());
        emb.add(embeds(tmp, ctx));
        int max = 0;
        for (EmbedBuilder eb : emb) {
            LinkedList<String> temp = new LinkedList<>();
            max = emotewhen.remove(0)-max;
            for (int i = 0; i < max; i++) {
                temp.addAll(emotes.remove(0));
            }

            Message msgs = channel.sendMessage(eb.build()).complete();                
            data.msgid.add(msgs.getId());
                for (String var : temp) {
                    if(var == null || var.length() == 0)
                           continue;
                    channel.addReactionById(msgs.getId(), var).queue();
                }
        }
        channel.deleteMessageById(ctx.getMessage().getId()).queue();
        
        
    }

    @Override
    public MessageEmbed getPublicHelp(String prefix) {
        return StandardHelp.Help(prefix, getName(), "", "Command to see all roles with emotes such that you can assign them yourself.");
    }

    public EmbedBuilder embeds(HashMap<String, String> fields, CommandContext ctx){
        
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Roles you can assign yourself", null);
        eb.setColor(1);

        for (String var : fields.keySet()) {
            eb.addField(var, fields.get(var), true); 
        }

        String nickname = (ctx.getMember().getNickname() != null) ? ctx.getMember().getNickname()
                : ctx.getMember().getEffectiveName();
        eb.setFooter("Summoned by: " + nickname, ctx.getAuthor().getAvatarUrl());

        return eb;
    }

    public LinkedList<Object[]> rolesorter (HashMap<Role, Object[]> sorting){
        LinkedList<Object[]> res = new LinkedList<>();
        while(sorting.size()!=0){
            Role highest = null;
            for (Role var : sorting.keySet()) {
                if(highest == null || var.getPosition() > highest.getPosition()){
                    highest = var;
                }
            }
            res.add(sorting.get(highest));
            sorting.remove(highest);
        }
        return res;
    }
    
}
