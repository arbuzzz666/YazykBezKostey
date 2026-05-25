package com.example.speechapp;

import java.util.ArrayList;
import java.util.List;

/**
 * Имитация базы данных с контентом для приложения.
 * В реальном проекте здесь был бы Room Database или загрузка из удаленного источника.
 */
public class SpeechDatabase {

    /**
     * Возвращает список из 100 популярных предметов для упражнения "Описание предмета".
     */
    public static List<String> getItemsToDescribe() {
        List<String> items = new ArrayList<>();
        items.add("Яблоко");
        items.add("Карандаш");
        items.add("Очки");
        items.add("Чашка");
        items.add("Наушники");
        items.add("Книга");
        items.add("Телефон");
        items.add("Стол");
        items.add("Стул");
        items.add("Лампа");
        items.add("Зеркало");
        items.add("Часы");
        items.add("Ручка");
        items.add("Тетрадь");
        items.add("Рюкзак");
        items.add("Ключ");
        items.add("Монета");
        items.add("Свеча");
        items.add("Подушка");
        items.add("Одеяло");
        items.add("Дверь");
        items.add("Окно");
        items.add("Ковёр");
        items.add("Ваза");
        items.add("Цветок");
        items.add("Дерево");
        items.add("Камень");
        items.add("Ракушка");
        items.add("Гитара");
        items.add("Мяч");
        items.add("Шахматы");
        items.add("Кисть");
        items.add("Краски");
        items.add("Фотоаппарат");
        items.add("Велосипед");
        items.add("Автомобиль");
        items.add("Самолёт");
        items.add("Глобус");
        items.add("Компас");
        items.add("Карта");
        items.add("Микрофон");
        items.add("Колонка");
        items.add("Телевизор");
        items.add("Пульт");
        items.add("Батарейка");
        items.add("Зонт");
        items.add("Шляпа");
        items.add("Перчатки");
        items.add("Ботинки");
        items.add("Куртка");
        items.add("Шарф");
        items.add("Ожерелье");
        items.add("Браслет");
        items.add("Кольцо");
        items.add("Серьги");
        items.add("Кошелёк");
        items.add("Сумка");
        items.add("Чемодан");
        items.add("Лестница");
        items.add("Мост");
        items.add("Фонтан");
        items.add("Скамейка");
        items.add("Вентилятор");
        items.add("Обогреватель");
        items.add("Холодильник");
        items.add("Микроволновка");
        items.add("Тостер");
        items.add("Чайник");
        items.add("Ложка");
        items.add("Вилка");
        items.add("Нож");
        items.add("Тарелка");
        items.add("Кастрюля");
        items.add("Сковорода");
        items.add("Бутылка");
        items.add("Стакан");
        items.add("Градусник");
        items.add("Аптечка");
        items.add("Бинт");
        items.add("Ножницы");
        items.add("Скрепка");
        items.add("Кнопка");
        items.add("Молоток");
        items.add("Отвёртка");
        items.add("Гвоздь");
        items.add("Верёвка");
        items.add("Скотч");
        items.add("Клей");
        items.add("Магнит");
        items.add("Прищепка");
        items.add("Губка");
        items.add("Мыло");
        items.add("Полотенце");
        return items;
    }

    /**
     * Возвращает список из 30 вопросов для "Симулятора собеседования".
     */
    public static List<String> getInterviewQuestions() {
        List<String> questions = new ArrayList<>();
        questions.add("Расскажите о себе и своем опыте.");
        questions.add("Почему вы выбрали именно эту профессию?");
        questions.add("Какие ваши главные сильные стороны?");
        questions.add("Опишите свою самую большую неудачу и чему вы научились.");
        questions.add("Кем вы видите себя через 5 лет?");
        questions.add("Почему вы хотите работать именно в нашей компании?");
        questions.add("Как вы справляетесь со стрессом и давлением?");
        questions.add("Расскажите о проекте, которым вы гордитесь больше всего.");
        questions.add("Как вы организуете свое рабочее время?");
        questions.add("Что для вас важнее: деньги или интересная работа?");
        questions.add("Умеете ли вы работать в команде? Приведите пример.");
        questions.add("Что вы знаете о нашей компании?");
        questions.add("Какие книги/курсы вы изучили за последний год?");
        questions.add("Опишите идеального руководителя.");
        questions.add("Готовы ли вы к переработкам?");
        questions.add("Как вы реагируете на критику?");
        questions.add("Расскажите о конфликте на работе и как вы его разрешили.");
        questions.add("Ваше хобби? Как оно помогает в работе?");
        questions.add("Что вас мотивирует в работе?");
        questions.add("Опишите ситуацию, где вы проявили лидерские качества.");
        questions.add("Как вы принимаете сложные решения?");
        questions.add("Что бы вы изменили в своей работе на прошлом месте?");
        questions.add("Какие технологии/инструменты вы используете?");
        questions.add("Расскажите о случае, когда вы нарушили правила.");
        questions.add("Ваш главный недостаток?");
        questions.add("Что вы будете делать в первые 30 дней на новой работе?");
        questions.add("Как вы учитесь новому?");
        questions.add("Почему мы должны нанять именно вас?");
        questions.add("Опишите вашу идеальную рабочую среду.");
        questions.add("Есть ли у вас вопросы ко мне?");
        return questions;
    }

    /**
     * Возвращает список из 15 заданий для "Лексического марафона".
     */
    public static List<String> getLexicalMarathonTasks() {
        List<String> tasks = new ArrayList<>();
        tasks.add("Прилагательные на букву 'А'");
        tasks.add("Слова, заканчивающиеся на 'О'");
        tasks.add("Существительные, связанные с природой");
        tasks.add("Глаголы на букву 'П'");
        tasks.add("Слова из 5 букв");
        tasks.add("Профессии");
        tasks.add("Страны и города на букву 'М'");
        tasks.add("Фрукты и овощи");
        tasks.add("Чувства и эмоции");
        tasks.add("Слова, начинающиеся на 'СО'");
        tasks.add("Животные");
        tasks.add("Музыкальные инструменты");
        tasks.add("Слова с удвоенной согласной");
        tasks.add("Виды спорта");
        tasks.add("Кухонная утварь");
        return tasks;
    }
}