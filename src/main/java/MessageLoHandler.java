import Util.Answers;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MessageLoHandler extends ListenerAdapter {
        private static final String CHANNEL_ID = "1151906690233540778"; // Замените на ID вашего канала
        private static final String ROLE_NAME = "LO"; // Название роли
    private boolean isRunning = false;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    Answers answers = new Answers();

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        String[] command = event.getMessage().getContentRaw().split(" ");
        if (command.length == 1 && command[0].equalsIgnoreCase(".start")) {
            startSendingMessages(event.getGuild());
            event.getChannel().sendMessage("Бот начал отправку сообщений каждый день в определенное время. Используйте .stop, чтобы остановить бота.").queue();
        } else if (command.length == 1 && command[0].equalsIgnoreCase(".stop")) {
            scheduler.shutdownNow();
            event.getChannel().sendMessage("Бот остановлен.").queue();
        }
    }

    private void startSendingMessages(Guild guild) {
        System.out.println("Starting to send messages...");

        // Задайте время, когда бот должен отправить сообщения
        LocalTime[] sendTimes = {
                LocalTime.of(9, 0), // Пример времени (12:00)
                LocalTime.of(9, 25), // Пример времени (15:00)
                // Добавьте дополнительные времена сюда
                LocalTime.of(9, 55),  // Пример времени (09:00)
                LocalTime.of(10, 55), // Пример времени (18:00)
                LocalTime.of(11,45),
        };

        List<String> amAnswers = answers.getLO_Answers();

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