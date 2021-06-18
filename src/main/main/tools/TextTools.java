package main.tools;

import main.entities.AutoSiteData;
import org.apache.commons.lang.StringUtils;
import org.apache.lucene.analysis.ru.RussianAnalyzer;
import org.apache.lucene.morphology.russian.RussianMorphology;

import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

public class TextTools {
    RussianMorphology rm = new RussianMorphology();
    RussianAnalyzer ra = new RussianAnalyzer();

    public TextTools() throws IOException {
    }

    public List<String> textToNormalForm(AutoSiteData data) {
        String bufText = data.getText();
        String[] wordsList = bufText.toLowerCase().replaceAll("[^а-яёa-z ]", "").split(" ");
        List<String> normalWordsList = new LinkedList<>();
        for (String word : wordsList) {
            if (StringUtils.isNotBlank(word)) {
                if (!Pattern.matches("[а-яё ]+", word))
                    normalWordsList.add(word);
                else {
                    if (ra.getStopwordSet().contains(word))
                        normalWordsList.add(rm.getNormalForms(word).get(0));
                }
            }
        }
        return normalWordsList;
    }

    public List<List<String>> getShingles(List<String> wordsList, int shingleSize) {
        List<List<String>> shingles = new LinkedList<>();
        List<String> shingleBuf = new LinkedList<>();
        int j = 0;
        for (int i = 0; i < wordsList.size(); i++) {
            if (j == shingleSize) {
                shingles.add(shingleBuf);
                shingleBuf = new LinkedList<>();
                j = 0;
                i -= (shingleSize - 1);
            }
            shingleBuf.add(wordsList.get(i));
            j++;
        }
        return shingles;
    }

    public Integer getMinHash(List<List<String>> shingles) {
        List<Integer> hashes = new LinkedList<>();
        for (int i = 32; i < 132; i++) {
            for (List<String> shingle : shingles) {
                StringBuilder sb = new StringBuilder();
                for (String word : shingle) {
                    sb.append(word);
                }
                hashes.add(strHash(sb.toString(),32));
            }
        }
        return Collections.min(hashes);
    }

    public double jacarta(Integer hash1, Integer hash2) {
        double jacart;
        String strHash1 = String.valueOf(hash1);
        String strHash2 = String.valueOf(hash2);
        int hashSize = strHash1.length();
        int similarCount = 0;
        for (int i = 0; i < hashSize; i++) {
            if (strHash1.charAt(i) == strHash2.charAt(i))
                similarCount++;
        }
        jacart = (double) (Math.round(((double) (similarCount) / (hashSize * 2 - similarCount)) * 100.0)) / 100;
        return jacart;
    }

    private int strHash(String str, int k) {
        int hash = 7;
        for (int i = 0; i < str.length(); i++) {
            hash = ((hash * k + (str.charAt(i))) ^ 15) % 1000000;
        }
        while (String.valueOf(hash).length() < 7)
            hash *= 10;
        return hash;
    }
}
