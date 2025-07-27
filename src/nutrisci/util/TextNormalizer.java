package nutrisci.util;

import java.text.Normalizer;

public class TextNormalizer {
    public static String normalize(String input) {
        // Remove accents/diacritics
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD)
            .replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
        // Convert to lowercase
        return normalized.toLowerCase();
    }
}