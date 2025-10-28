package translation;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;     // java.util.List (avoid java.awt.List)
import java.util.Map;

public class GUI {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // ---------- Data sources ----------
            final Translator translator = new JSONTranslator();
            final CountryCodeConverter countryConv = new CountryCodeConverter();
            final LanguageCodeConverter languageConv = new LanguageCodeConverter();

            // ---------- Build languages (names) + map name->code ----------
            List<String> langCodes = new ArrayList<>(translator.getLanguageCodes()); // e.g., "en","fr",...
            Collections.sort(langCodes);

            final Map<String, String> languageNameToCode = new HashMap<>();
            List<String> languageNames = new ArrayList<>();
            for (String code : langCodes) {
                String lname = languageConv.fromLanguageCode(code.toLowerCase());
                if (lname == null || lname.isBlank()) lname = code.toLowerCase();
                languageNames.add(lname);
                languageNameToCode.put(lname, code.toLowerCase());
            }
            languageNames.sort(String.CASE_INSENSITIVE_ORDER);

            // ---------- Build countries (names) + map name->alpha3 ----------
            List<String> countryCodes = new ArrayList<>(translator.getCountryCodes()); // alpha-3 codes
            Collections.sort(countryCodes);

            final Map<String, String> countryNameToCode = new HashMap<>();
            List<String> countryNames = new ArrayList<>();
            for (String alpha3 : countryCodes) {
                String cname = countryConv.fromCountryCode(alpha3.toLowerCase()); // -> country name
                if (cname == null || cname.isBlank()) cname = alpha3.toUpperCase(); // fallback
                countryNames.add(cname);
                countryNameToCode.put(cname, alpha3.toUpperCase());
            }
            countryNames.sort(String.CASE_INSENSITIVE_ORDER);

            // ---------- UI: top language row ----------
            JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
            top.add(new JLabel("Language:"));
            JComboBox<String> languageCombo = new JComboBox<>(languageNames.toArray(new String[0]));
            // default to English if present
            for (String ln : languageNames) {
                if ("English".equalsIgnoreCase(ln)) { languageCombo.setSelectedItem(ln); break; }
            }
            top.add(languageCombo);

            // ---------- UI: translation line ----------
            JPanel transRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
            JLabel transTitle = new JLabel("Translation:");
            JLabel transValue = new JLabel("—");
            transValue.setFont(transValue.getFont().deriveFont(Font.BOLD, 14f));
            transRow.add(transTitle);
            transRow.add(transValue);

            // ---------- UI: scrollable country list ----------
            JList<String> countryList = new JList<>(countryNames.toArray(new String[0]));
            countryList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            countryList.setVisibleRowCount(14);
            JScrollPane countryScroll = new JScrollPane(countryList);
            countryScroll.setPreferredSize(new Dimension(300, 260));

            // default select Canada (or first)
            int idx = countryNames.indexOf("Canada");
            if (idx < 0 && !countryNames.isEmpty()) idx = 0;
            if (idx >= 0) {
                countryList.setSelectedIndex(idx);
                countryList.ensureIndexIsVisible(idx);
            }

            // ---------- Behavior: translate on any selection change ----------
            Runnable update = () -> {
                String countryName = countryList.getSelectedValue();
                String languageName = (String) languageCombo.getSelectedItem();
                if (countryName == null || languageName == null) {
                    transValue.setText("—");
                    return;
                }
                String alpha3 = countryNameToCode.getOrDefault(countryName, countryName).toUpperCase();
                String langCode = languageNameToCode.getOrDefault(languageName, languageName).toLowerCase();

                String translated = translator.translate(alpha3, langCode);
                transValue.setText((translated == null || translated.isBlank()) ? "no translation found!" : translated);
            };

            countryList.addListSelectionListener((ListSelectionEvent e) -> {
                if (!e.getValueIsAdjusting()) update.run();
            });
            languageCombo.addActionListener(e -> update.run());

            // ---------- Main layout ----------
            JPanel main = new JPanel();
            main.setLayout(new BoxLayout(main, BoxLayout.Y_AXIS));
            main.add(top);
            main.add(transRow);
            main.add(countryScroll);

            JFrame frame = new JFrame("Country Name Translator");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setContentPane(main);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);

            // initial compute
            update.run();
        });
    }
}
