package translation;

import javax.swing.*;
import java.awt.event.*;
import java.awt.Dimension;


// TODO Task D: Update the GUI for the program to align with UI shown in the README example.
//            Currently, the program only uses the CanadaTranslator and the user has
//            to manually enter the language code they want to use for the translation.
//            See the examples package for some code snippets that may be useful when updating
//            the GUI.
public class GUI {

    public static void main(String[] args) {
        LanguageCodeConverter codeConverter = new LanguageCodeConverter();
        String[] languageCodes = {"de", "en", "zh", "es", "fr"};
        String[] languageNames = new String[languageCodes.length];

        for (int i = 0; i < languageCodes.length; i++) {
            String name = codeConverter.fromLanguageCode(languageCodes[i]);
            languageNames[i] = (name != null) ? name : languageCodes[i];
        }

        SwingUtilities.invokeLater(() -> {
            JPanel countryPanel = new JPanel();
            JTextField countryField = new JTextField(10);
            countryField.setText("can");
            countryField.setEditable(false); // we only support the "can" country code for now
            countryPanel.add(new JLabel("Country:"));
            countryPanel.add(countryField);


            JPanel languagePanel = new JPanel();
            JList<String> languageList = new JList<>(languageNames);
            languageList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            languageList.setSelectedIndex(0);
            JScrollPane languageScrollPane = new JScrollPane(languageList);

            languageScrollPane.setPreferredSize(new Dimension(80, 60));
            languagePanel.setLayout(new BoxLayout(languagePanel, BoxLayout.Y_AXIS));
            languagePanel.add(new JLabel("Select Language:"));
            languagePanel.add(languageScrollPane);

            JPanel buttonPanel = new JPanel();
            JButton submit = new JButton("Submit");
            buttonPanel.add(submit);

            JLabel resultLabelText = new JLabel("Translation:");
            buttonPanel.add(resultLabelText);
            JLabel resultLabel = new JLabel("\t\t\t\t\t\t\t");
            buttonPanel.add(resultLabel);

            submit.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    // Get the selected index from the list
                    int selectedIndex = languageList.getSelectedIndex();

                    if (selectedIndex == -1) {
                        resultLabel.setText("Please select a language!");
                        return;
                    }

                    // Use the index to look up the correct language code
                    String language = languageCodes[selectedIndex];
                    String country = countryField.getText();

                    // for now, just using our simple translator, but
                    // we'll need to use the real JSON version later.
                    // NOTE: Assumes Translator and CanadaTranslator exist
                    Translator translator = new CanadaTranslator();

                    String result = translator.translate(country, language);
                    if (result == null) {
                        result = "no translation found for code: " + language;
                    }
                    resultLabel.setText(result);

                }

            });

            JPanel mainPanel = new JPanel();
            mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
            mainPanel.add(countryPanel);
            mainPanel.add(languagePanel);
            mainPanel.add(buttonPanel);

            JFrame frame = new JFrame("Country Name Translator");
            frame.setContentPane(mainPanel);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.pack();
            frame.setVisible(true);


        });
    }
}