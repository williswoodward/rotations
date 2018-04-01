import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.Sheets;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

public class Main {
    // SHEET VARIABLES
    private static Sheets _sheets;

    // GOOGLE VARIABLES
    private static final String APPLICATION_NAME = "Setters of Catan Rotations";
    private static final java.io.File DATA_STORE_DIR = new java.io.File(
            System.getProperty("user.home"), "/.credentials/sheets.googleapis.com-setters-rotations");
    private static FileDataStoreFactory DATA_STORE_FACTORY;
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static HttpTransport HTTP_TRANSPORT;
    private static final List<String> SCOPES =
            Collections.singletonList(SheetsScopes.SPREADSHEETS_READONLY);

    static {
        try {
            HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
            DATA_STORE_FACTORY = new FileDataStoreFactory(DATA_STORE_DIR);
        } catch (Throwable t) {
            t.printStackTrace();
            System.exit(1);
        }
    }

    private static List<Lineup42> _best_lineups = new ArrayList<>();
    private static int _search_count = 0;

    /**
     * APPLICATION START
     */
    public static void main(String[] args) throws IOException {
        // Build a new authorized API client service.
        _sheets = getSheetsService();
        getPlayerDataFromSheets();
        searchAllLineups(Arrays.asList(Config._players), 0);

        System.out.println("\nTOP LINEUPS");
        System.out.println("-----------");
        for (Lineup42 lineup : _best_lineups) {
            System.out.println("\n" + lineup);
        }
    }

    private static void getPlayerDataFromSheets() throws IOException {
        for (Player player : Config._players) {
            player.initData(_sheets);
        }
    }

    private static void searchAllLineups(List<Player> players, int pos){
        for(int i = pos; i < players.size(); i++){
            if (pos == 0) {
                System.out.println("Searching lineups starting with " + players.get(_search_count) + "...");
                _search_count++;
            }
            Collections.swap(players, i, pos);
            searchAllLineups(players, pos+1);
            Collections.swap(players, pos, i);
        }

        if (pos == players.size() - 1){
            createAndSearchLineup(players);
        }
    }

    private static void createAndSearchLineup(List<Player> players) {
        Lineup42 lineup = new Lineup42(players);
        if (_best_lineups.size() < Config.NUM_LINEUPS) {
            _best_lineups.add(new Lineup42(lineup));
            _best_lineups.sort(Comparator.comparing(Lineup42::getValue).reversed());
        } else if (lineup.getValue().compareTo(_best_lineups.get(_best_lineups.size() - 1).getValue()) > 0) {
            _best_lineups.remove(_best_lineups.size() -1);
            _best_lineups.add(new Lineup42(lineup));
            _best_lineups.sort(Comparator.comparing(Lineup42::getValue).reversed());
        }

        // DEBUG: Uncomment!
//        System.out.println(lineup);
    }


    /**
     * Creates an authorized Credential object.
     *
     * @return an authorized Credential object.
     * @throws IOException exception
     */
    private static Credential authorize() throws IOException {
        // Load client secrets.
        InputStream in =
                Main.class.getResourceAsStream("/client_secret.json");
        GoogleClientSecrets clientSecrets =
                GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow =
                new GoogleAuthorizationCodeFlow.Builder(
                        HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                        .setDataStoreFactory(DATA_STORE_FACTORY)
                        .setAccessType("offline")
                        .build();
        Credential credential = new AuthorizationCodeInstalledApp(
                flow, new LocalServerReceiver()).authorize("user");
        System.out.println(
                "Credentials saved to " + DATA_STORE_DIR.getAbsolutePath());
        return credential;
    }

    /**
     * Build and return an authorized Sheets API client service.
     *
     * @return an authorized Sheets API client service
     * @throws IOException exception
     */
    private static Sheets getSheetsService() throws IOException {
        Credential credential = authorize();
        return new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
                .setApplicationName(APPLICATION_NAME)
                .build();
    }
}