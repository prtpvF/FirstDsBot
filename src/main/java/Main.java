import Util.ID;
import com.sun.net.httpserver.HttpServer;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;

import ReactionHandler.ReactionHandler;

import java.net.InetSocketAddress;

public class Main  extends ListenerAdapter {

    public static void main(String[] args) throws Exception {

        String portStr = System.getenv("PORT");
        int port = (portStr != null) ? Integer.parseInt(portStr) : 8080;

        // Создаем HTTP-сервер для "привязки" к порту
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);

        // Запускаем HTTP-сервер (в данном случае, он не делает ничего, просто "занимает" порт)
        server.start();
        JDA jda = JDABuilder.createDefault("MTE1MTI0ODM2ODQ1NTEzMTE2Ng.GEZQI6.9wHqwsj4w8mtlWtrWi2_WXxB9SrMBPsYjr5HcI")
                .enableIntents(GatewayIntent.GUILD_MESSAGES) // Для сообщений в серверных чатах

                .setActivity(Activity.playing("Fight with shadow"))
                .build();

        jda.awaitReady();

        // Получаем текстовый канал, в котором вы хотите отправить сообщение
        TextChannel textChannel = jda.getCategories().get(1).getTextChannels().get(0);

        // Отправляем сообщение и сохраняем его в переменной
        Message message = textChannel.sendMessage("На сервер поселелился ОТТ бот." +'\n'
                                                    +"он подскажет как лучше распределить торговое время." +'\n'
                                                    + "чтобы он упоминал тебя когда ты за чартами, отреагируй на это сообщение: " +'\n'
                                                    + "LO - если торгуешь Лондон" +'\n'
                                                    + "AM - если торгуешь утро Нью-Йорка" +'\n'
                                                    + "PM -  если мучаешь вечернюю Нью-Йоркскую сессию").complete();
        Guild guild = jda.getGuildById("1147457730110558310");

        // Добавляем обработчик реакций для этого сообщения



        jda.addEventListener(new ReactionHandler(message));
        jda.addEventListener(new MessageLoHandler(jda,guild));
        jda.addEventListener(new MessageAmHandler(jda, guild));
        jda.addEventListener(new MessagePmHandler(jda, guild));
        jda.addEventListener(new MemberMessageHandler(jda, guild));

    }

}

