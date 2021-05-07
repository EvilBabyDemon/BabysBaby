package BabyBaby.Command.commands.Public;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

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

public class GetRole implements PublicCMD{

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
        if(!ctx.getGuild().getId().equals(data.ethid))
            return;

        MessageChannel channel = ctx.getChannel();

        List<String> cmds = ctx.getArgs();
        
        Connection c = null;
        Statement stmt = null;
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

        if(cmds.size() != 0){

            HashMap<String, String> emoterolelist = data.emoteassign;
            HashMap<String, Object> namerole = new HashMap<>();
            for (String var : emoterolelist.keySet()) {
                Role tmp = ctx.getGuild().getRoleById(emoterolelist.get(var));
                namerole.put(tmp.getName().toLowerCase(), tmp);
            }
            for (String var : cats) {
                namerole.put(var.toLowerCase(), var);
            }


            String msg = ctx.getMessage().getContentRaw();
            msg = msg.substring(1 + getName().length() + 1);
            msg.toLowerCase();
            if(msg.length() > 50){
                channel.sendMessage("I think you are using this command wrong...").queue();
                return;
            }

            if(namerole.containsKey(msg)){
                
                if(namerole.get(msg) instanceof Role)
                    gibRole(ctx, (Role) namerole.get(msg));
                else
                    sendEmbed(ctx, (String) namerole.get(msg));
                return;
            }


            String finalorso = msg;
            
            List<Object[]> minedit = namerole.keySet().parallelStream().map(role -> new Object[] {minDistance(finalorso, role), namerole.get(role)}).collect(Collectors.toList());

            LinkedList<Object[]> smallest = new LinkedList<Object[]>();    
            
            smallest.add(minedit.remove(0));
            int smallestint = (int) smallest.get(0)[0];
            for (Object[] var : minedit) {
                int x = (int) var[0];
                if(smallestint == x){
                    smallest.add(var);
                } else if(smallestint > x){
                    smallest = new LinkedList<>();
                    smallest.add(var);
                    smallestint = x;
                }
            }
            
            if(smallestint == 100){
                channel.sendMessage("I don't think this role exists.").queue();
                return;
            }

            if(smallest.size()!=1){
                channel.sendMessage("Sorry you gotta write more precise as there is more than one Role you could have meant.").queue();
                return;
            }

            if(smallest.size()==1){
                if(smallest.get(0)[1] instanceof Role)
                    gibRole(ctx, (Role) smallest.get(0)[1]);
                else
                    sendEmbed(ctx, (String) smallest.get(0)[1]);
                //channel.sendMessage("" + (int) smallest.get(0)[0]).queue();
                return;
            }
        }

        

        String msg = "";

        LinkedList<LinkedList<String>> emotes = new LinkedList<>();
        LinkedList<String> categ = new LinkedList<>();
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
                try {
                    temp.addAll(emotes.remove(0));
                } catch (Exception e) {
                    break;
                }
               
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
        return StandardHelp.Help(prefix, getName(), "[Role name / Category]", "Command to see all roles with emotes such that you can assign them yourself. Or add the role name and directly get a Role added or removed.");
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

    public int minDistance(String word1, String word2) {
        int len1 = word1.length();
        int len2 = word2.length();
        

        // len1+1, len2+1, because finally return dp[len1][len2]
        int[][] dp = new int[len1 + 1][len2 + 1];
     
        for (int i = 0; i <= len1; i++) {
            dp[i][0] = i;
        }
     
        for (int j = 0; j <= len2; j++) {
            dp[0][j] = j;
        }
     
        //iterate though, and check last char
        for (int i = 0; i < len1; i++) {
            char c1 = word1.charAt(i);
            int minimal = 100;
            for (int j = 0; j < len2; j++) {
                char c2 = word2.charAt(j);
     
                //if last two chars equal
                if (c1 == c2) {
                    //update dp value for +1 length
                    dp[i + 1][j + 1] = dp[i][j];
                } else {
                    int replace = dp[i][j] + 1;
                    int insert = dp[i][j + 1] + 1;
                    int delete = dp[i + 1][j] + 1;
                    
                    int min = replace > insert ? insert : replace;
                    min = delete > min ? min : delete;
                    dp[i + 1][j + 1] = min;
                }
                if(minimal>dp[i + 1][j + 1])
                    minimal = dp[i + 1][j + 1];
            }
            if(minimal >= (1 + Math.min(len1, len2)/5*2))
                return 100;
        }
        
        return (dp[len1][len2]<(1 + Math.min(len1, len2)/5*2)? dp[len1][len2]:100);
    }


    private void gibRole (CommandContext ctx, Role role){
        //767315361443741717 External data.ethexternal
        //747786383317532823 Student data.ethstudent
        MessageChannel channel = ctx.getChannel();

        List<Role> autroles = ctx.getMember().getRoles();
        
        if(autroles.contains(role)){
            if(role.getId().equals(data.ethexternal)){
                Role student = ctx.getGuild().getRoleById(data.ethstudent);
                if(autroles.contains(student)){
                    ctx.getGuild().addRoleToMember(ctx.getMember(), student).complete();
                    ctx.getGuild().removeRoleFromMember(ctx.getMember(), role).complete();
                    channel.sendMessage("Removed the Role " + role.getName() + ".").complete();
                } else{
                    channel.sendMessage("You need at least either the Student or External Role").queue();
                }
            } else if(role.getId().equals(data.ethstudent)){
                Role external = ctx.getGuild().getRoleById(data.ethexternal);
                if(autroles.contains(external)){
                    ctx.getGuild().addRoleToMember(ctx.getMember(), external).complete();
                    ctx.getGuild().removeRoleFromMember(ctx.getMember(), role).complete();
                    channel.sendMessage("Removed the Role " + role.getName() + ".").complete();
                } else{
                    channel.sendMessage("You need at least either the Student or External Role").queue();
                }
            } else {
                ctx.getGuild().removeRoleFromMember(ctx.getMember(), role).complete();
                channel.sendMessage("Removed the Role " + role.getName() + ".").complete();
            }
        } else {
            if(role.getId().equals(data.ethexternal)){
                Role student = ctx.getGuild().getRoleById(data.ethstudent);
                ctx.getGuild().addRoleToMember(ctx.getMember(), role).complete();
                ctx.getGuild().removeRoleFromMember(ctx.getMember(), student).complete();
                channel.sendMessage("Gave you the Role " + role.getName() + " and removed " + student.getName()).complete();
            } else if(role.getId().equals(data.ethstudent)){
                Role external = ctx.getGuild().getRoleById(data.ethexternal);
                ctx.getGuild().addRoleToMember(ctx.getMember(), role).complete();
                ctx.getGuild().removeRoleFromMember(ctx.getMember(), external).complete();  
                channel.sendMessage("Gave you the Role " + role.getName() + " and removed " + external.getName()).complete();  
            } else {
                ctx.getGuild().addRoleToMember(ctx.getMember(), role).complete();
                channel.sendMessage("Gave you the Role " + role.getName() + ".").complete();
            }
        }
    }

    private void sendEmbed(CommandContext ctx, String cat){

        Connection c = null;
        PreparedStatement stmt = null;
        ResultSet rs;
        MessageChannel channel = ctx.getChannel();

        String msg = "";
        LinkedList<String> roles = new LinkedList<>();

        
        HashMap<Role, Object[]> sorting = new HashMap<>();
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection(data.db);
            
            stmt = c.prepareStatement("SELECT * FROM ASSIGNROLES WHERE categories=?;");
            stmt.setString(1, cat);
            Guild called = ctx.getGuild();
            rs = stmt.executeQuery();
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
        LinkedList<String> emotes = new LinkedList<>();
        msg = "";
        for (Object[] obj : sorted) {
            emotes.add((String) obj[0]);
            msg += (String) obj[1];
        }

        roles.add(msg);

        EmbedBuilder eb = new EmbedBuilder();
        
        eb.setTitle(cat);
        eb.setColor(1);
        eb.setDescription(roles.get(0));
        
        eb.setFooter("Click on the Emotes to assign yourself Roles.");
        
        Message msgs = channel.sendMessage(eb.build()).complete();                
        data.msgid.add(msgs.getId());
        for (String var : emotes) {
            if(var == null || var.length() == 0)
                    continue;
            channel.addReactionById(msgs.getId(), var).queue();
        }
        data.msgid.add((msgs.getId()));
    }
}
