import Util.ID;
import com.sun.net.httpserver.HttpServer;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;

import ReactionHandler.ReactionHandler;

import java.io.*;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class Main  extends ListenerAdapter {

    private static Message message;

    public static void main(String[] args) throws Exception {
        String token="";
        String guildId="";
        String portStr = System.getenv("PORT");
        int port = (portStr != null) ? Integer.parseInt(portStr) : 8080;

        // Создаем HTTP-сервер для "привязки" к порту
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);

        // Запускаем HTTP-сервер (в данном случае, он не делает ничего, просто "занимает" порт)
        server.start();

        try(FileReader reader= new FileReader("storage.txt")){
            List<String> allId = new ArrayList<>();
            int c;
            while((c=reader.read())!=' '){
                token=token + (char)c;
            }
        }catch (IOException e){
            System.out.println(e.getMessage());
        }
        System.out.println("Токен: " + token);
        try (BufferedReader br = new BufferedReader(new FileReader("storage.txt"))) {
            // пропускаем первую строку
            br.readLine();
            // считываем вторую строку
             guildId = br.readLine();
            String[] patterns = guildId.split("-");
            guildId = patterns[0];
            System.out.println("ID сервера: " + guildId);
        } catch (IOException e) {
            System.err.println("Ошибка при чтении файла " + "storage.txt" + ": " + e.getMessage());
        }



        JDA jda = JDABuilder.createDefault(token)
                .enableIntents(GatewayIntent.GUILD_MESSAGES,GatewayIntent.GUILD_MEMBERS)
                .setActivity(Activity.playing("Fight with shadow"))
                .build();

        jda.awaitReady();

        Guild guild = jda.getGuildById(guildId);

        MessageHandler messageHandler = new MessageHandler(jda);
        RoleHandler roleHandler = new RoleHandler();
        ReactionHandler reactionHandler = new ReactionHandler(message);
        MessageLoHandler messageLoHandler = new MessageLoHandler(jda, guild);
        MessageAmHandler messageAmHandler = new MessageAmHandler(jda, guild);
        MessagePmHandler messagePmHandler = new MessagePmHandler(jda, guild);
        MemberMessageHandler memberMessageHandler = new MemberMessageHandler(jda, guild);
        ScheduledMessageSender scheduledMessageSender = new ScheduledMessageSender(jda,guild);
        AtomicBoolean terminateThreads = new AtomicBoolean(false); // Флаг для завершения потоков

        Thread messageHandlerThread = new Thread(() -> {
            jda.addEventListener(messageHandler);
            while (!terminateThreads.get()) {
                // Ваша логика для messageHandler
            }
            jda.removeEventListener(messageHandler);
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

        Thread LOHandlerThread = new Thread(() -> {
            jda.addEventListener(messageLoHandler);
            while (!terminateThreads.get()) {
                // Ваша логика для roleHandler
            }
            jda.removeEventListener(messageLoHandler);
        });
        Thread PmHandlerThread = new Thread(() -> {
            jda.addEventListener(messagePmHandler);
            while (!terminateThreads.get()) {
                // Ваша логика для roleHandler
            }
            jda.removeEventListener(messagePmHandler);
        });
        Thread AMHandlerThread = new Thread(() -> {
            jda.addEventListener(messageAmHandler);
            while (!terminateThreads.get()) {
                // Ваша логика для roleHandler
            }
            jda.removeEventListener(messageAmHandler);
        });
        Thread MemberHandlerThread = new Thread(() -> {
            jda.addEventListener(memberMessageHandler);
            while (!terminateThreads.get()) {
                // Ваша логика для roleHandler
            }
            jda.removeEventListener(memberMessageHandler);
        });

        // Добавьте аналогичные потоки и обработчики для других событий

        messageHandlerThread.start();
        roleHandlerThread.start();
        reactionHandlerThread.start();
//        LOHandlerThread.start();
//        PmHandlerThread.start();
//        AMHandlerThread.start();
//        MemberHandlerThread.start();


        // Ожидание завершения всех потоков

    }
}