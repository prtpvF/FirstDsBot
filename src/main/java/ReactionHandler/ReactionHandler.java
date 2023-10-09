package ReactionHandler;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReactionHandler extends ListenerAdapter {
    private Message message;
    private Map<Long, Message> adminMessages = new HashMap<>();
    private List<Role> addedRoles = new ArrayList<>();
    private List<Role> removedRoles = new ArrayList<>();
    private boolean isStarted = false;

    private long adminMessageId = -1;

    public ReactionHandler(Message message) {
        this.message = message;
        loadAdminMessageId();
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        String[] command = event.getMessage().getContentRaw().split(" ");
        if (command.length > 0 && command[0].equalsIgnoreCase(".message")) {
            if (isAdmin(event.getMember())) {
                if (command.length > 1) {
                    String adminMessage = String.join(" ", command).substring(".message".length()).trim();
                    Message message = event.getChannel().sendMessage(adminMessage).complete();
                    adminMessages.put(message.getIdLong(), message);
                    setAdminMessageId(message.getIdLong()); // Сохраняем ID админского сообщения
                } else {
                    event.getChannel().sendMessage("Пожалуйста, укажите сообщение после команды `.message`.").queue();
                }
            } else {
                event.getChannel().sendMessage("У вас нет прав на использование этой команды.").queue();
            }
        }
    }

    @Override
    public void onMessageReactionAdd(MessageReactionAddEvent event) {
        long adminMessageId = getAdminMessageIdFromTextFile(); // Получаем ID админского сообщения из текстового файла
        if (event.getMessageIdLong() == adminMessageId) {
            String emojiName = event.getReactionEmote().getName();
            Member member = event.getMember();
            Guild guild = event.getGuild();
            String name = emojiName.toUpperCase();
            Role currentRole=guild.getRolesByName(name, true).get(0);
            guild.addRoleToMember(member, currentRole).queue();

            if (emojiName.equals("pm")) {
                Role role = guild.getRolesByName("PM", true).get(0);
                guild.addRoleToMember(member, role).queue();
            } else if (emojiName.equals("lo")) {
                Role role = guild.getRolesByName("LO", true).get(0);
                guild.addRoleToMember(member, role).queue();
            } else if (emojiName.equals("am")) {
                Role role = guild.getRolesByName("AM", true).get(0);
                guild.addRoleToMember(member, role).queue();
            }
        }
    }

    @Override
    public void onMessageReactionRemove(MessageReactionRemoveEvent event) {
        long adminMessageId = getAdminMessageIdFromTextFile(); // Получаем ID админского сообщения из текстового файла
        if (event.getMessageIdLong() == adminMessageId) {
            String emojiName = event.getReactionEmote().getName();
            long userId = event.getUserIdLong();
            Guild guild = event.getGuild();
            String name = emojiName.toUpperCase();
            Role currentRole=guild.getRolesByName(name, true).get(0);
            guild.removeRoleFromMember(userId, currentRole).queue();
            if (emojiName.equals("pm")) {
                Role role = guild.getRolesByName("PM", true).get(0);
                guild.removeRoleFromMember(userId, role).queue();
            } else if (emojiName.equals("lo")) {
                Role role = guild.getRolesByName("LO", true).get(0);
                guild.removeRoleFromMember(userId, role).queue();
            } else if (emojiName.equals("am")) {
                Role role = guild.getRolesByName("AM", true).get(0);
                guild.removeRoleFromMember(userId, role).queue();
            }
        }
    }
    private long getAdminMessageIdFromTextFile() {
        try {
            File file = new File("adminMessageId.txt");
            if (!file.exists()) {
                file.createNewFile();
                // Здесь можно задать начальное значение ID, если файла не существовало
                // saveAdminMessageIdToFile(STARTING_ADMIN_MESSAGE_ID);
            }
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line = reader.readLine();
            if (line != null) {
                return Long.parseLong(line);
            }
            reader.close();
        } catch (IOException | NumberFormatException e) {
            e.printStackTrace();
        }
        return 0; // Возвращаем 0 в случае ошибки
    }


    private boolean isAdmin(Member member) {
        if (member == null) {
            return false;
        }
        return member.hasPermission(Permission.ADMINISTRATOR);
    }

    private void loadAdminMessageId() {
        try {
            File file = new File("adminMessageId.txt");
            if (!file.exists()) {
                file.createNewFile();
                saveAdminMessageId(); // Сохраняем начальное значение в новом файле
            }
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line = reader.readLine();
            if (line != null) {
                adminMessageId = Long.parseLong(line);
            }
            reader.close();
        } catch (IOException | NumberFormatException e) {
            e.printStackTrace();
        }
    }




    private void saveAdminMessageId() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("adminMessageId.txt"))) {
            writer.write(String.valueOf(adminMessageId));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public long getAdminMessageId() {
        return adminMessageId;
    }

    private void setAdminMessageId(long adminMessageId) {
        this.adminMessageId = adminMessageId;
        saveAdminMessageId();
    }
}
