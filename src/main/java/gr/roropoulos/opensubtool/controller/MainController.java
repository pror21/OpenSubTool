package gr.roropoulos.opensubtool.controller;

import com.github.wtekiela.opensub4j.api.OpenSubtitles;
import com.github.wtekiela.opensub4j.impl.OpenSubtitlesImpl;
import com.github.wtekiela.opensub4j.response.SubtitleInfo;
import com.sun.webkit.network.URLs;
import gr.roropoulos.opensubtool.helper.LanguageHelper;
import gr.roropoulos.opensubtool.manager.MovieManager;
import gr.roropoulos.opensubtool.manager.SubtitleFileManager;
import gr.roropoulos.opensubtool.model.Language;
import gr.roropoulos.opensubtool.model.Movie;
import gr.roropoulos.opensubtool.model.Subtitle;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.effect.Reflection;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Pair;
import javafx.util.StringConverter;
import org.apache.xmlrpc.XmlRpcException;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.prefs.Preferences;

public class MainController implements Initializable {
    @FXML
    private ComboBox<Language> langComboBox;
    @FXML
    private TextField directoryTextField;
    @FXML
    private TableView<Movie> moviesTableView;
    @FXML
    private TableColumn<Movie, String> movieTitleTableColumn;
    @FXML
    private TableColumn<Movie, Subtitle> movieSubtitleTableColumn;
    @FXML
    private TableColumn<Movie, Boolean> checkBoxTableColumn;
    @FXML
    private HBox loggedInHBox, loggedOutHBox;
    @FXML
    private Label usernameLabel, logoLabel;
    @FXML
    private MenuItem selectAllMenuItem, deselectAllMenuItem, selectOnlyMenuItem;
    @FXML
    private Button fetchButton, downloadButton;

    private List<Movie> movieList;
    private Preferences prefs;
    private MovieManager movieManager;
    private OpenSubtitles openSubtitles = null;
    private Timer timer;
    private static final String USER_AGENT = "OSTestUserAgentTemp";

    public void initialize(URL fxmlFileLocation, ResourceBundle resources) {
        // Set Preferences node
        prefs = Preferences.userRoot().node("opensubtool");

        try {
            openSubtitles = new OpenSubtitlesImpl(URLs.newURL("http://api.opensubtitles.org/xml-rpc"));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        setupLogo();
        setupGui();
    }

    private void setupGui() {
        fetchButton.setDisable(true);
        downloadButton.setDisable(true);
        // Load user and login if exists
        if (prefs.get("username", null) != null && prefs.get("password", null) != null) {
            login(prefs.get("username", null), prefs.get("password", null), prefs.get("lang", "eng"));
        }

        // Load languages
        List<Language> languageList = LanguageHelper.getLanguageList();
        populateLangComboBox(languageList);

        // Load language and preselect if exist
        if (prefs.get("lang", null) != null) {
            String langCode = prefs.get("lang", null);

            Language language = languageList.stream()
                    .filter(item -> item.getCode().equals(langCode))
                    .findFirst().get();
            langComboBox.getSelectionModel().select(language);
        }

        // Load movie path if exists
        if (prefs.get("moviePath", null) != null) {
            directoryTextField.setText(prefs.get("moviePath", null));
            // Load movie files
            loadMovieFiles(directoryTextField.getText());
        }

        // Set table view context menu items
        selectAllMenuItem.setOnAction(t -> {
            for (Movie movie : movieList)
                movie.setDownloadProperty(true);
        });

        selectOnlyMenuItem.setOnAction(t -> {
            for (Movie movie : movieList) {
                if (movieManager.checkIfSubtitleExist(movie))
                    movie.setDownloadProperty(false);
                else
                    movie.setDownloadProperty(true);
            }
        });

        deselectAllMenuItem.setOnAction(t -> {
            for (Movie movie : movieList)
                movie.setDownloadProperty(false);
        });
    }

    private void loadMovieFiles(String moviePath) {
        moviesTableView.getItems().clear();
        movieManager = new MovieManager(moviePath);
        if (movieManager.checkIfPathExists()) {
            movieList = movieManager.getInitialMovieList();
            populateTableView(movieList);
            setupCheckBoxColumn();
        }
    }

    private void populateLangComboBox(List<Language> languageList) {
        langComboBox.getItems().addAll(languageList);
        langComboBox.valueProperty().addListener((ov, previous, current) -> prefs.put("lang", current.getCode()));
    }

    private void populateTableView(List<Movie> moviePathList) {
        ObservableList<Movie> pathObservableList = FXCollections.observableList(moviePathList);
        moviesTableView.getItems().addAll(pathObservableList);
        movieTitleTableColumn.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getMovieFile().getName()));
    }


    @FXML
    private void browseButtonActionHandler() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Select your movies directory");
        File selectedDirectory = directoryChooser.showDialog(directoryTextField.getScene().getWindow());
        if (selectedDirectory != null) {
            directoryTextField.setText(selectedDirectory.getAbsolutePath());
            prefs.put("moviePath", selectedDirectory.getAbsolutePath());
            movieList.clear();
            moviesTableView.getItems().clear();
            loadMovieFiles(selectedDirectory.getAbsolutePath());
        }
    }

    @FXML
    private void loginButtonActionHandler() {
        // Create the custom dialog.
        Dialog<Pair<String, String>> dialog = new Dialog<>();
        dialog.setTitle("Login to OpenSubtitles");
        dialog.setHeaderText("Please use your OpenSubtitles.org account\n" +
                "credentials to login in order to use this application.");

        // Set the icon (must be included in the project).
        dialog.setGraphic(new ImageView(this.getClass().getResource("/img/login.png").toString()));

        // Set the button types.
        ButtonType loginButtonType = new ButtonType("Login", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(loginButtonType, ButtonType.CANCEL);

        // Create the username and password labels and fields.
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField username = new TextField();
        username.setPromptText("Username");
        PasswordField password = new PasswordField();
        password.setPromptText("Password");

        grid.add(new Label("Username:"), 0, 0);
        grid.add(username, 1, 0);
        grid.add(new Label("Password:"), 0, 1);
        grid.add(password, 1, 1);

        // Enable/Disable login button depending on whether a username was entered.
        Node loginButton = dialog.getDialogPane().lookupButton(loginButtonType);
        loginButton.setDisable(true);

        // Do some validation (using the Java 8 lambda syntax).
        username.textProperty().addListener((observable, oldValue, newValue) -> loginButton.setDisable(newValue.trim().isEmpty()));

        dialog.getDialogPane().setContent(grid);

        // Request focus on the username field by default.
        Platform.runLater(username::requestFocus);

        // Convert the result to a username-password-pair when the login button is clicked.
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == loginButtonType) {
                return new Pair<>(username.getText(), password.getText());
            }
            return null;
        });

        Optional<Pair<String, String>> result = dialog.showAndWait();

        result.ifPresent(usernamePassword -> login(usernamePassword.getKey(), usernamePassword.getValue(), prefs.get("lang", "eng")));
    }

    @FXML
    private void logoutButtonActionHandler() {
        prefs.remove("username");
        prefs.remove("password");
        loggedInHBox.setVisible(false);
        loggedOutHBox.setVisible(true);
        fetchButton.setDisable(true);
        downloadButton.setDisable(true);
        stopTimer();
        try {
            openSubtitles.logout();
        } catch (XmlRpcException e) {
            e.printStackTrace();
        }
    }

    private void keepConnectionAlive() {
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {

                try {
                    openSubtitles.noop();
                } catch (XmlRpcException e) {
                    e.printStackTrace();
                }

            }
        }, 0, 600000);
    }

    private void stopTimer() {
        timer.cancel();
        timer.purge();
    }

    private void login(String username, String password, String lang) {
        try {
            openSubtitles.login(username, password, lang, USER_AGENT);
            prefs.put("username", username);
            prefs.put("password", password);
            loggedOutHBox.setVisible(false);
            loggedInHBox.setVisible(true);
            usernameLabel.setText(username);
            keepConnectionAlive();
            fetchButton.setDisable(false);
            downloadButton.setDisable(false);
        } catch (XmlRpcException e) {
            showFailedLoginDialog();
            e.printStackTrace();
        }
    }

    private void showFailedLoginDialog() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Login failed");
        alert.setHeaderText("User authentication failed.");
        alert.setContentText("Please check your username and your password.\nIf you don't have an OpenSubtitles account please register one.");
        alert.showAndWait();
    }

    @FXML
    private void fetchButtonActionHandler() {
        Language selectedLanguage = langComboBox.getSelectionModel().getSelectedItem();
        for (Movie movie : movieList) {
            if (movie.getDownloadProperty()) {
                Subtitle subtitle = new Subtitle();
                List<SubtitleInfo> movieSubtitleInfoList;

                // Get subs from OpenSubtitles.org
                movieSubtitleInfoList = movieManager.fetchMovieSubtitles(movie, selectedLanguage.getCode(), openSubtitles);

                // Set subtitles
                subtitle.getSubtitleInfoList().setAll(movieSubtitleInfoList);
                movie.setSubtitle(subtitle);

                // Pre-select the subtitle that has the most downloads
                if (!subtitle.getSubtitleInfoList().isEmpty()) {
                    SubtitleInfo subtitleInfoPopular = Collections.max(subtitle.getSubtitleInfoList(), Comparator.comparing(SubtitleInfo::getDownloadsNo));
                    movie.getSubtitle().setSelectedInfo(subtitleInfoPopular);
                }
            }
        }
        populateSubtitleChoiceBox();
    }

    private void populateSubtitleChoiceBox() {
        if (!movieList.isEmpty()) {
            movieSubtitleTableColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getSubtitle()));
            movieSubtitleTableColumn.setCellFactory(col -> {
                ComboBox<SubtitleInfo> combo = new ComboBox<>();
                combo.setMaxWidth(Double.MAX_VALUE);

                combo.setConverter(
                        new StringConverter<SubtitleInfo>() {
                            @Override
                            public String toString(SubtitleInfo subtitleInfo) {
                                if (subtitleInfo == null) {
                                    return null;
                                } else {
                                    return subtitleInfo.getFileName();
                                }
                            }

                            @Override
                            public SubtitleInfo fromString(String s) {
                                return null;
                            }
                        });

                TableCell<Movie, Subtitle> cell = new TableCell<Movie, Subtitle>() {
                    @Override
                    protected void updateItem(Subtitle subInfo, boolean empty) {
                        super.updateItem(subInfo, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            if (subInfo != null) {
                                combo.getItems().setAll(subInfo.getSubtitleInfoList());
                                combo.setValue(subInfo.getSelectedSubtitleInfo());
                            }
                            setGraphic(combo);
                        }
                    }
                };

                combo.valueProperty().addListener((obs, oldValue, newValue) ->
                        cell.getItem().setSelectedInfo(newValue));

                return cell;
            });
        }
    }

    private void setupCheckBoxColumn() {
        checkBoxTableColumn.setCellValueFactory(new PropertyValueFactory<>("downloadProperty"));
        checkBoxTableColumn.setCellFactory(p -> {
            final CheckBoxTableCell<Movie, Boolean> ctCell = new CheckBoxTableCell<>();
            ctCell.setSelectedStateCallback(index -> moviesTableView.getItems().get(index).downloadProperty());
            return ctCell;
        });
    }

    @FXML
    private void downloadButtonActionHandler() {
        int counter = 0;
        for (Movie movie : movieList) {
            if (movie.getDownloadProperty() && movie.getSubtitle() != null && movie.getSubtitle().getSelectedSubtitleInfo() != null) {
                SubtitleFileManager subtitleFileManager = new SubtitleFileManager(movie);
                subtitleFileManager.downloadSubtitle();
                counter++;
            }
        }
        showResultDialog(counter);
    }

    private void showResultDialog(int counter) {
        Alert alert;
        if (counter < 1) {
            alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Subtitles not found or set to be downloaded.");
        } else {
            alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Success");
            alert.setHeaderText(null);
            alert.setContentText(counter + " subtitle(s) downloaded!");
            movieList.clear();
            moviesTableView.getItems().clear();
            loadMovieFiles(directoryTextField.getText());
        }
        alert.showAndWait();
    }

    private void setupLogo() {
        Reflection r = new Reflection();
        r.setFraction(0.75f);
        r.setBottomOpacity(0);
        r.setTopOffset(-10);
        r.setTopOpacity(0.5);
        logoLabel.setOnMouseEntered(e -> logoLabel.setEffect(r));
        logoLabel.setOnMouseExited(e -> logoLabel.setEffect(null));
        logoLabel.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent e) {
                try {
                    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/AboutView.fxml"));
                    Parent root = fxmlLoader.load();
                    Stage stage = new Stage();
                    stage.setTitle("OpenSubTool - About");
                    stage.initModality(Modality.WINDOW_MODAL);
                    stage.setResizable(false);
                    stage.setScene(new Scene(root));
                    stage.showAndWait();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
    }
}
