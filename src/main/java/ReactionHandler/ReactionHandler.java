package ReactionHandler;

import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;

public class ReactionHandler extends ListenerAdapter {
    private final Message message;

    public ReactionHandler(Message message) {
        this.message = message;
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
                Role role = guild.getRolesByName("если мучаешь вечернюю Нью-Йоркскую сессию", true).get(0);
                guild.addRoleToMember(member, role).queue();
            } else if (emojiName.equals("lo")) {
                // Добавляем роль "если торгуешь лондон"
                Role role = guild.getRolesByName("если торгуешь лондон", true).get(0);
                guild.addRoleToMember(member, role).queue();
            } else if (emojiName.equals("am")) {
                // Добавляем роль "если мучаешь вечернюю Нью-Йоркскую сессию"

                Role role = guild.getRolesByName("если торгуешь утром Нью-Йорка", true).get(0);
                guild.addRoleToMember(member, role).queue();
            }
        }
    }

    @Override
    public void onMessageReactionRemove(@NotNull MessageReactionRemoveEvent event) {
        if (event.getMessageIdLong() == message.getIdLong()) {
            // Получаем имя (или Unicode-символ) удаленной реакции
            String emojiName = event.getReactionEmote().getName();

            // Получаем объект пользователя
            Member member = event.getMember();

            // Получаем объект сервера
            Guild guild = event.getGuild();

            // Сравниваем имя удаленной реакции с ожидаемыми значениями и удаляем соответствующие роли
            if (emojiName.equals("pm")) {
                // Удаляем роль "если торгуешь утром Гью-Йорка"
                Role role = guild.getRolesByName("если торгуешь утром Гью-Йорка", true).get(0);
                guild.removeRoleFromMember(member, role).queue();
            } else if (emojiName.equals("lo")) {
                // Удаляем роль "если торгуешь лондон"
                Role role = guild.getRolesByName("если торгуешь лондон", true).get(0);
                guild.removeRoleFromMember(member, role).queue();
            } else if (emojiName.equals("am")) {
                // Удаляем роль "если мучаешь вечернюю Нью-Йоркскую сессию"
                Role role = guild.getRolesByName("если мучаешь вечернюю Нью-Йоркскую сессию", true).get(0);
                guild.removeRoleFromMember(member, role).queue();
            }
        }
    }
}