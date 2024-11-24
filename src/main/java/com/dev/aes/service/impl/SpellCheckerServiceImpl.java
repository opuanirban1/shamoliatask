package com.dev.aes.service.impl;

import com.dev.aes.payloads.response.DictionaryCheckerResponse;
import com.dev.aes.payloads.response.IncorrectWord;
import com.dev.aes.payloads.response.SpellCheckResponse;
import com.dev.aes.service.DictionaryService;
import com.dev.aes.service.SpellCheckerService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.language.LanguageIdentifier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import java.io.*;
import java.util.*;
import java.util.regex.Pattern;

@Service
@Slf4j
public class SpellCheckerServiceImpl implements SpellCheckerService {
    private final DictionaryService dictionaryService;

    private static Set<String> bengaliWords;
    @Value("${node.spellcheck.dictionary.command}")
    private String nodeCommand;

    @Autowired
    public SpellCheckerServiceImpl(DictionaryService dictionaryService) {
        this.dictionaryService = dictionaryService;
    }

    @Override
    public SpellCheckResponse checkSpell(String content) {
        if (Objects.isNull(content) || content.isBlank()){
            return null;
        }
        List<IncorrectWord> incorrectWords = new ArrayList<>();
        List<String> response = List.of();
        if (languageDetection(content).equals("en")){
            try{
                response = englishSpellCheck(content);
            }catch (Exception e){
                return  SpellCheckResponse.builder().incorrectWords(incorrectWords).build();
            }

        }else {
            response = bengaliSpellCheck(content);
        }
        if (Objects.nonNull(response) && !response.isEmpty()) {
            Set<String> words = dictionaryService.findUserCorrectionWords();
            for (String word : response) {

                if (!words.contains(word) && !word.isBlank()) {
                    incorrectWords.add(new IncorrectWord(word, content.indexOf(word)));
                }
            }
        }
        return SpellCheckResponse.builder().incorrectWords(incorrectWords).build();
    }

    private String languageDetection(String content){
        LanguageIdentifier identifier = new LanguageIdentifier(content);
        return identifier.getLanguage();
    }

    private Set<String> constructBengaliWords(String dictionaryPath) {
        try {
            Set<String> words = new HashSet<>();
            File file = ResourceUtils.getFile("classpath:" + dictionaryPath);
            BufferedReader mainBR = new BufferedReader(new InputStreamReader(
                    new FileInputStream(file), "UTF-8"));
            int i = 0;
            String string = mainBR.readLine();
            while (string != null) {
                words.add(string);
                i++;
                string = mainBR.readLine();
            }
            mainBR.close();
            return words;

        } catch (IOException e) {
            System.out.println("An error occurred while reading to the file: " + e.getMessage());
        }
        return Set.of();
    }

    private List<String> bengaliSpellCheck(String word) {
        if (Objects.isNull(bengaliWords) || bengaliWords.isEmpty()) {
            bengaliWords = constructBengaliWords("bn-words.txt");
        }
        String[] words = word.split(" ");
        List<String> inCorrectWords = new ArrayList<>();
        for (String str : words) {
            String replaceWord = str.replaceAll("['-?:।;(),<>!{}\\[\\]৷\"]", "").trim();
            if (isNumeric(replaceWord)) continue;
            if (!replaceWord.isBlank() && !bengaliWords.contains(replaceWord)) {
                inCorrectWords.add(str);
            }
        }
        return inCorrectWords;
    }

    private List<String> englishSpellCheck(String word) {
        log.info("API path {} and input {}", "/getSpellCheckEnglish", word);

        ProcessBuilder builder = new ProcessBuilder(
                "/bin/bash", "-c", nodeCommand + " " + word.replace("-\n","")
                .replace("\n","").replaceAll("['-?:;(),_<>!{}\\[\\]৷\"]", " ").trim());

        builder.redirectErrorStream(true);
        Process p = null;
        try {
            p = builder.start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()));
        String line = "",res="";

        while (true) {
            try {
                if (!((line = r.readLine()) != null)) break;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            line = line.replace("'", "\\\\\"");
            res += line;
        }

        log.info("Response {}", res.replaceAll("\\\\", ""));
        ObjectMapper objectMapper = new ObjectMapper();
        DictionaryCheckerResponse data = null;
        try {
            data = objectMapper.readValue(res.replaceAll("\\\\", ""), DictionaryCheckerResponse.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return data.getMismatch();
    }



    private boolean isNumeric(String str) {
        if (str == null || str.isEmpty()) {
            return false;
        }
        Pattern pattern = Pattern.compile("[0-9০১২৩৪৫৬৭৮৯]+");
        return pattern.matcher(str).matches();
    }
}
