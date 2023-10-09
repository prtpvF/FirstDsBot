import Util.Answers;
import Util.ID;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.time.*;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MemberMessageHandler extends ListenerAdapter {

    private static final String ROLE_NAME = "member"; // Название роли
    private static ID all_id;
    private final ScheduledExecutorService scheduler;
    private Answers answers = new Answers();
    boolean isRunning;
    private JDA jda;
    private Guild guild;
    private ZonedDateTime lastExecution = null;
    private  LocalTime[] sendTimes = {
            LocalTime.of(8, 30),
            LocalTime.of(15, 0),
            LocalTime.of(23, 0),
    };

    public MemberMessageHandler(JDA jda, Guild guild) {
        this.jda = jda;
        this.guild = guild;
        this.scheduler = Executors.newScheduledThreadPool(1);

        List<String> memberAnswers = answers.getMEMBER_Answers();

        Runnable task = () -> {
            ZonedDateTime now = ZonedDateTime.now(ZoneId.of("UTC")); // Текущее время в UTC

            if (lastExecution == null || now.getDayOfWeek() != lastExecution.getDayOfWeek()) {
                // Если это первый запуск или начался новый день, начинаем новый цикл
                lastExecution = now;
                LocalTime currentTime = LocalTime.now();
                for (int i = 0; i < sendTimes.length; i++) {
                    LocalTime sendTime = sendTimes[i];

                    // Если sendTime меньше или равно текущему времени, переносим на следующий день
                    if (sendTime.isBefore(currentTime) || sendTime.equals(currentTime)) {
                        sendTime = sendTime.plusHours(24);
                    }

                    if (i < memberAnswers.size()) {
                        String message = memberAnswers.get(i);
                        long delayMillis = calculateDelay(currentTime, sendTime);

                        // Планируем задачу
                        scheduler.schedule(() -> sendMessage(message), delayMillis, TimeUnit.MILLISECONDS);
                    }
                }
                System.out.println("All messages scheduled.");
            }
        };

        // Запускаем задачу для отправки сообщений
        task.run();
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {


        String[] command = event.getMessage().getContentRaw().split(" ");
        if (command.length == 1 && command[0].equalsIgnoreCase(".stop")) {
            isRunning = false; // Устанавливаем флаг в false, чтобы указать, что бот остановлен
            event.getChannel().sendMessage("Бот остановлен.").queue();
        } else {
            // Отправка сообщения
            LocalTime currentTime = LocalTime.now();
            List<String> memberAnswers = answers.getMEMBER_Answers();

            for (int i = 0; i < sendTimes.length; i++) {
                LocalTime sendTime = sendTimes[i];

                // Если sendTime меньше или равно текущему времени, переносим на следующий день
                if (sendTime.isBefore(currentTime) || sendTime.equals(currentTime)) {
                    sendTime = sendTime.plusHours(24);
                }

                if (i < memberAnswers.size()) {
                    long delayMillis = calculateDelay(currentTime, sendTime);

                    // Добавьте здесь логику отправки сообщения с учетом задержки
                    if (delayMillis > 0) {
                        try {
                            TimeUnit.MILLISECONDS.sleep(delayMillis);
                            sendMessage(memberAnswers.get(i));
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }

    private void sendMessage(String message) {
        Guild guild = jda.getGuildById("1147457730110558310"); // Замените на ID вашего сервера
        Role role = guild.getRolesByName(ROLE_NAME, true).get(0);
        TextChannel channel = guild.getTextChannelById("1151906690233540778");
        DayOfWeek currentDayOfWeek = LocalDate.now().getDayOfWeek();

        // Проверьте, если текущий день суббота или воскресенье
        if(currentDayOfWeek != DayOfWeek.MONDAY || currentDayOfWeek != DayOfWeek.TUESDAY){
            channel.sendMessage(" ");
            System.out.println("понедельник/вторник, сообщение не будет оптравлено");
        }
         else if(channel != null  ) {
            channel.sendMessage("@everyone\n" + message).queue();
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
