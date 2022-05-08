package main.tools;


import company.evo.jmorphy2.MorphAnalyzer;
import company.evo.jmorphy2.ParsedWord;
import company.evo.jmorphy2.ResourceFileLoader;
import company.evo.jmorphy2.Tag;
import lombok.extern.log4j.Log4j;
import main.entities.NewsSiteData;
import org.apache.lucene.analysis.ru.RussianAnalyzer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Log4j
public class TextTools {
    private static MorphAnalyzer morph = null;
    private static RussianAnalyzer ra = new RussianAnalyzer();

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
}
