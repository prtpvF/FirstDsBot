package Util;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class CustomFileReader {
    private static final String FILE_PATH = "storage.txt";

    public String GetId(int numberOfString){
        String id="";
        try (BufferedReader br = new BufferedReader(new java.io.FileReader("storage.txt"))) {
            // пропускаем первую строку
            for (int i = 0; i < numberOfString; i++) {
                br.readLine();
            }
            id = br.readLine();

        } catch (IOException e) {
            System.err.println("Ошибка при чтении файла " + "storage.txt" + ": " + e.getMessage());
        }
        return id;
    }

    private List<String> readAllLines() {
        List<String> lines = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = br.readLine()) != null) {
                lines.add(line);
            }
        } catch (IOException e) {
            System.err.println("Ошибка при чтении файла 'storage.txt': " + e.getMessage());
        }
        return lines;
    }

    private void writeAllLines(List<String> lines) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_PATH))) {
            for (String newLine : lines) {
                bw.write(newLine);
                bw.newLine();
            }
        } catch (IOException e) {
            System.err.println("Ошибка при записи в файл 'storage.txt': " + e.getMessage());
        }
    }



    public String getGuildId(){
        String guildId="";
        try (BufferedReader br = new BufferedReader(new java.io.FileReader("storage.txt"))) {
            // пропускаем первую строку
            for (int i = 0; i < 1; i++) {
                br.readLine();
            }
            guildId = br.readLine();
            System.out.println("ID сервера: " + guildId);
        } catch (IOException e) {
            System.err.println("Ошибка при чтении файла " + "storage.txt" + ": " + e.getMessage());
        }
        return guildId;
    }

    public String getBotToken(){
        String botToken="";
        try(FileReader reader= new FileReader("storage.txt")){
            List<String> allId = new ArrayList<>();
            int c;
            while((c=reader.read())!=' '){
                botToken=botToken + (char)c;
            }
        }catch (IOException e){
            System.out.println(e.getMessage());
        }

        return botToken;
    }

    public void setBotToken(String token) {
        List<String> lines = readAllLines();
        for (int i = 0; i < lines.size(); i++) {
            if (lines.get(i).contains("токен")) {
                lines.set(i, token + " токен"); // Заменяем старую строку на новую
                break; // Выходим из цикла после первой замены
            }
        }
        writeAllLines(lines);
        System.out.println("Токен бота обновлен.");
    }
    public void setGuildId(String guildId) {
        List<String> lines = readAllLines();
        lines.set(1, guildId); // Заменяем первую строку на новое значение
        writeAllLines(lines);
        System.out.println("ID сервера обновлен.");
    }
    public void setAdminChannelId(String id) {
        List<String> lines = readAllLines();
        lines.set(2, id); // Заменяем вторую строку на новое значение
        writeAllLines(lines);
        System.out.println("ID сервера обновлен.");
    }
    public void setMessageChannelId(String id) {
        List<String> lines = readAllLines();
        lines.set(3, id); // Заменяем третью строку на новое значение
        writeAllLines(lines);
        System.out.println("ID сервера обновлен.");
    }}
