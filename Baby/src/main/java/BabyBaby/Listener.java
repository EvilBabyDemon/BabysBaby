package BabyBaby;

import BabyBaby.data.PrefixMap;
import BabyBaby.data.SQLiteDataSource;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.SelfUser;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public class Listener extends ListenerAdapter {


    private final CmdHandler manager;

    private SelfUser botUser;
    private User owner;
    private final JDA bot;
    private final String prefix = "+";
    private final String ownerID = "223932775474921472";

    public Listener(JDA bot) throws IOException {
        this.bot = bot;
        manager = new CmdHandler(bot);
        owner = bot.getUserById(ownerID);
    }

    @Override
    public void onReady(@Nonnull ReadyEvent event) {
        botUser = event.getJDA().getSelfUser();
    }

    @Override
    public void onPrivateMessageReceived(@NotNull PrivateMessageReceivedEvent event) {
        User user = event.getAuthor();

        if (user == botUser) {
            return;
        }

        if (owner == null && user.getId().equals(ownerID)) {
            owner = user;
            sendDM(owner, "linked you boss");
        } else if (owner == null) {
            owner = bot.getUserById(ownerID);
            if (owner == null) {
                sendDM(user, "sry dm aren't supported until i find my owner somewhere");
                return;
            }
        }
    }

    @Override
    public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent event) {

        User user = event.getAuthor();

        if (user.isBot() || event.isWebhookMessage()) {
            return;
        }


        final long guildId = event.getGuild().getIdLong();
        final String guildName = event.getGuild().getName();
        String prefix = PrefixMap.PREFIXES.computeIfAbsent(guildId, (id) -> getPrefix(guildId, guildName));
        String raw = event.getMessage().getContentRaw();


        // starts with prefix -> send to command handler
        if (raw.startsWith(prefix)) {
            manager.handle(event, prefix);
        }
    }

    User getOwner() {
        if (owner == null){
            return bot.getUserById(ownerID);
        }
        return owner;
    }

    private String getPrefix(long guildId, String guildName) {
        try (final PreparedStatement preparedStatement = SQLiteDataSource
                .getConnection()
                //language-SQLite
                .prepareStatement("SELECT prefix FROM guild_settings WHERE guild_id = ?")) {

            preparedStatement.setString(1, String.valueOf(guildId));

            try (final ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getString("prefix");
                }
            }

            try (final PreparedStatement insertStatement = SQLiteDataSource
                    .getConnection()
                    //language-SQLite
                    .prepareStatement("INSERT INTO guild_settings (guild_id, guild_name) VALUES (?, ?)")) {

                insertStatement.setString(1, String.valueOf(guildId));
                insertStatement.setString(2, guildName);

                insertStatement.execute();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return prefix;
    }


    void sendDM(User user, String message) {
        user.openPrivateChannel().complete().sendMessage(message).queue();
    }
   
}
