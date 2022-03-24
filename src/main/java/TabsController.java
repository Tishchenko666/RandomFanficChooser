import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;

import java.io.*;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class TabsController implements Initializable {

    public CheckBox jenCheckBox;
    public CheckBox getCheckBox;
    public CheckBox slashCheckBox;
    public CheckBox otherCheckBox;
    public TextArea originalHistoryTextArea;
    public ComboBox originalHistoryComboBox = new ComboBox();
    public Button getFanficButton;
    public Label fanficLabel;

    public Tab chooseFanficTab;
    public Tab addFanficTab;
    public TabPane fanficsTabPane;
    public RadioButton fromFileRadioButton;
    public RadioButton byHandsRadioButton;
    public HBox forFromFileHBox;
    public TextField fromFileTextField;
    public Button forFromFileButton;
    public Button getFileButton;

    DBConnector dbc;
    ObservableList<String> historyComboList = FXCollections.observableArrayList();
    StringBuilder chosenHistory = new StringBuilder("");
    ArrayList<String> chosenGenreList = new ArrayList<>();
    ArrayList<String> chosenHistoryList = new ArrayList<>();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ResultSet historyResultSet;
        try {
            dbc = new DBConnector();
            historyResultSet = dbc.getOriginalHistoryList();
            while (historyResultSet.next()) {
                historyComboList.add(historyResultSet.getString(1));
            }
            dbc.closeStatement();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        originalHistoryComboBox.setItems(historyComboList);
    }

    @FXML
    private void fromFileButtonClicked() {
        String fansFilePath = fromFileTextField.getText();
        ArrayList<StringBuilder> insertQueryParts = new ArrayList<>();
        File fansFile = new File(fansFilePath);
        if (fansFile.exists()) {
            try (FileReader fansFileReader = new FileReader(fansFile)) {
                BufferedReader bufferedReader = new BufferedReader(fansFileReader);
                String fanLine;
                StringBuilder insertFansBuilder;
                while ((fanLine = bufferedReader.readLine()) != null) {
                    insertFansBuilder = new StringBuilder();
                    String[] splitedLine = fanLine.split(";");
                    insertFansBuilder
                            .append("('")
                            .append(splitedLine[0])
                            .append("', '")
                            .append(splitedLine[1])
                            .append("', '")
                            .append(splitedLine[2])
                            .append("')");
                    insertQueryParts.add(insertFansBuilder);
                }
                bufferedReader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                int result = dbc.addFanfics(insertQueryParts);
                dbc.closeStatement();
                if (result > 0)
                    fromFileTextField.setText("Данные добавлены");
                else
                    fromFileTextField.setText("Ошибка");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void historyChose() {
        chosenHistory
                .replace(0, chosenHistory.length(), originalHistoryTextArea.getText())
                .append(originalHistoryComboBox.getValue()).append("; ");
        originalHistoryTextArea.setText(String.valueOf(chosenHistory));
    }

    @FXML
    private void getFanficButtonClicked() {

        if (jenCheckBox.isSelected()) chosenGenreList.add("Джен");
        if (getCheckBox.isSelected()) chosenGenreList.add("Гет");
        if (slashCheckBox.isSelected()) chosenGenreList.add("Слеш");
        if (otherCheckBox.isSelected()) chosenGenreList.add("Другое");

        if (!chosenHistory.toString().equals("")) {
            Collection<String> collection = Arrays.asList(chosenHistory.toString().trim().split(";"));
            chosenHistoryList = (ArrayList<String>) collection.stream().map(String::trim).collect(Collectors.toList());
        }

        if (chosenHistoryList.isEmpty() && chosenGenreList.isEmpty()) {
            try {
                ResultSet randomFanficResultSet = dbc.getRandomFanfic();
                if (randomFanficResultSet.next()) {
                    fanficLabel.setText("'" + randomFanficResultSet.getString(2) + "'\n" +
                            "Жанр: " + randomFanficResultSet.getString(3) +
                            "\nОригинал: " + randomFanficResultSet.getString(4));
                } else {
                    fanficLabel.setText("Ошибка");
                }
                dbc.closeStatement();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        } else if (chosenHistoryList.isEmpty()) {
            StringBuilder genres = new StringBuilder("(");
            for (String g : chosenGenreList) {
                genres.append("'").append(g).append("', ");
            }
            genres.replace(genres.length() - 2, genres.length(), ")");
            try {
                ResultSet fanficByGenre = dbc.getRandomFanficByGenre(genres);
                if (fanficByGenre.next()) {
                    fanficLabel.setText("'" + fanficByGenre.getString(2) + "'\n" +
                            "Жанр: " + fanficByGenre.getString(3) +
                            "\nОригинал: " + fanficByGenre.getString(4));
                } else {
                    fanficLabel.setText("Ошибка");
                }
                dbc.closeStatement();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        } else if (chosenGenreList.isEmpty()) {
            StringBuilder histories = new StringBuilder("(");
            for (String h : chosenHistoryList) {
                histories.append("'").append(h).append("', ");
            }
            histories.replace(histories.length() - 2, histories.length(), ")");
            try {
                ResultSet fanficByHistory = dbc.getRandomFanficByHistory(histories);
                if (fanficByHistory.next()) {
                    fanficLabel.setText("'" + fanficByHistory.getString(2) + "'\n" +
                            "Жанр: " + fanficByHistory.getString(3) +
                            "\nОригинал: " + fanficByHistory.getString(4));
                } else {
                    fanficLabel.setText("Ошибка");
                }
                dbc.closeStatement();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        } else {
            StringBuilder andGenres = new StringBuilder("(");
            StringBuilder andHistories = new StringBuilder("(");
            int loopSize = Math.max(chosenGenreList.size(), chosenHistoryList.size());
            for (int i = 0; i < loopSize; i++) {
                if (i < chosenGenreList.size())
                    andGenres.append("'").append(chosenGenreList.get(i)).append("', ");
                if (i < chosenHistoryList.size())
                    andHistories.append("'").append(chosenHistoryList.get(i)).append("', ");
            }
            andGenres.replace(andGenres.length() - 2, andGenres.length(), ")");
            andHistories.replace(andHistories.length() - 2, andHistories.length(), ")");
            String inPart = andGenres.append(" AND original_history IN ").append(andHistories).toString();
            try {
                ResultSet fanficByGenreAndHistory = dbc.getRandomFanficByGenreAndHistory(inPart);
                if (fanficByGenreAndHistory.next()) {
                    fanficLabel.setText("'" + fanficByGenreAndHistory.getString(2) + "'\n" +
                            "Жанр: " + fanficByGenreAndHistory.getString(3) +
                            "\nОригинал: " + fanficByGenreAndHistory.getString(4));
                } else {
                    fanficLabel.setText("Ошибка");
                }
                dbc.closeStatement();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
    }

}
