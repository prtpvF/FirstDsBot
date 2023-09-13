import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Category;
import net.dv8tion.jda.api.entities.TextChannel;

import javax.security.auth.login.LoginException;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) throws Exception {
        JDA jda = JDABuilder.createDefault("MTE1MTI0ODM2ODQ1NTEzMTE2Ng.GhQO0S.9s5tPv2CxtC7pETl2SIjqiLUvkJW0ulxI1cFJ0")
                .build();
        jda.awaitReady();
        List<TextChannel> textChannels = jda.getTextChannels();
        if (!textChannels.isEmpty()) {
            TextChannel targetTextChannel = textChannels.get(0); // Выбираем первый доступный текстовый канал
            targetTextChannel.sendMessage("TET")
                    .timeout(5, TimeUnit.SECONDS)
                    .submit();
        } else {
            System.out.println("На сервере нет доступных текстовых каналов.");
        }
    }
}
