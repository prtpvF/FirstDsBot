import Util.Checks;
import Util.CustomFileReader;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.security.auth.login.LoginException;
import java.io.*;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class MessageHandler extends ListenerAdapter {
    private String storageFile = "messageStorage.txt";
    private CustomFileReader reader = new CustomFileReader();
    private JDA jda;
    private Map<String, List<String>> roleMessagesMap;
    private final Object fileLock = new Object();
    private ExecutorService executor = Executors.newFixedThreadPool(1);



    public MessageHandler(JDA jda) {
        this.jda =jda;
        roleMessagesMap = loadMessagesFromFile();
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        String messageContent = event.getMessage().getContentRaw();
        TextChannel channel = event.getGuild().getTextChannelById(reader.GetId(3));
        Checks checks = new Checks();
        Bot bot = new Bot();
        if (event.getChannel() == channel) {
            if (checks.isAdmin(event.getMember())) {
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

                    ScheduledMessageSender scheduledMessageSender = new ScheduledMessageSender(jda);
                    scheduledMessageSender.cancelAllTasks();

                    // Отправка сообщения об успешном завершении


                    jda.shutdown();
                    try {
                        bot.startBotWithNewToken(reader.getBotToken());
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    } catch (LoginException e) {
                        System.out.println("ошибка считывания токена" + e.getMessage());
                    }
                }
                else {
                    Guild guild = event.getGuild();
                    CustomFileReader reader = new CustomFileReader();
                    String adminChannelId = reader.GetId(2);
                    TextChannel textChannel = guild.getTextChannelById(adminChannelId);
                    textChannel.sendMessage("сообщение пустое");
                }
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


}