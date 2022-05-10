package main.tools;


import company.evo.jmorphy2.MorphAnalyzer;
import company.evo.jmorphy2.ParsedWord;
import company.evo.jmorphy2.ResourceFileLoader;
import lombok.extern.log4j.Log4j;
import main.entities.NewsSiteData;

import java.io.IOException;
import java.util.*;

@Log4j
public class TextTools {
    private static Map<String, Integer> DICTIONARY = new HashMap<>();
    private static MorphAnalyzer morph = null;

    public void init() {
        try {
            morph = new MorphAnalyzer.Builder()
                    .fileLoader(new ResourceFileLoader(String.format("/company/evo/jmorphy2/%s/pymorphy2_dicts", "ru")))
                    .charSubstitutes(null)
                    .cacheSize(0)
                    .build();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    public List<String> textToNormalForm(NewsSiteData data) {
        String text = data.getTitle() + " " + data.getText();
        ArrayList<String> words = new ArrayList<>(Arrays.asList(text.replaceAll("[^а-яА-ЯёЁ ]", "").toLowerCase().split(" ")));
        words.removeIf(s -> s.equals(""));

        ArrayList<String> wordsNorm = new ArrayList<>();
        String normalForm;
        List<ParsedWord> pwList;
        for (String word : words) {
            try {
                pwList = morph.parse(word);
            } catch (Exception e) {
                log.warn("Can't get normal form for: " + word + ", reason: " + e.getMessage());
                continue;
            }
            if (pwList.size() != 0)
                normalForm = pwList.get(0).normalForm;
            else {
                log.error("Can't make morph analyze for: " + word);
                continue;
            }
            wordsNorm.add(normalForm);
        }
        return wordsNorm;
    }

    public int getEmotionalRate(List<String> words) {
        return words.stream().filter(word -> DICTIONARY.containsKey(word)).mapToInt(word -> DICTIONARY.get(word)).sum();
    }

    static {
        DICTIONARY.put("акция", 1);
        DICTIONARY.put("аттракционы", 1);
        DICTIONARY.put("безмятежность", 1);
        DICTIONARY.put("бесплатное", 1);
        DICTIONARY.put("благодарность", 1);
        DICTIONARY.put("благородство", 1);
        DICTIONARY.put("блаженство", 1);
        DICTIONARY.put("богатство", 1);
        DICTIONARY.put("бодрость", 1);
        DICTIONARY.put("божественно", 1);
        DICTIONARY.put("бонус", 1);
        DICTIONARY.put("бриллиант", 1);
        DICTIONARY.put("везение", 1);
        DICTIONARY.put("вера", 1);
        DICTIONARY.put("верность", 1);
        DICTIONARY.put("веселье", 1);
        DICTIONARY.put("весна", 1);
        DICTIONARY.put("вкуснятина", 1);
        DICTIONARY.put("восторг", 1);
        DICTIONARY.put("восхищение", 1);
        DICTIONARY.put("выигрыш", 1);
        DICTIONARY.put("выходные", 1);
        DICTIONARY.put("гармония", 1);
        DICTIONARY.put("гонорар", 1);
        DICTIONARY.put("гордость", 1);
        DICTIONARY.put("горы", 1);
        DICTIONARY.put("гостеприимство", 1);
        DICTIONARY.put("деньги", 1);
        DICTIONARY.put("дети", 1);
        DICTIONARY.put("добро", 1);
        DICTIONARY.put("доброта", 1);
        DICTIONARY.put("достаток", 1);
        DICTIONARY.put("достижение", 1);
        DICTIONARY.put("дружба", 1);
        DICTIONARY.put("друзья", 1);
        DICTIONARY.put("женитьба", 1);
        DICTIONARY.put("забава", 1);
        DICTIONARY.put("забота", 1);
        DICTIONARY.put("заначка", 1);
        DICTIONARY.put("здоровье", 1);
        DICTIONARY.put("знакомство", 1);
        DICTIONARY.put("игра", 1);
        DICTIONARY.put("кайф", 1);
        DICTIONARY.put("каникулы", 1);
        DICTIONARY.put("карусели", 1);
        DICTIONARY.put("комедия", 1);
        DICTIONARY.put("конфеты", 1);
        DICTIONARY.put("корпоратив", 1);
        DICTIONARY.put("красота", 1);
        DICTIONARY.put("креатив", 1);
        DICTIONARY.put("ласка", 1);
        DICTIONARY.put("лёгкость", 1);
        DICTIONARY.put("лето", 1);
        DICTIONARY.put("лидер", 1);
        DICTIONARY.put("лучший", 1);
        DICTIONARY.put("любимая", 1);
        DICTIONARY.put("любимый", 1);
        DICTIONARY.put("любовь", 1);
        DICTIONARY.put("мама", 1);
        DICTIONARY.put("массаж", 1);
        DICTIONARY.put("медовый", 1);
        DICTIONARY.put("мечта", 1);
        DICTIONARY.put("мир", 1);
        DICTIONARY.put("море", 1);
        DICTIONARY.put("мороженое", 1);
        DICTIONARY.put("награда", 1);
        DICTIONARY.put("надежда", 1);
        DICTIONARY.put("надёжность", 1);
        DICTIONARY.put("наслаждение", 1);
        DICTIONARY.put("нежность", 1);
        DICTIONARY.put("новый год", 1);
        DICTIONARY.put("обновка", 1);
        DICTIONARY.put("объятия", 1);
        DICTIONARY.put("оргазм", 1);
        DICTIONARY.put("отдых", 1);
        DICTIONARY.put("отпуск", 1);
        DICTIONARY.put("победа", 1);
        DICTIONARY.put("победитель", 1);
        DICTIONARY.put("подарок", 1);
        DICTIONARY.put("позитив", 1);
        DICTIONARY.put("поощрение", 1);
        DICTIONARY.put("потеха", 1);
        DICTIONARY.put("поцелуй", 1);
        DICTIONARY.put("праздник", 1);
        DICTIONARY.put("преданность", 1);
        DICTIONARY.put("презент", 1);
        DICTIONARY.put("прекрасный", 1);
        DICTIONARY.put("прибавка", 1);
        DICTIONARY.put("приз", 1);
        DICTIONARY.put("путешествие", 1);
        DICTIONARY.put("пятница", 1);
        DICTIONARY.put("радость", 1);
        DICTIONARY.put("радуга", 1);
        DICTIONARY.put("рай", 1);
        DICTIONARY.put("распродажа", 1);
        DICTIONARY.put("ребёнок", 1);
        DICTIONARY.put("релакс", 1);
        DICTIONARY.put("рыбалка", 1);
        DICTIONARY.put("свадьба", 1);
        DICTIONARY.put("свет", 1);
        DICTIONARY.put("свидание", 1);
        DICTIONARY.put("свобода", 1);
        DICTIONARY.put("секс", 1);
        DICTIONARY.put("семья", 1);
        DICTIONARY.put("скидка", 1);
        DICTIONARY.put("сладость", 1);
        DICTIONARY.put("смех", 1);
        DICTIONARY.put("солнце", 1);
        DICTIONARY.put("спа", 1);
        DICTIONARY.put("счастье", 1);
        DICTIONARY.put("сюрприз", 1);
        DICTIONARY.put("талант", 1);
        DICTIONARY.put("творчество", 1);
        DICTIONARY.put("торжество", 1);
        DICTIONARY.put("триумф", 1);
        DICTIONARY.put("удача", 1);
        DICTIONARY.put("удовольствие", 1);
        DICTIONARY.put("украшения", 1);
        DICTIONARY.put("улыбка", 1);
        DICTIONARY.put("успех", 1);
        DICTIONARY.put("уют", 1);
        DICTIONARY.put("фарт", 1);
        DICTIONARY.put("фартит", 1);
        DICTIONARY.put("фейерверк", 1);
        DICTIONARY.put("халява", 1);
        DICTIONARY.put("хвала", 1);
        DICTIONARY.put("цветы", 1);
        DICTIONARY.put("цирк", 1);
        DICTIONARY.put("чемпион", 1);
        DICTIONARY.put("честь", 1);
        DICTIONARY.put("шоколад", 1);
        DICTIONARY.put("эйфория", 1);
        DICTIONARY.put("экстаз", 1);
        DICTIONARY.put("эскимо", 1);
        DICTIONARY.put("жить", 1);
        DICTIONARY.put("любить", 1);
        DICTIONARY.put("работать", 1);
        DICTIONARY.put("строить", 1);
        DICTIONARY.put("инвестировать", 1);
        DICTIONARY.put("рожать", 1);
        DICTIONARY.put("развивать", 1);
        DICTIONARY.put("развиваться", 1);
        DICTIONARY.put("учить", 1);
        DICTIONARY.put("учиться", 1);
        DICTIONARY.put("верить", 1);
        DICTIONARY.put("воспитывать", 1);
        DICTIONARY.put("чувствовать", 1);
        DICTIONARY.put("искать", 1);
        DICTIONARY.put("стремиться", 1);
        DICTIONARY.put("путешествовать", 1);
        DICTIONARY.put("желать", 1);
        DICTIONARY.put("понимать", 1);
        DICTIONARY.put("растить", 1);
        DICTIONARY.put("общаться", 1);
        DICTIONARY.put("удивлять", 1);
        DICTIONARY.put("восхищать", 1);
        DICTIONARY.put("убеждать", 1);
        DICTIONARY.put("беречь", 1);
        DICTIONARY.put("думать", 1);
        DICTIONARY.put("узнавать", 1);
        DICTIONARY.put("постигать", 1);
        DICTIONARY.put("дорожить", 1);
        DICTIONARY.put("обогащаться", 1);
        DICTIONARY.put("вдохновлять", 1);
        DICTIONARY.put("созидать", 1);
        DICTIONARY.put("Творить", 1);
        DICTIONARY.put("мотивировать", 1);
        DICTIONARY.put("дарить", 1);
        DICTIONARY.put("хранить", 1);
        DICTIONARY.put("сохранять", 1);
        DICTIONARY.put("помогать", 1);
        DICTIONARY.put("хвалить", 1);
        DICTIONARY.put("благодарить.", 1);
        DICTIONARY.put("радовать", 1);
        DICTIONARY.put("доверять", 1);
        DICTIONARY.put("надеяться", 1);
        DICTIONARY.put("объединяться", 1);
        DICTIONARY.put("мечтать", 1);
        DICTIONARY.put("обниматься", 1);
        DICTIONARY.put("целоваться", 1);
        DICTIONARY.put("уважать", 1);
        DICTIONARY.put("воплощать", 1);
        DICTIONARY.put("усваивать", 1);
        DICTIONARY.put("играть", 1);
        DICTIONARY.put("авария", -1);
        DICTIONARY.put("авиакатастрофа", -1);
        DICTIONARY.put("ад", -1);
        DICTIONARY.put("аннуляция", -1);
        DICTIONARY.put("аутсайдер", -1);
        DICTIONARY.put("банкротство", -1);
        DICTIONARY.put("бегство", -1);
        DICTIONARY.put("бедность", -1);
        DICTIONARY.put("бес", -1);
        DICTIONARY.put("беспокойство", -1);
        DICTIONARY.put("болезнь", -1);
        DICTIONARY.put("боль", -1);
        DICTIONARY.put("ведьма", -1);
        DICTIONARY.put("вирус", -1);
        DICTIONARY.put("вмятина", -1);
        DICTIONARY.put("война", -1);
        DICTIONARY.put("гадость", -1);
        DICTIONARY.put("гниль", -1);
        DICTIONARY.put("говно", -1);
        DICTIONARY.put("голод", -1);
        DICTIONARY.put("горе", -1);
        DICTIONARY.put("горечь", -1);
        DICTIONARY.put("грибок", -1);
        DICTIONARY.put("грязь", -1);
        DICTIONARY.put("дерьмо", -1);
        DICTIONARY.put("дефолт", -1);
        DICTIONARY.put("долг", -1);
        DICTIONARY.put("жертва", -1);
        DICTIONARY.put("зависть", -1);
        DICTIONARY.put("запрет", -1);
        DICTIONARY.put("злость", -1);
        DICTIONARY.put("измена", -1);
        DICTIONARY.put("импотенция", -1);
        DICTIONARY.put("катастрофа", -1);
        DICTIONARY.put("кораблекрушение", -1);
        DICTIONARY.put("кража", -1);
        DICTIONARY.put("крах", -1);
        DICTIONARY.put("кризис", -1);
        DICTIONARY.put("кровопролитие", -1);
        DICTIONARY.put("кровь", -1);
        DICTIONARY.put("лохатрон", -1);
        DICTIONARY.put("лузер", -1);
        DICTIONARY.put("маньяк", -1);
        DICTIONARY.put("микробы", -1);
        DICTIONARY.put("мистика", -1);
        DICTIONARY.put("могила", -1);
        DICTIONARY.put("мошшеничество", -1);
        DICTIONARY.put("му́ка", -1);
        DICTIONARY.put("мучение", -1);
        DICTIONARY.put("мышеловка", -1);
        DICTIONARY.put("наказание", -1);
        DICTIONARY.put("напряг", -1);
        DICTIONARY.put("насилие", -1);
        DICTIONARY.put("негатив", -1);
        DICTIONARY.put("негодяй", -1);
        DICTIONARY.put("недееспособность", -1);
        DICTIONARY.put("ненависть", -1);
        DICTIONARY.put("нервозность", -1);
        DICTIONARY.put("нищета", -1);
        DICTIONARY.put("обида", -1);
        DICTIONARY.put("обман", -1);
        DICTIONARY.put("обязанность", -1);
        DICTIONARY.put("обязательство", -1);
        DICTIONARY.put("ограбление", -1);
        DICTIONARY.put("одиночество", -1);
        DICTIONARY.put("отказ", -1);
        DICTIONARY.put("отставка", -1);
        DICTIONARY.put("пени", -1);
        DICTIONARY.put("подделка", -1);
        DICTIONARY.put("подстава", -1);
        DICTIONARY.put("позор", -1);
        DICTIONARY.put("полтергейст", -1);
        DICTIONARY.put("потеря", -1);
        DICTIONARY.put("предатель", -1);
        DICTIONARY.put("предательство", -1);
        DICTIONARY.put("провал", -1);
        DICTIONARY.put("проигрыш", -1);
        DICTIONARY.put("пропажа", -1);
        DICTIONARY.put("просрочка", -1);
        DICTIONARY.put("пытка", -1);
        DICTIONARY.put("развод", -1);
        DICTIONARY.put("разрушение", -1);
        DICTIONARY.put("рутина", -1);
        DICTIONARY.put("самоубийство", -1);
        DICTIONARY.put("санкция", -1);
        DICTIONARY.put("скука", -1);
        DICTIONARY.put("слабость", -1);
        DICTIONARY.put("слеза", -1);
        DICTIONARY.put("смерть", -1);
        DICTIONARY.put("смрад", -1);
        DICTIONARY.put("ссора", -1);
        DICTIONARY.put("страдание", -1);
        DICTIONARY.put("страх", -1);
        DICTIONARY.put("стужа", -1);
        DICTIONARY.put("суд", -1);
        DICTIONARY.put("суета", -1);
        DICTIONARY.put("суицид", -1);
        DICTIONARY.put("темнота", -1);
        DICTIONARY.put("теракт", -1);
        DICTIONARY.put("тоска", -1);
        DICTIONARY.put("тревога", -1);
        DICTIONARY.put("трусость", -1);
        DICTIONARY.put("труха", -1);
        DICTIONARY.put("тухлятина", -1);
        DICTIONARY.put("тюрьма", -1);
        DICTIONARY.put("тяжесть", -1);
        DICTIONARY.put("убийство", -1);
        DICTIONARY.put("урод", -1);
        DICTIONARY.put("ущерб", -1);
        DICTIONARY.put("холод", -1);
        DICTIONARY.put("штраф", -1);
        DICTIONARY.put("яд", -1);
        DICTIONARY.put("язва", -1);
        DICTIONARY.put("null", -1);
    }

}
