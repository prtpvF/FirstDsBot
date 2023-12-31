import Util.CustomFileReader;
import com.sun.net.httpserver.HttpServer;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;
import javax.security.auth.login.LoginException;
import java.net.InetSocketAddress;
import java.util.concurrent.atomic.AtomicBoolean;

public class Bot extends ListenerAdapter {
    private static Message  message;
    private JDA jda;
    public void stopBot() {
        jda.shutdownNow();

    }

    public void startBotWithNewToken(String token) throws InterruptedException, LoginException {

         jda = JDABuilder.createDefault(token)
                .enableIntents(GatewayIntent.GUILD_MESSAGES,GatewayIntent.GUILD_MEMBERS)
                .setActivity(Activity.playing("Fighting with Changpeng Zhao"))
                .build();

        jda.awaitReady();
        CustomFileReader reader = new CustomFileReader();
        String guildId = reader.getGuildId();
       EditMessageHandler editMessageHandler = new EditMessageHandler(jda);
        MessageHandler messageHandler = new MessageHandler(jda);
        RoleHandler roleHandler = new RoleHandler();
        ReactionHandler reactionHandler = new ReactionHandler(message);
        ScheduledMessageSender scheduledMessageSender = new ScheduledMessageSender(jda);
        AtomicBoolean terminateThreads = new AtomicBoolean(false); // Флаг для завершения потоков

        Thread messageHandlerThread = new Thread(() -> {
            jda.addEventListener(messageHandler);
            while (!terminateThreads.get()) {

            }
            jda.removeEventListener(messageHandler);
        });

        Thread messageEditHandler = new Thread(() -> {
            jda.addEventListener(editMessageHandler);
            while (!terminateThreads.get()) {

            }
            jda.removeEventListener(editMessageHandler);
        });

        Thread scheludeMessageThread = new Thread(() -> {
            jda.addEventListener(scheduledMessageSender);
            while (!terminateThreads.get()) {
                // Ваша логика для messageHandler
            }
            jda.removeEventListener(scheduledMessageSender);
        });

        Thread roleHandlerThread = new Thread(() -> {
            jda.addEventListener(roleHandler);
            while (!terminateThreads.get()) {
                // Ваша логика для roleHandler
            }
            jda.removeEventListener(roleHandler);
        });



        Thread reactionHandlerThread = new Thread(() -> {
            jda.addEventListener(reactionHandler);
            while (!terminateThreads.get()) {
                // Ваша логика для roleHandler
            }
            jda.removeEventListener(roleHandler);
        });





        // Добавьте аналогичные потоки и обработчики для других событий

        messageHandlerThread.start();
        roleHandlerThread.start();
        reactionHandlerThread.start();
        messageEditHandler.start();


    }

    public static void main(String[] args) throws Exception {
        Bot bot = new Bot();
        String token="";

        String portStr = System.getenv("PORT");
        int port = (portStr != null) ? Integer.parseInt(portStr) : 8081;
        // Создаем HTTP-сервер для "привязки" к порту
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);

        server.start();
        //Id сервера
        CustomFileReader reader = new CustomFileReader();
        String guildId = reader.getGuildId();
        token = reader.getBotToken();
        System.out.println("Токен: " + token);
        bot.startBotWithNewToken(token);
    }
}