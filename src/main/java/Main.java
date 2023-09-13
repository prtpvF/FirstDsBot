import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Category;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.requests.GatewayIntent;

import javax.security.auth.login.LoginException;
import java.util.List;
import java.util.concurrent.TimeUnit;
import ReactionHandler.ReactionHandler;

public class Main {

    public static void main(String[] args) throws Exception {
        JDA jda = JDABuilder.createDefault("MTE1MTI0ODM2ODQ1NTEzMTE2Ng.GUisQG.6CoRgi4N0UJ6zGUqHsHOmN-UlgCmstnswhzHMw")
                .enableIntents(GatewayIntent.GUILD_MESSAGES) // Для сообщений в серверных чатах
                .setActivity(Activity.playing("Fight with shadow"))
                .build();

        jda.awaitReady();

        // Получаем текстовый канал, в котором вы хотите отправить сообщение
        TextChannel textChannel = jda.getCategories().get(1).getTextChannels().get(0);

        // Отправляем сообщение и сохраняем его в переменной
        Message message = textChannel.sendMessage("Hello").complete();

        // Добавляем обработчик реакций для этого сообщения
        jda.addEventListener(new ReactionHandler(message));




    }
}

