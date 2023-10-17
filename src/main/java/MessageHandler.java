import Util.Answers;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class MessageHandler extends ListenerAdapter {
    private String storageFile = "messageStorage.txt";
    private Map<String, List<String>> roleMessagesMap;
    private final Object fileLock = new Object();
    private ExecutorService executor = Executors.newFixedThreadPool(1); // Один поток для выполнения команд

    public MessageHandler() {
        roleMessagesMap = loadMessagesFromFile();
    }

    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        String messageContent = event.getMessage().getContentRaw();

        if (messageContent.startsWith(".addMessage ")) {
            String[] command = messageContent.split(" ", 3); // Разделяем сообщение на три части

            if (command.length >= 3) {
                String roleName = command[1];
                String messageAndTime = command[2];

                // Преобразуем имя роли в верхний регистр для унификации
                roleName = roleName.toUpperCase();
                Guild guild = event.getGuild();

                // Проверяем, существует ли роль с указанным именем
                Role role = guild.getRolesByName(roleName, true).stream().findFirst().orElse(null);

                if (role != null) {
                    // Роль существует, добавляем сообщение
                    String finalRoleName = roleName;
                    executor.submit(() -> addMessageForRole(finalRoleName, messageAndTime));

                    event.getChannel().sendMessage("Сообщение добавлено для роли " + roleName).queue();
                } else {
                    event.getChannel().sendMessage("Такой роли не существует!").queue();
                }
            } else {
                event.getChannel().sendMessage("Пожалуйста, используйте команду следующим образом: .addMessage \"Название роли\" сообщение-время").queue();
            }
        }
    }

    private void addMessageForRole(String roleName, String messageAndTime) {
        synchronized (fileLock) {
            try {
                PrintWriter writer = new PrintWriter(new FileWriter(storageFile, true)); // Открываем файл для добавления записей
                writer.println(roleName + " " + messageAndTime); // Добавляем новую запись в файл
                writer.close(); // Закрываем файл
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public Map<String, List<String>> loadMessagesFromFile() {
        synchronized (fileLock) {
            Map<String, List<String>> messages = new HashMap<>();
            try {
                File file = new File(storageFile);
                if (!file.exists()) {
                    file.createNewFile(); // Создать файл, если его нет
                }
                BufferedReader reader = new BufferedReader(new FileReader(file));
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(" - ");
                    if (parts.length == 2) {
                        String roleName = parts[0];
                        String messageAndTime = parts[1];
                        List<String> roleMessages = messages.computeIfAbsent(roleName, k -> new ArrayList<>());
                        roleMessages.add(roleName + " - " + messageAndTime);
                    }
                }
                reader.close(); // Закрыть reader после использования
            } catch (IOException e) {
                e.printStackTrace();
            }
            return messages;
        }
    }

    public void saveMessagesToFile() {
        synchronized (fileLock) {
            try {
                PrintWriter writer = new PrintWriter(new FileWriter(storageFile, true));
                for (Map.Entry<String, List<String>> entry : roleMessagesMap.entrySet()) {
                    for (String messageAndTime : entry.getValue()) {
                        writer.println(messageAndTime);
                    }
                }
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}