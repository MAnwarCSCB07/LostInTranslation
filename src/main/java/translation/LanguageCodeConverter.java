package translation;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * This class provides the services of: <br/>
 * - converting language codes to their names <br/>
 * - converting language names to their codes
 */
public class LanguageCodeConverter {

    private final Map<String, String> languageCodeToLanguage = new HashMap<>();
    private final Map<String, String> languageToLanguageCode = new HashMap<>();

    /**
     * Default constructor that loads the language codes from "language-codes.txt"
     * in the resources folder.
     */
    public LanguageCodeConverter() {
        this("language-codes.txt");
    }

    /**
     * Overloaded constructor that allows us to specify the filename to load the language code data from.
     * @param filename the name of the file in the resources folder to load the data from
     * @throws RuntimeException if the resources file can't be loaded properly
     */
    public LanguageCodeConverter(String filename) {
        try {
            List<String> lines = Files.readAllLines(
                    Paths.get(getClass().getClassLoader().getResource(filename).toURI())
            );

            Iterator<String> iterator = lines.iterator();
            if (iterator.hasNext()) {
                iterator.next(); // skip header line
            }

            while (iterator.hasNext()) {
                String line = iterator.next().trim();
                if (line.isEmpty()) continue;

                // Expect tab-delimited: <languageName>\t<code>
                String[] parts = line.split("\\t");
                if (parts.length < 2) continue; // skip malformed rows

                String language = parts[0].trim();
                String code = parts[1].trim().toLowerCase();

                if (!language.isEmpty() && !code.isEmpty()) {
                    // store both directions; normalize language key to lowercase for lookup
                    languageCodeToLanguage.put(code, language);
                    languageToLanguageCode.put(language.toLowerCase(), code);
                }
            }
        } catch (IOException | URISyntaxException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * Return the name of the language for the given language code.
     * @param code the 2-letter language code
     * @return the name of the language corresponding to the code, or null if unknown
     */
    public String fromLanguageCode(String code) {
        if (code == null) return null;
        return languageCodeToLanguage.get(code.trim().toLowerCase());
    }

    /**
     * Return the code of the language for the given language name.
     * @param language the name of the language
     * @return the 2-letter code of the language, or null if unknown
     */
    public String fromLanguage(String language) {
        if (language == null) return null;
        return languageToLanguageCode.get(language.trim().toLowerCase());
    }

    /**
     * Return how many languages are included in this language code converter.
     * @return how many languages are included in this language code converter.
     */
    public int getNumLanguages() {
        return languageCodeToLanguage.size();
    }
}
