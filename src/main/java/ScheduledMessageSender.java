import Util.CustomFileReader;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.io.*;
import java.time.*;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ScheduledMessageSender extends ListenerAdapter {
    private ScheduledExecutorService scheduler;
    private JDA jda;
    private Guild guild;


    public ScheduledMessageSender(JDA jda, Guild guild) {
        this.jda = jda;
        this.guild = guild;
        this.scheduler = Executors.newScheduledThreadPool(1);

        // Запуск задачи для отправки сообщений
        startScheduledTask();

        // Запланировать выполнение задачи для обновления данных каждый день
        scheduleDailyUpdate();
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        String[] command = event.getMessage().getContentRaw().split(" ");
        if (command.length == 1 && command[0].isEmpty()) {
            // Если приходит пустая команда, начнем отправку запланированных сообщений
            startScheduledTask();
        }
    }

    private void startScheduledTask() {
        // Остановить предыдущую задачу, если она выполнялась
        if (!scheduler.isShutdown()) {
            scheduler.shutdownNow();
        }

        // Создать новый ScheduledExecutorService
        scheduler = Executors.newScheduledThreadPool(1);

        // Создать новую задачу для отправки сообщений
        Runnable task = () -> {
            ZonedDateTime now = ZonedDateTime.now(ZoneId.of("UTC")); // Текущее время в UTC
            try (BufferedReader reader = new BufferedReader(new FileReader("messageStorage.txt"))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String roleName = readRoleFromLine(line);
                    String messageContent = readMessageContentFromLine(line);
                    LocalTime sendTime = readSendTimeFromLine(line);

                    if (roleName != null && messageContent != null && sendTime != null) {
                        Role role = guild.getRolesByName(roleName, true).get(0);


                            // Сравниваем время отправки с текущим временем и датой
                            LocalTime currentTime = LocalTime.now();
                            if (sendTime.isBefore(currentTime)) {
                                sendTime = sendTime.plusHours(24); // Переносим на следующий день
                            }

                            long delayMillis = calculateDelay(currentTime, sendTime);
                            scheduler.schedule(() -> sendMessage(role, messageContent), delayMillis, TimeUnit.MILLISECONDS);
                        }


                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (DateTimeParseException e) {
                e.printStackTrace();
            }
            System.out.println("All messages scheduled.");
        };
        task.run();
    }

    void sendMessage(Role role, String messageContent) {
        CustomFileReader reader = new CustomFileReader();
        String channelId = reader.GetId(4);
        TextChannel channel = guild.getTextChannelById(channelId);
        DayOfWeek currentDayOfWeek = LocalDate.now().getDayOfWeek();
        if(currentDayOfWeek == DayOfWeek.SATURDAY | currentDayOfWeek == DayOfWeek.SUNDAY){
            channel.sendMessage(" ");
            System.out.println("суббота/воскресенье, сообщение не будет оптравлено");
        }
        else if (channel != null) {
            channel.sendMessage(role.getAsMention() + "\n" + messageContent).queue();
            LocalTime time = LocalTime.now();
            System.out.println("сообщение для Роли из файла отправлено, время отправки " + time);
        }
    }

    private String readRoleFromLine(String line) {
        int start = line.indexOf('"');
        if (start != -1) {
            return line.substring(0, start).trim();
        }
        return null;
    }

    private String readMessageContentFromLine(String line) {
        int start = line.indexOf('"');
        int end = line.lastIndexOf('"');
        if (start != -1 && end != -1) {
            return line.substring(start + 1, end);
        }
        return null;
    }

    private LocalTime readSendTimeFromLine(String line) {
        int start = line.lastIndexOf(' ');
        if (start != -1) {
            try {
                return LocalTime.parse(line.substring(start).trim());
            } catch (DateTimeParseException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private long calculateDelay(LocalTime currentTime, LocalTime sendTime) {
        long delayMillis = TimeUnit.SECONDS.toMillis(currentTime.until(sendTime, TimeUnit.SECONDS.toChronoUnit()));
        if (delayMillis < 0) {
            delayMillis += TimeUnit.HOURS.toMillis(24);
        }
        return delayMillis;
    }

    private void scheduleDailyUpdate() {
        ZonedDateTime now = ZonedDateTime.now(ZoneId.of("UTC"));
        ZonedDateTime nextDay = now.plusDays(1).with(LocalTime.MIDNIGHT);
        long initialDelay = now.until(nextDay, ChronoUnit.MILLIS);

        scheduler.scheduleAtFixedRate(this::startScheduledTask, initialDelay, 24 * 60 * 60 * 1000, TimeUnit.MILLISECONDS);
    }
}
