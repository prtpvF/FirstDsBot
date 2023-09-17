import Util.Answers;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.time.LocalTime;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MessageAmHandler extends ListenerAdapter {
    private static final String CHANNEL_ID = "1151906690233540778"; // Замените на ID вашего канала
    private static final String ROLE_NAME = "AM"; // Название роли
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    Answers answers = new Answers();

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        String[] command = event.getMessage().getContentRaw().split(" ");
        if (command.length == 1 && command[0].equalsIgnoreCase(".start")) {
            startSendingMessages(event.getGuild());
        } else if (command.length == 1 && command[0].equalsIgnoreCase(".stop")) {
            scheduler.shutdownNow();

        }
    }

    private void startSendingMessages(Guild guild) {
        System.out.println("Starting to send messages...");
        LocalTime[] sendTimes = {
                LocalTime.of(15, 20),
                LocalTime.of(16, 45),
                LocalTime.of(16, 55),
                LocalTime.of(17, 45),
                LocalTime.of(18,45),
                LocalTime.of(19,0)
        };

        List<String> amAnswers = answers.getAM_Answers();

        for (int i = 0; i < sendTimes.length; i++) {
            final int index = i;
            LocalTime sendTime = sendTimes[i];
            String message = amAnswers.get(i);

            // Создаем задачу для отправки сообщения
            Runnable task = () -> {
                sendMessage(guild, message);
                System.out.println("Scheduled message " + index + " sent at: " + LocalTime.now());
            };

            // Получаем текущее время
            LocalTime currentTime = LocalTime.now();

            // Если sendTime меньше или равно текущему времени, переносим на следующий день
            if (sendTime.isBefore(currentTime) || sendTime.equals(currentTime)) {
                sendTime = sendTime.plusHours(24);
            }

            long delayMillis = calculateDelay(currentTime, sendTime);

            // Планируем задачу
            scheduler.schedule(task, delayMillis, TimeUnit.MILLISECONDS);
        }

        System.out.println("All messages scheduled.");
    }

    private void sendMessage(Guild guild, String message) {
        Role role = guild.getRolesByName(ROLE_NAME, true).get(0);
        TextChannel channel = guild.getTextChannelById(CHANNEL_ID);

        if (channel != null) {
            channel.sendMessage(role.getAsMention() + "\n" + message).queue();
        }
    }

    private long calculateDelay(LocalTime currentTime, LocalTime sendTime) {
        long delayMillis = TimeUnit.SECONDS.toMillis(currentTime.until(sendTime, TimeUnit.SECONDS.toChronoUnit()));
        if (delayMillis < 0) {
            delayMillis += TimeUnit.HOURS.toMillis(24); // Добавляем 24 часа, чтобы перенести на следующий день
        }
        return delayMillis;
    }
}

