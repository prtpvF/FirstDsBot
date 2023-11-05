import Util.CustomFileReader;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.security.auth.login.LoginException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AddAllIdHandler extends ListenerAdapter {
    private CustomFileReader fileReader = new CustomFileReader();
    private ExecutorService executor = Executors.newSingleThreadExecutor();
    private JDA jda;

    public AddAllIdHandler(JDA jda) {
        this.jda = jda;
    }
    public void restartBot(String token) throws LoginException, InterruptedException {
        Bot bot = new Bot();
        jda.shutdown();
        bot.startBotWithNewToken(token);
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        String[] command = event.getMessage().getContentRaw().split(" ");

        String token = fileReader.getBotToken();

        if (command[0].equalsIgnoreCase(".setAdminChannel") && command.length > 1) {
            String newAdminChannelId = command[1];
            executor.submit(() -> fileReader.setAdminChannelId(newAdminChannelId));
            event.getChannel().sendMessage("ID административного канала обновлен.").queue();
            try {
                restartBot(token);
            } catch (LoginException e) {
                throw new RuntimeException(e);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        } else if (command[0].equalsIgnoreCase(".setGuildId") && command.length > 1) {
            String newGuildId = command[1];
            executor.submit(() -> fileReader.setGuildId(newGuildId));
            event.getChannel().sendMessage("ID сервера обновлен.").queue();
            try {
                restartBot(token);
            } catch (LoginException e) {
                throw new RuntimeException(e);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        } else if (command[0].equalsIgnoreCase(".setBotToken") && command.length > 1) {
            String newBotToken = command[1];
            executor.submit(() -> fileReader.setBotToken(newBotToken));
            event.getChannel().sendMessage("Токен бота обновлен.").queue();
            try {
                restartBot(token);
            } catch (LoginException e) {
                throw new RuntimeException(e);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        } else if (command[0].equalsIgnoreCase(".setMessageChannel") && command.length > 1) {
            String newMessageChannelId = command[1];
            executor.submit(() -> fileReader.setMessageChannelId(newMessageChannelId));
            event.getChannel().sendMessage("ID канала сообщений обновлен.").queue();
            try {
                restartBot(token);
            } catch (LoginException e) {
                throw new RuntimeException(e);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
