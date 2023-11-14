import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import Util.Checks;
import Util.CustomFileReader;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class EditMessageHandler extends ListenerAdapter {
    private String storageFile = "messageStorage.txt";
    private CustomFileReader reader = new CustomFileReader();
    private JDA jda;
    ArrayList<String> messages = new ArrayList<>();

    public EditMessageHandler(JDA jda){
        this.jda = jda;
    }
    @Override
    public void onMessageReceived(MessageReceivedEvent event) {

        Checks checks = new Checks();
        String messageContent = event.getMessage().getContentRaw();
        TextChannel channel = event.getGuild().getTextChannelById(reader.GetId(3));
        if (event.getChannel() == channel && checks.isAdmin(event.getMember()) && messageContent.startsWith(".editMessage ")) {
            System.out.println("прошло проверку");
            String[] command = messageContent.split(" ", 3);
            if (command.length >= 2) {
                System.out.println("метод прошло проверку 2");
                String role = command[1].toUpperCase();

                getMessageForRoleFromFile(channel, role, messages);

            }
        }
        else if(event.getChannel() == channel && checks.isAdmin(event.getMember()) && messageContent.startsWith(".changeMessage ")){
            String[] command = messageContent.split(" ", 3);
            if (command.length >= 3) {
                System.out.println(messages.size());
                int messageId = Integer.parseInt(command[1]);
                String defaultMessage =  messages.get(1);
                String newMessage = readMessageContentFromLine(command[2]);
                messages.set(messageId,newMessage);
                System.out.println(defaultMessage);
                System.out.println(newMessage);

            }
        }

    }

    public void getMessageForRoleFromFile(TextChannel channel, String role, List<String> messages) {
        try (BufferedReader reader = new BufferedReader(new FileReader(storageFile))) {
            String currentLine;
            while ((currentLine = reader.readLine()) != null) {
                if (currentLine.contains(role)) {
                    String content = readMessageContentFromLine(currentLine);
                    if (content != null) {
                        messages.add(content);
                    }
                }
            }
            if (!messages.isEmpty()) {
                for (String message : messages) {
                    channel.sendMessage(message).queue();
                }
            } else {
                channel.sendMessage("Сообщений для роли не найдено.").queue();
            }
        } catch (IOException e) {
            channel.sendMessage("Произошла ошибка при чтении файла: " + e.getMessage()).queue();
            e.printStackTrace();
        }
    }
    private String readMessageContentFromLine(String line) {
        int start = line.indexOf('"');
        int end = line.lastIndexOf('"');
        if (start != -1 && end != -1 && end > start) {
            return line.substring(start + 1, end);
        }
        return null;
    }



}
