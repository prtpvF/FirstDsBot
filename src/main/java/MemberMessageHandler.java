import Util.Answers;
import Util.ID;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MemberMessageHandler extends ListenerAdapter {
    private static final String ROLE_NAME = "member"; // Название роли
    private static ID all_id;
    private Guild guild;
    private boolean isRunning;
    private ZonedDateTime lastExecution = null;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private Answers answers = new Answers();
    private JDA jda;

    public MemberMessageHandler(JDA jda, Guild guild) {
        this.jda = jda;
        this.guild = guild;
        // Запускаем отправку сообщений при создании объекта

    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        String[] command = event.getMessage().getContentRaw().split(" ");
        if (command.length == 1 && command[0].equalsIgnoreCase(".start")) {
            if (!isRunning) { // Проверяем, не запущен ли бот уже
                startSendingMessages(event.getGuild());
                isRunning = true; // Устанавливаем флаг в true, чтобы указать, что бот запущен

            }
        } else if (command.length == 1 && command[0].equalsIgnoreCase(".stop")) {
            scheduler.shutdownNow();
            isRunning = false; // Устанавливаем флаг в false, чтобы указать, что бот остановлен
            event.getChannel().sendMessage("Бот остановлен.").queue();
        }
    }


    private void startSendingMessages(Guild guild) {
        System.out.println("Starting to send messages...");

        // Задайте время, когда бот должен отправить сообщения
        LocalTime[] sendTimes = {
                LocalTime.of(8, 30),
                LocalTime.of(15, 0),
                LocalTime.of(23, 0),
        };

        List<String> memberAnswers = answers.getMEMBER_Answers();

        Runnable task = () -> {
            ZonedDateTime now = ZonedDateTime.now(ZoneId.of("UTC")); // Текущее время в UTC

            if (lastExecution == null || now.getDayOfWeek() != lastExecution.getDayOfWeek()) {
                // Если это первый запуск или начался новый день, начинаем новый цикл
                lastExecution = now;
                LocalTime currentTime = LocalTime.now();
                for (int i = 0; i < sendTimes.length; i++) {
                    LocalTime sendTime = sendTimes[i];
                    String message = memberAnswers.get(i);

                    // Если sendTime меньше или равно текущему времени, переносим на следующий день
                    if (sendTime.isBefore(currentTime) || sendTime.equals(currentTime)) {
                        sendTime = sendTime.plusHours(24);
                    }

                    long delayMillis = calculateDelay(currentTime, sendTime);

                    // Планируем задачу
                    scheduler.schedule(() -> sendMessage(message), delayMillis, TimeUnit.MILLISECONDS);
                }
                System.out.println("All messages scheduled.");
            }
        };

        // Запускаем задачу для отправки сообщений
        task.run();
    }


    private void sendMessage(String message) {
        Guild guild = jda.getGuildById("1147457730110558310"); // Замените на ID вашего сервера
        Role role = guild.getRolesByName(ROLE_NAME, true).get(0);
        TextChannel channel = guild.getTextChannelById("1151906690233540778");
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
