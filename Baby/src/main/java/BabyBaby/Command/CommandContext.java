package BabyBaby.Command;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.List;

import BabyBaby.data.Data;


public class CommandContext {

    private final MessageReceivedEvent event;
    private final List<String> args;
    private final int permissionLevel;

    public CommandContext(MessageReceivedEvent event, List<String> args) {
        this.event = event;
        this.args = args;
        Member member = this.getMember();

        Role mod = event.getGuild().getRoleById(Data.MODERATOR_ID);

        // owner -> 2
        // admin -> 1
        // public -> 0
       this.permissionLevel = member.getId().equals(Data.myselfID) ? 2 : event.getGuild().getId().equals(Data.ETH_ID) && (member.hasPermission(Permission.ADMINISTRATOR) || member.getRoles().contains(mod)) ? 1 : 0;
    }
    
    public Guild getGuild() {
        return this.getEvent().getGuild();
    }

    public MessageReceivedEvent getEvent() {
        return this.event;
    }

    public List<String> getArgs() {
        return this.args;
    }

    public int getPermissionLevel() {
        return this.permissionLevel;
    }

    //public Channel getChannel() {
    //    return new Channel(this.event.getChannel());
    //}
    public MessageChannel getChannel() {
      return this.event.getChannel();
  }
      
    public Message getMessage() {
      return this.event.getMessage();
    }
    
    public User getAuthor() {
      return this.event.getAuthor();
    }
    
    public Member getMember() {
      return this.event.getMember();
    }
    
    public JDA getJDA() {
      return this.event.getJDA();
    }
    
    public User getSelfUser() {
      return this.event.getJDA().getSelfUser();
    }
    
    public Member getSelfMember() {
      return this.event.getGuild().getMember(this.getSelfUser());
    }
      
}