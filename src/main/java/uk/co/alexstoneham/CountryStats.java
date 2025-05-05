package uk.co.alexstoneham;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.FontUIResource;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Enumeration;

public class CountryStats {
    private static final HttpClient client = HttpClient.newHttpClient();

    public static void main(String[] args) {
        setUIFont(new FontUIResource(new Font("Tahoma", Font.PLAIN, 14)));

        // main frame
        JFrame frame = new JFrame("Country Statistics");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        frame.setResizable(false);

        // header
        JLabel countryName = new JLabel("Click 'Search' to begin");
        countryName.setFont(new Font("Tahoma", Font.BOLD, 20));

        JPanel topPanel = new JPanel();
        topPanel.setBorder(new EmptyBorder(15, 0, 0, 0));
        topPanel.add(countryName);

        // country details
        JLabel fullNameLabel = new JLabel("Official Name");
        setBoldFont(fullNameLabel);
        JLabel fullNameValue = new JLabel();
        addBottomGap(fullNameValue);

        JLabel countryCodeLabel = new JLabel("Country Code");
        setBoldFont(countryCodeLabel);
        JLabel countryCodeValue = new JLabel();
        addBottomGap(countryCodeValue);

        JLabel regionLabel = new JLabel("Region");
        setBoldFont(regionLabel);
        JLabel regionValue = new JLabel();
        addBottomGap(regionValue);

        JLabel populationLabel = new JLabel("Population");
        setBoldFont(populationLabel);
        JLabel populationValue = new JLabel();
        addBottomGap(populationValue);

        JLabel landAreaLabel = new JLabel("Land Area");
        setBoldFont(landAreaLabel);
        JLabel landAreaValue = new JLabel();
        addBottomGap(landAreaValue);

        JLabel capitalLabel = new JLabel("Capital");
        setBoldFont(capitalLabel);
        JLabel capitalValue = new JLabel();
        addBottomGap(capitalValue);

        JLabel languagesLabel = new JLabel("Languages");
        setBoldFont(languagesLabel);
        JLabel languagesValue = new JLabel();
        addBottomGap(languagesValue);

        JLabel currenciesLabel = new JLabel("Currencies");
        setBoldFont(currenciesLabel);
        JLabel currenciesValue = new JLabel();
        addBottomGap(currenciesValue);

        JLabel landlockedStatus = new JLabel("Landlocked?");
        setBoldFont(landlockedStatus);
        addBottomGap(landlockedStatus);

        JLabel independentStatus = new JLabel("Independent?");
        setBoldFont(independentStatus);
        addBottomGap(independentStatus);

        JLabel unStatus = new JLabel("Member of United Nations?");
        setBoldFont(unStatus);

        JLabel flagLabel = new JLabel();
        JPanel imagePanel = new JPanel(new BorderLayout());
        imagePanel.setPreferredSize(new Dimension(250, 250));
        imagePanel.add(flagLabel, BorderLayout.CENTER);

        // Country details container
        JPanel centerPanel = new JPanel(new GridLayout(20, 1));
        centerPanel.setBorder(new EmptyBorder(10, 30, 10, 10));
        centerPanel.add(fullNameLabel);
        centerPanel.add(fullNameValue);
        centerPanel.add(countryCodeLabel);
        centerPanel.add(countryCodeValue);
        centerPanel.add(regionLabel);
        centerPanel.add(regionValue);
        centerPanel.add(populationLabel);
        centerPanel.add(populationValue);
        centerPanel.add(landAreaLabel);
        centerPanel.add(landAreaValue);
        centerPanel.add(capitalLabel);
        centerPanel.add(capitalValue);
        centerPanel.add(languagesLabel);
        centerPanel.add(languagesValue);
        centerPanel.add(currenciesLabel);
        centerPanel.add(currenciesValue);
        centerPanel.add(landlockedStatus);
        centerPanel.add(independentStatus);
        centerPanel.add(unStatus);
        centerPanel.setVisible(false);

        JLabel instructions = new JLabel("Enter a country:");

        // Create the text field for input
        JTextField countryInputField = new JTextField(15); // Width of 15 columns
        countryInputField.setFont(new Font("Tahoma", Font.PLAIN, 14));

        // Create the search button
        JPanel bottomPanel = new JPanel();
        JButton newCountryButton = new JButton("Search");

        // Add the text field and button to the bottom panel
        bottomPanel.add(instructions);
        bottomPanel.add(countryInputField);
        bottomPanel.add(newCountryButton);

        // Country info layout
        frame.setLayout(new BorderLayout());
        frame.add(topPanel, BorderLayout.NORTH);
        frame.add(centerPanel, BorderLayout.CENTER);
        frame.add(imagePanel, BorderLayout.EAST);
        frame.add(bottomPanel, BorderLayout.SOUTH);
        frame.setVisible(true);

        // Add a key listener to simulate button click when Enter is pressed
        countryInputField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
                    newCountryButton.doClick(); // Simulates a button click
                }
            }
        });

        // Action listener for the Search button
        newCountryButton.addActionListener(e -> {
            String result = countryInputField.getText().trim();  // Get the text from the input field

            if (!result.isEmpty()) {
                try {
                    String encodedName = result.replace(" ", "%20");
                    HttpRequest request = HttpRequest.newBuilder()
                            .uri(URI.create("https://restcountries.com/v3.1/name/" + encodedName + "?fullText=true"))
                            .build();

                    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                    if (response.statusCode() == 404) {
                        JOptionPane.showMessageDialog(
                                frame,
                                "No country found with the name: " + result,
                                "Country Not Found",
                                JOptionPane.ERROR_MESSAGE
                        );
                        return;
                    }

                    // overall array
                    JSONArray array = new JSONArray(response.body());
                    JSONObject countryDetails = array.getJSONObject(0);

                    // Update the country details in the UI
                    // Country name
                    JSONObject countryNameDetails = countryDetails.getJSONObject("name");
                    String cca3 = countryDetails.getString("cca3");
                    String commonName = countryNameDetails.getString("common");
                    String fullName = countryNameDetails.getString("official");
                    countryCodeValue.setText(cca3);
                    countryName.setText(commonName);
                    fullNameValue.setText(fullName);

                    // region
                    String region = countryDetails.getString("region");
                    String subRegion = countryDetails.getString("subregion");
                    regionValue.setText(region + " -> " + subRegion);

                    // population
                    int population = countryDetails.getInt("population");
                    String formattedPopulation = String.format("%,d", population);
                    populationValue.setText(formattedPopulation);

                    // land area
                    int landArea = countryDetails.getInt("area");
                    String formattedLandArea = String.format("%,d", landArea) + " km²";
                    landAreaValue.setText(formattedLandArea);

                    // capital
                    JSONArray capitalCity = countryDetails.getJSONArray("capital");
                    capitalValue.setText(capitalCity.getString(0));

                    // languages
                    StringBuilder languagesList = new StringBuilder();
                    JSONObject languages = countryDetails.getJSONObject("languages");

                    for (String key : languages.keySet()) {
                        String languageName = languages.getString(key);
                        languagesList.append(languageName).append(", ");
                    }

                    if (!languagesList.isEmpty()) {
                        languagesList.setLength(languagesList.length() - 2);
                    }

                    languagesValue.setText(languagesList.toString());

                    // currencies
                    StringBuilder currenciesList = new StringBuilder();
                    JSONObject currencies = countryDetails.getJSONObject("currencies");

                    for (String code : currencies.keySet()) {
                        JSONObject currencyInfo = currencies.getJSONObject(code);
                        String currencySymbol = currencyInfo.getString("symbol");
                        String currencyName = currencyInfo.getString("name");
                        currenciesList.append("(").append(currencySymbol).append(") ").append(currencyName).append(", ");
                    }

                    if (!currenciesList.isEmpty()) {
                        currenciesList.setLength(currenciesList.length() - 2);
                    }

                    currenciesValue.setText(currenciesList.toString());

                    // landlocked
                    boolean isLandlocked = countryDetails.getBoolean("landlocked");
                    if (isLandlocked) {
                        landlockedStatus.setText("Landlocked? ✅");
                    } else {
                        landlockedStatus.setText("Landlocked? ❌");
                    }

                    // independent
                    boolean isIndependent = countryDetails.getBoolean("independent");
                    if (isIndependent) {
                        independentStatus.setText("Independent? ✅");
                    } else {
                        independentStatus.setText("Independent? ❌");
                    }

                    // UN member
                    boolean isUNMember = countryDetails.getBoolean("unMember");
                    if (isUNMember) {
                        unStatus.setText("Member of United Nations? ✅");
                    } else {
                        unStatus.setText("Member of United Nations? ❌");
                    }

                    // flag
                    JSONObject flags = countryDetails.getJSONObject("flags");
                    String flagUrl = flags.getString("png");

                    try {
                        HttpRequest flagRequest = HttpRequest.newBuilder()
                                .uri(URI.create(flagUrl))
                                .build();
                        HttpResponse<byte[]> flagResponse = client.send(flagRequest, HttpResponse.BodyHandlers.ofByteArray());

                        if (flagResponse.statusCode() == 200) {
                            ImageIcon icon = new ImageIcon(flagResponse.body());
                            Image img = icon.getImage();
                            Image scaledImg = img.getScaledInstance(200, 120, Image.SCALE_SMOOTH);
                            flagLabel.setIcon(new ImageIcon(scaledImg));
                            flagLabel.setText("");
                        } else {
                            flagLabel.setText("Flag unavailable");
                            flagLabel.setIcon(null);
                        }
                    } catch (Exception ex) {
                        flagLabel.setText("Flag unavailable");
                        flagLabel.setIcon(null);
                    }

                    if (!centerPanel.isVisible()) {
                        centerPanel.setVisible(true);
                        frame.revalidate();
                        frame.repaint();
                    }

                    countryInputField.setText("");
                } catch (IOException | InterruptedException | JSONException ex) {
                    ex.printStackTrace();
                }
            }
        });
    }

    public static void setUIFont(FontUIResource f) {
        Enumeration<Object> keys = UIManager.getDefaults().keys();
        while (keys.hasMoreElements()) {
            Object key = keys.nextElement();
            Object value = UIManager.get(key);
            if (value instanceof FontUIResource orig) {
                Font font = new Font(f.getFontName(), orig.getStyle(), f.getSize());
                UIManager.put(key, new FontUIResource(font));
            }
        }
    }

    public static void setBoldFont(Component c) {
        c.setFont(new Font("Tahoma", Font.BOLD, 14));
    }

    public static void addBottomGap(JLabel c) {
        c.setBorder(new EmptyBorder(0, 0, 5, 0));
    }
}