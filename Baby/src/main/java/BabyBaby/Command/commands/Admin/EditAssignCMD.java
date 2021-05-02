package BabyBaby.Command.commands.Admin;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import BabyBaby.Command.AdminCMD;
import BabyBaby.Command.CommandContext;
import BabyBaby.Command.StandardHelp;
import BabyBaby.data.data;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.MessageReaction;
import net.dv8tion.jda.api.entities.Role;

public class EditAssignCMD implements AdminCMD{

	@Override
	public void handleOwner(CommandContext ctx) {
		handleAdmin(ctx);
		
	}

	@Override
	public MessageEmbed getOwnerHelp(String prefix) {
		return getAdminHelp(prefix);
	}

	@Override
	public String getName() {
		return "editassign";
	}

	@Override
	public void handleAdmin(CommandContext ctx) {
		if(!ctx.getGuild().getId().equals(data.ethid)){
            return;
        }
        Connection c = null;
        Statement stmt = null;
        MessageChannel channel = ctx.getChannel();
        HashSet<String> cats = new HashSet<String>(); 

        ResultSet rs;

        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection(data.db);
            
            stmt = c.createStatement();

            rs = stmt.executeQuery("SELECT categories FROM ASSIGNROLES;");
            while ( rs.next() ) {
                String cat = rs.getString("categories");
                cats.add(cat);
            }
            rs.close();
            stmt.close();
            c.close();
        } catch ( Exception e ) {
            channel.sendMessage( e.getClass().getName() + ": " + e.getMessage()).queue();
            e.printStackTrace(); 
            return;
        }
        
        

        String msg = "";

        LinkedList<LinkedList<String>> emotes = new LinkedList<>();
        ArrayList<String> categ = new ArrayList<>();
        LinkedList<String> roles = new LinkedList<>();

        for (String var : cats) {
            HashMap<Role, Object[]> sorting = new HashMap<>();
            try {
                Class.forName("org.sqlite.JDBC");
                c = DriverManager.getConnection(data.db);
                
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

        LinkedList<String> remover = new LinkedList<>();

        for (int i = 0; i < categ.size(); i++) {
            emb.add(embeds(categ.get(i), roles.get(i)));
        }
        int count = 0;
        for (EmbedBuilder eb : emb) {
            LinkedList<String> temp = new LinkedList<>();
            temp.addAll(emotes.remove(0));

            c = null;
            PreparedStatement pstmt = null;
            try {
                Class.forName("org.sqlite.JDBC");
                c = DriverManager.getConnection(data.db);
                pstmt = c.prepareStatement("SELECT * FROM MSGS WHERE CATEGORY = ? AND GUILDID = ?;");
                pstmt.setString(1, categ.get(count));
                pstmt.setString(2, ctx.getGuild().getId());
                rs = pstmt.executeQuery();

                String msgid = "";
                boolean empty = true;
                while(rs.next()){
                    msgid = rs.getString("MSGID");
                    Message edited;
                    try {
                        edited = ctx.getGuild().getTextChannelById(rs.getString("CHANNELID")).editMessageById(msgid, eb.build()).complete();    
                    } catch (Exception e) {
                        remover.add(msgid);
                        continue;
                    }
                    
                    data.msgid.add(edited.getId());
                    
                    List<MessageReaction> reactions = edited.getReactions();
                    for (MessageReaction var : reactions) {
                        if(!cont(temp, var.getReactionEmote().getName())){
                            var.clearReactions().complete();
                        }
                    }


                    for (String var : temp) {
                        if(var == null || var.length() == 0)
                                continue;
                        try{
                            channel.addReactionById(edited.getId(), var).queue();
                        } catch (Exception e){
                            ctx.getChannel().sendMessage("Reaction with ID:" + var + " is not accesible.").complete().delete().queueAfter(10, TimeUnit.SECONDS);
                        }
                    }
                    empty = false;
                }
                if(empty){
                    Message msgs = ctx.getChannel().sendMessage(eb.build()).complete();
                    data.msgid.add(msgs.getId());
                    for (String var : temp) {
                        if(var == null || var.length() == 0)
                                continue;
                        try{
                        channel.addReactionById(msgs.getId(), var).queue();
                        } catch (Exception e){
                            ctx.getChannel().sendMessage("Reaction with ID:" + var + " is not accesible.").complete().delete().queueAfter(10, TimeUnit.SECONDS);
                        }
                    }

                    c = null;
                    pstmt = null;
                    try {
                        Class.forName("org.sqlite.JDBC");
                        c = DriverManager.getConnection(data.db);
                        pstmt = c.prepareStatement("INSERT INTO MSGS (GUILDID, CHANNELID, MSGID, CATEGORY) VALUES (?, ?, ?, ?);");
                        pstmt.setString(1, ctx.getGuild().getId());
                        pstmt.setString(2, ctx.getChannel().getId());
                        pstmt.setString(3, msgs.getId());
                        pstmt.setString(4, categ.get(count));

                        pstmt.executeUpdate();
                        pstmt.close();
                        c.close();
                    } catch ( Exception e ) {
                        e.printStackTrace(); 
                    }
                }
                
                pstmt.close();
                c.close();
            } catch (Exception e) {
                channel.sendMessage(e.getClass().getName() + ": " + e.getMessage()).queue();
                e.printStackTrace();
            }
            
            count++;
        }

        for (String var : remover) {
            c = null;
            PreparedStatement pstmt = null;
            try {
                Class.forName("org.sqlite.JDBC");
                c = DriverManager.getConnection(data.db);
                pstmt = c.prepareStatement("DELETE FROM MSGS WHERE MSGID = ?;");
                pstmt.setString(1, var);
                pstmt.executeUpdate();
                pstmt.close();
                c.close();
            } catch ( Exception e ) {
                channel.sendMessage(e.getClass().getName() + ": " + e.getMessage()).queue();
                e.printStackTrace(); 
            }
        }
        



        channel.deleteMessageById(ctx.getMessage().getId()).queue();

		
	}

	@Override
	public MessageEmbed getAdminHelp(String prefix) {
		return StandardHelp.Help(prefix, getName(), "", "Update'" + new RoleAssignCMD().getName() + "' cmd messages. This technically can be used in any channel and it will update all saved embeds but if a new category was added it will send that embed in the channel the command was used.");
	}


    public EmbedBuilder embeds(String title, String msg){
        
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle(title);
        eb.setColor(1);
        eb.setDescription(msg);
        eb.setFooter("Click on the Emotes to assign yourself Roles.");

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

    public boolean cont (List<String> c, String s){
        for (String var : c) {
            if(var.contains(s)){
                return true;
            }
        }
        return false;
    }
    
}
