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
 * Converts between alpha-3 country codes and country names.
 * Reads a tab-separated file with a header (e.g., "name\talpha-2\talpha-3").
 */
public class CountryCodeConverter {

    private final Map<String, String> countryCodeToCountry = new HashMap<>(); // alpha3(lower) -> name
    private final Map<String, String> countryToCountryCode = new HashMap<>(); // name -> alpha3(lower)

    public CountryCodeConverter() {
        this("country-codes.txt");
    }

    public CountryCodeConverter(String filename) {
        try {
            List<String> lines = Files.readAllLines(
                    Paths.get(getClass().getClassLoader().getResource(filename).toURI())
            );

            Iterator<String> it = lines.iterator();
            if (it.hasNext()) it.next(); // skip header

            while (it.hasNext()) {
                String line = it.next().trim();
                if (line.isEmpty()) continue;

                // Expect something like: name<TAB>alpha-2<TAB>alpha-3  (but be resilient)
                String[] parts = line.split("\t");
                if (parts.length == 0) continue;

                String name = parts[0].trim();
                if (name.isEmpty()) continue;

                // Find a 3-letter alpha-3 code in the remaining columns
                String alpha3 = null;
                for (int i = 1; i < parts.length; i++) {
                    String p = parts[i].trim();
                    if (p.length() == 3 && p.chars().allMatch(Character::isLetter)) {
                        alpha3 = p.toLowerCase();
                        break;
                    }
                }

                if (alpha3 == null) continue; // no alpha-3 found, skip line

                countryCodeToCountry.put(alpha3, name);
                countryToCountryCode.put(name, alpha3);
            }
        } catch (IOException | URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    /** name from alpha-3 code (case-insensitive) */
    public String fromCountryCode(String code) {
        if (code == null) return null;
        return countryCodeToCountry.get(code.toLowerCase());
    }

    /** alpha-3 code (lowercase) from name */
    public String fromCountry(String country) {
        if (country == null) return null;
        return countryToCountryCode.get(country);
    }

    public int getNumCountries() {
        return countryCodeToCountry.size();
    }
}
