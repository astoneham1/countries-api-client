package uk.co.alexstoneham;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.FontUIResource;
import java.awt.*;
import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.util.Enumeration;
import java.net.URI;
import java.net.http.HttpResponse;

public class Main {
    private static final HttpClient client = HttpClient.newHttpClient();
    private static final String BASE_API_LINK = "https://restcountries.com/v3.1/name/";

    public static void main(String[] args) {
        setUIFont(new FontUIResource(new Font("Tahoma", Font.PLAIN, 14)));

        JFrame frame = new JFrame("Country Statistics");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        frame.setResizable(false);

        JLabel countryName = new JLabel("Country Name");
        countryName.setFont(new Font("Tahoma", Font.BOLD, 20));

        JPanel topPanel = new JPanel();
        topPanel.add(countryName);

        JLabel fullNameLabel = new JLabel("Official Name");
        setBoldFont(fullNameLabel);
        JLabel fullNameValue = new JLabel("The United Kingdom of Great Britain and Northern Ireland");
        addBottomGap(fullNameValue);

        JLabel regionLabel = new JLabel("Region");
        setBoldFont(regionLabel);
        JLabel regionValue = new JLabel("NSEW");
        addBottomGap(regionValue);

        JLabel populationLabel = new JLabel("Population");
        setBoldFont(populationLabel);
        JLabel populationValue = new JLabel("999,999");
        addBottomGap(populationValue);

        JLabel capitalLabel = new JLabel("Capital");
        setBoldFont(capitalLabel);
        JLabel capitalValue = new JLabel("XXXXYYYY");
        addBottomGap(capitalValue);

        JLabel languagesLabel = new JLabel("Languages");
        setBoldFont(languagesLabel);
        JLabel languagesValue = new JLabel("ish,man,an,ese");
        addBottomGap(languagesValue);

        JPanel centerPanel = new JPanel(new GridLayout(20, 1));
        centerPanel.setBorder(new EmptyBorder(10, 30, 10, 10));
        centerPanel.add(fullNameLabel);
        centerPanel.add(fullNameValue);
        centerPanel.add(regionLabel);
        centerPanel.add(regionValue);
        centerPanel.add(populationLabel);
        centerPanel.add(populationValue);
        centerPanel.add(capitalLabel);
        centerPanel.add(capitalValue);
        centerPanel.add(languagesLabel);
        centerPanel.add(languagesValue);

        JButton newCountryButton = new JButton("Search");

        JPanel bottomPanel = new JPanel();
        bottomPanel.add(newCountryButton);

        frame.setLayout(new BorderLayout());
        frame.add(topPanel, BorderLayout.NORTH);
        frame.add(centerPanel, BorderLayout.CENTER);
        frame.add(bottomPanel, BorderLayout.SOUTH);
        frame.setVisible(true);

        newCountryButton.addActionListener(e -> {
            String result = (String)JOptionPane.showInputDialog(
                    frame,
                    "Enter a country name",
                    "Search",
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    null,
                    null
            );

            if (result != null) {
                try {
                    HttpRequest request = HttpRequest.newBuilder()
                            .uri(URI.create(BASE_API_LINK + result))
                            .build();

                    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                    String flagUrl;

                    // overall array
                    JSONArray array = new JSONArray(response.body());
                    JSONObject countryDetails = array.getJSONObject(0);

                    // country name
                    JSONObject countryNameDetails = countryDetails.getJSONObject("name");
                    countryName.setText(countryNameDetails.getString("common"));
                    fullNameValue.setText(countryNameDetails.getString("official"));

                    // region
                    String region = countryDetails.getString("region");
                    String subRegion = countryDetails.getString("subregion");
                    regionValue.setText(region + " -> " + subRegion);

                    // population
                    int population = countryDetails.getInt("population");
                    String formattedPopulation = String.format("%,d", population);
                    populationValue.setText(formattedPopulation);

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

                    System.out.println(countryDetails);

                } catch (IOException | InterruptedException | JSONException ex ) {
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
