import net.dv8tion.jda.api.entities.*;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class MessageHandler extends ListenerAdapter {

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        String messageSent = event.getMessage().getContentRaw();
        if(messageSent.equalsIgnoreCase("hello")){
            // Проверяем, является ли канал приватным
            if (event.isFromType(ChannelType.PRIVATE)) {
                // Если да, то игнорируем сообщение
                return;
            } else {
                // Если нет, то отвечаем в том же канале
                event.getChannel().sendMessage("Hi").queue();
            }
        }
    }




}
