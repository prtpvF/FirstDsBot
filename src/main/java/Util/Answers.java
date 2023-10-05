package Util;

import java.util.ArrayList;
import java.util.List;

public class Answers {
    List<String> LO_Answers = new ArrayList<>();
    {
        LO_Answers.add("-не кипишуй, осмотрись: тебе платят за ожидание!");
        LO_Answers.add("-через 5 минут начнется макрос 2:33-3:00 (utc-4) - concentrate work time(!)");
        LO_Answers.add("-через 5 минут утренний SILVER BULLET time 03:00-04:00 AM (utc-4)");
        LO_Answers.add("-через 5 минут начнется макрос 4:03-4:50 (utc-4) - в ближайшие 50 минут ты берешь позу!");
        LO_Answers.add("-чилл до AM. закрывай чарты! потрать следующие три часа на то угодно кроме чартов!");
    }
    List<String> AM_Answers = new ArrayList<>();
    {
        AM_Answers.add("-через 10 минут открытие NY, ждём манипуляцию, после которой ищем позы");
        AM_Answers.add("-через 5 минут начнется макрос 9:50-10:10 (utc-4) - ребалансировка/доставка");
        AM_Answers.add("-через 5 минут SILVER BULLET time 10:00-11:00 (utc-4)");
        AM_Answers.add("-через 5 минут начнется макрос 10:50-11:10 (utc-4) - доставка/ребалансировка");
        AM_Answers.add("-через 5 минут начнется макрос 11:50-12:10 (utc-4) - доставка/ребалансировка");
        AM_Answers.add("-ланч! закрывай или обезопась позы и отдохни");
    }
    List<String> PM_Answers = new ArrayList<>();
    {
        PM_Answers.add("-вечерняя сессия начнется через 5 минут 13:00 (utc-4). до ближайшего макроса 15 минут. где находится цена?");
        PM_Answers.add("-через 5 минут начнется макрос 13:10-13:40 (utc-4). что делает цена?");
        PM_Answers.add("-через 5 минут начнется макрос 14:10-14:40 (utc-4). куда доставляют цену?");
        PM_Answers.add("-14:50-15:05 (utc-4) свинг под ласт аур, и ралли за полкой?");
        PM_Answers.add("-это был хороший день, он подарил тебе бесценный опыт, похвали себя за знания которые сегодня приобрел!");



    }
    List<String> MEMBER_Answers = new ArrayList<>();
    {
        MEMBER_Answers.add("-доброе утро, тигр! ты знаешь своих демонов и как с ними бороться, твоя торговля становится лучше каждый день," + "\n" + "сегодня ты снова порвешь рынок!");
        MEMBER_Answers.add("-как сегодня прошла твоя медитация?");
        MEMBER_Answers.add("-это был хороший день, он подарил тебе бесценный опыт, похвали себя за знания которые сегодня приобрел!");
    }

    public List<String> getLO_Answers() {
        return LO_Answers;
    }

    public List<String> getAM_Answers() {
        return AM_Answers;
    }

    public List<String> getPM_Answers() {
        return PM_Answers;
    }

    public List<String> getMEMBER_Answers() {
        return MEMBER_Answers;
    }
}
