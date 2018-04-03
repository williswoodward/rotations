import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

@WebServlet(name = "RotationsServlet", urlPatterns = {"results"}, loadOnStartup = 1)
public class Main extends HttpServlet {
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

    private static StringBuffer _result_str = new StringBuffer();
    static StringBuffer _import_str = new StringBuffer();

    private static List<Lineup> _best_lineups = new ArrayList<>();
    private static int _search_count = 0;

    /**
     * LOCAL APPLICATION
     */
    public static void main(String[] args) throws IOException {
        _sheets = getSheetsService();
        getPlayerDataFromSheets(Config._players);
        searchAllLineups(Arrays.asList(Config._players), 0);

        printBestLineups();
        System.out.println(_result_str.append(_import_str));
    }

    /**
     * WEB APPLICATION
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        _result_str = new StringBuffer();
        _import_str = new StringBuffer();
        _sheets = getSheetsService();
        _best_lineups = new ArrayList<>();
        _search_count = 0;

        Player[] players = parseRequest(request);
        addPlayerOverridesFromConfig(players);
        getPlayerDataFromSheets(players);

        searchAllLineups(Arrays.asList(players), 0);

        printBestLineups();
        String result = _result_str.append(_import_str).toString().replace("\n", "<br />");

        request.setAttribute("results", result);
        request.getRequestDispatcher("response.jsp").forward(request, response);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        response.sendRedirect(request.getContextPath());
    }

    private static void getPlayerDataFromSheets(Player[] players) throws IOException {
        for (Player player : players) {
            player.initData(_sheets);
        }
    }

    private static void searchAllLineups(List<Player> players, int pos) {
        for (int i = pos; i < players.size(); i++) {
            if (pos == 0) {
                System.out.printf("Searching lineups starting with %s...\n", players.get(_search_count));
                _search_count++;
            }
            Collections.swap(players, i, pos);
            searchAllLineups(players, pos + 1);
            Collections.swap(players, pos, i);
        }

        if (pos == players.size() - 1) {
            createAndSearchLineup(players);
        }
    }

    private static void createAndSearchLineup(List<Player> players) {
        Lineup lineup = new Lineup(players);
        if (_best_lineups.size() < Config.NUM_LINEUPS) {
            _best_lineups.add(new Lineup(lineup));
            _best_lineups.sort(Comparator.comparing(Lineup::getValue).reversed());
        } else if (lineup.getValue().compareTo(_best_lineups.get(_best_lineups.size() - 1).getValue()) > 0) {
            _best_lineups.remove(_best_lineups.size() - 1);
            _best_lineups.add(new Lineup(lineup));
            _best_lineups.sort(Comparator.comparing(Lineup::getValue).reversed());
        }

        // DEBUG: Uncomment!
//        Main._result_str.append("\n").append(lineup);
    }

    private static void printBestLineups() {
        _result_str.append("TOP LINEUPS");
        _result_str.append("\n-----------");
        for (Lineup lineup : _best_lineups) {
            _result_str.append("\n\n").append(lineup).append("\n");
        }
    }

    private Player[] parseRequest(HttpServletRequest request) {
        List<Player> players = new ArrayList<>();

        //TODO: Can't change positions without full refresh?
        for (int i = 1; i <= 11; i++) {
            String paramPrefix = Integer.toString(i) + "_";
            if (i < 10) {
                paramPrefix = "0" + paramPrefix;
            }
            // DEBUG: Uncomment!
//            System.out.println(paramPrefix);

            String nameStr = paramPrefix + "name";
            String name = request.getParameter(nameStr);

            String femaleStr = paramPrefix + "female";
            boolean isFemale = Integer.parseInt(request.getParameter(femaleStr)) == 1;

            List<Position> positions = new ArrayList<>();
            String middleStr = paramPrefix + "middle";
            if (request.getParameter(middleStr) != null) {
                positions.add(Position.MIDDLE);
            }
            String outsideStr = paramPrefix + "outside";
            if (request.getParameter(outsideStr) != null) {
                positions.add(Position.OUTSIDE);
            }
            String setterStr = paramPrefix + "setter";
            if (request.getParameter(setterStr) != null) {
                positions.add(Position.SETTER);
            }

            String playingStr = paramPrefix + "playing";
            boolean isPlaying = Integer.parseInt(request.getParameter(playingStr)) == 1;

            if (isPlaying) {
                Position[] positionArr = new Position[positions.size()];
                players.add(new Player(name, isFemale, positions.toArray(positionArr)));
            }
        }

        Player[] playerArr = new Player[players.size()];
        return players.toArray(playerArr);
    }

    private void addPlayerOverridesFromConfig(Player[] players) {
        for (Player player : players) {
            Optional<Player> configMatch = Arrays.stream(Config._players).filter(e -> e.getName().equals(player.getName())).findFirst();
            if (configMatch.isPresent()) {
                Player configPlayer = configMatch.get();
                player.withSrv(configPlayer.getSrv());
                player.withHit(configPlayer.getHit());
                player.withSet(configPlayer.getSet());
                player.withRcv(configPlayer.getRcv());
                player.withBlk(configPlayer.getBlk());
                player.withDig(configPlayer.getDig());
                player.withPass(configPlayer.getPass());
            }
        }
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
        return new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, setHttpTimeout(credential))
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    private static HttpRequestInitializer setHttpTimeout(final HttpRequestInitializer requestInitializer) {
        return httpRequest -> {
            requestInitializer.initialize(httpRequest);
            httpRequest.setConnectTimeout(3 * 60000);  // 3 minutes connect timeout
            httpRequest.setReadTimeout(3 * 60000);  // 3 minutes read timeout
        };
    }
}