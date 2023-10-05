package ReactionHandler;

import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ReactionHandler extends ListenerAdapter {
    private Message message;
    private List<Role> addedRoles = new ArrayList<>();
    private List<Role> removedRoles = new ArrayList<>();
    private ScheduledExecutorService executorService;
    private boolean isStarted = false;


    public ReactionHandler(Message message) {
        this.message = message;
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        String[] command = event.getMessage().getContentRaw().split(" ");
        if (command.length > 0 && command[0].equalsIgnoreCase(".start") && !isStarted) {
            sendMessageAndAddReactions((TextChannel) event.getChannel());
            isStarted = true;
        }
    }

    private void sendMessageAndAddReactions(TextChannel channel) {
        // Отправляем сообщение и добавляем реакции
        message = channel.sendMessage("На сервер поселился ОТТ бот." + '\n'
                + "Он подскажет как лучше распределить торговое время." + '\n'
                + "Чтобы он упоминал тебя когда ты за чартами, отреагируй на это сообщение: " + '\n'
                + "LO - если торгуешь Лондон" + '\n'
                + "AM - если торгуешь утро Нью-Йорка" + '\n'
                + "PM -  если мучаешь вечернюю Нью-Йоркскую сессию").complete();

    }
    @Override
    public void onMessageReactionAdd(MessageReactionAddEvent event) {

        // Проверяем, что реакция добавлена к нужному сообщению
        if (event.getMessageIdLong() == message.getIdLong()) {
            // Получаем имя (или Unicode-символ) эмодзи, на которое пользователь нажал
            String emojiName = event.getReactionEmote().getName();

            // Получаем объект пользователя
            Member member = event.getMember();

            // Получаем объект сервера
            Guild guild = event.getGuild();

            // Сравниваем имя эмодзи с ожидаемыми значениями и добавляем роли
            if (emojiName.equals("pm")) {
                // Добавляем роль "если торгуешь утром Гью-Йорка"
                Role role = guild.getRolesByName("PM", true).get(0);
                guild.addRoleToMember(member, role).queue();
            } else if (emojiName.equals("lo")) {
                // Добавляем роль "если торгуешь лондон"
                Role role = guild.getRolesByName("LO", true).get(0);
                guild.addRoleToMember(member, role).queue();
            } else if (emojiName.equals("am")) {
                // Добавляем роль "если мучаешь вечернюю Нью-Йоркскую сессию"

                Role role = guild.getRolesByName("AM", true).get(0);
                guild.addRoleToMember(member, role).queue();
            }
        }

    }

    @Override
    public void onMessageReactionRemove(MessageReactionRemoveEvent event) {
        // Проверяем, что реакция была удалена из нужного сообщения
        if (event.getMessageIdLong() == message.getIdLong()) {
            // Получаем имя (или Unicode-символ) удаленной реакции
            String emojiName = event.getReactionEmote().getName();

            // Получаем ID пользователя
            long userId = event.getUserIdLong();

            // Получаем объект сервера
            Guild guild = event.getGuild();

            // Сравниваем имя удаленной реакции с ожидаемыми значениями и удаляем соответствующие роли
            if (emojiName.equals("pm")) {
                // Удаляем роль "если торгуешь утром Гью-Йорка"
                Role role = guild.getRolesByName("PM", true).get(0);
                guild.removeRoleFromMember(userId, role).queue();
            } else if (emojiName.equals("lo")) {
                // Удаляем роль "если торгуешь лондон"
                Role role = guild.getRolesByName("LO", true).get(0);
                guild.removeRoleFromMember(userId, role).queue();
            } else if (emojiName.equals("am")) {
                // Удаляем роль "если мучаешь вечернюю Нью-Йоркскую сессию"
                Role role = guild.getRolesByName("AM", true).get(0);
                guild.removeRoleFromMember(userId, role).queue();
            }
        }
    }

    }

