import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RoleHandler extends ListenerAdapter {

    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        String[] command = event.getMessage().getContentRaw().split(" ");

        if (command.length > 0 && command[0].equalsIgnoreCase(".addRole")) {
            String roleName = String.join(" ", command).substring(".addRole".length()).trim();
            System.out.println("роль была добавлена");

            // Выполнение метода в отдельном потоке
            executor.submit(() -> {
                Guild guild = event.getGuild();
                // Создаем объект роли с указанным именем
                Role roleToAdd = guild.createRole()
                        .setName(roleName)
                        .complete(); // Блокирующий вызов для получения созданной роли

                // Добавляем созданную роль на сервер
                guild.addRoleToMember(guild.getSelfMember(), roleToAdd).queue();

                event.getChannel().sendMessage("Роль " + roleName + " успешно добавлена на сервер.").queue();
            });
        }
    }
}