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

public class Main  extends ListenerAdapter {

    private static Message message;
    public static void main(String[] args) throws Exception {

        String portStr = System.getenv("PORT");
        int port = (portStr != null) ? Integer.parseInt(portStr) : 8080;

        // Создаем HTTP-сервер для "привязки" к порту
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);

        // Запускаем HTTP-сервер (в данном случае, он не делает ничего, просто "занимает" порт)
        server.start();
        JDA jda = JDABuilder.createDefault("MTE1MTI0ODM2ODQ1NTEzMTE2Ng.Gdjld9.aU8tWxEuhQs-ESMSv4SKRifD3a9j7x32gXFibw")
                .enableIntents(GatewayIntent.GUILD_MESSAGES) // Для сообщений в серверных чатах

                .setActivity(Activity.playing("Fight with shadow"))
                .build();

        jda.awaitReady();

        // Получаем текстовый канал, в котором вы хотите отправить сообщение
        TextChannel textChannel = jda.getCategories().get(1).getTextChannels().get(0);


        Guild guild = jda.getGuildById("1147457730110558310");

        // Добавляем обработчик реакций для этого сообщения



        jda.addEventListener(new ReactionHandler(message));
        jda.addEventListener(new MessageLoHandler(jda,guild));
        jda.addEventListener(new MessageAmHandler(jda, guild));
        jda.addEventListener(new MessagePmHandler(jda, guild));
        jda.addEventListener(new MemberMessageHandler(jda, guild));

    }






}

