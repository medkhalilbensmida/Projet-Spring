package tn.fst.spring.projet_spring.services.utils;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class BadWordFilter {
    private List<String> badWords;
    public BadWordFilter() {
        try {
            Path path = new ClassPathResource("badwords.txt").getFile().toPath();
            System.out.print(path);
            badWords = Files.lines(path)
                    .map(String::toLowerCase)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors du chargement des mots interdits", e);
        }
    }
    public boolean containsBadWords(String content) {
        String normalized = content.toLowerCase();
        return badWords.stream().anyMatch(normalized::contains);
    }
}
