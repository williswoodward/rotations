import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

final class Config {
    // Match Config
    private static List<Player> _players = ImmutableList.of(
            new Player("Andrew", false, Position.OUTSIDE)
                    .withBlk(new BigDecimal(0.15))
                    .withSet(new BigDecimal(0.8)),
            new Player("Avi", false, Position.MIDDLE),
//            new Player("Denise", true, Position.OUTSIDE, Position.SETTER)
//                    .withBlk(new BigDecimal(0.1)),
//            new Player("Dennis", false, Position.MIDDLE),
            new Player("Hoff", false, Position.MIDDLE, Position.OUTSIDE),
            new Player("Jeremy", false, Position.MIDDLE)
                    .withSrv(new BigDecimal(0.3))
                    .withHit(new BigDecimal(0.25))
                    .withBlk(new BigDecimal(0.3))
                    .withRcv(new BigDecimal(0.25))
                    .withDig(new BigDecimal(0.2))
                    .withPass(new BigDecimal(0.8)),
            new Player("Jiyu", true, Position.SETTER)
                    .withBlk(BigDecimal.ZERO),
            new Player("Mark", false, Position.OUTSIDE, Position.SETTER),
            new Player("Nicole", true, Position.SETTER)
                    .withSrv(new BigDecimal(0.2))
                    .withSet(new BigDecimal(0.7))
                    .withRcv(new BigDecimal(0.5))
                    .withDig(new BigDecimal(0.4))
                    .withPass(new BigDecimal(0.9)),
            new Player("Regena", true, Position.MIDDLE, Position.OUTSIDE, Position.SETTER)
                    .withSrv(new BigDecimal(0.2))
                    .withSet(new BigDecimal(0.75))
                    .withHit(new BigDecimal(0.1))
                    .withBlk(new BigDecimal(0.1))
                    .withRcv(new BigDecimal(0.25))
                    .withDig(new BigDecimal(0.2))
                    .withPass(new BigDecimal(0.8)),
            new Player("Sue", true, Position.SETTER)
                    .withBlk(BigDecimal.ZERO)
    );

    /**
     * Return a mutable list of all players.
     */
    public static List<Player> getAllPlayers() {
        return Lists.newArrayList(_players);
    }

    /**
     * Return a list of just the players at the given indices.
     */
    public static List<Player> getPlayersByIndex(Integer... selectedIndices) {
        List<Player> playerList = Lists.newArrayList(_players);
        List<Integer> indicesList = Arrays.asList(selectedIndices);

        return IntStream.range(0, playerList.size())
                .filter(indicesList::contains)
                .mapToObj(playerList::get)
                .collect(Collectors.toList());
    }

    static final int[] STRONG_OPP_HIT_ROTATIONS = {0, 1, 2, 7, 8, 9};


    // Rotation Config

    // The number of rotations we expect to play per set. When exceeded, the value of rotations will drop off sharply.
    static final int EXPECTED_NUM_ROTATIONS = 10;

    // Cap on the number of rotations to look at for any given lineup.
    static final int MAX_ROTATIONS = 14;

    // Set to 2 for Double Rotation. You COULD set it to any integer. Have a party!
    static final int NUM_ROTATION_SIDES = 1;

    // For double rotation, this determines whether the odd number of players out ends up on Left or Right court.
    static final boolean MULTIPLE_ROTATION_SHIFT_LEFT = true;

    // For rotations where only the men rotate out, give everyone roughly equal time off of the court.
    static final boolean EQUALIZE_OFF_TIME = false;

    // Edge case stuff. Just leave it true.
    static final boolean MINIMIZE_SWAPPING = true;

    // Skill Config
    private static final BigDecimal SRV = new BigDecimal(1);
    private static final BigDecimal SET = new BigDecimal(2);
    private static final BigDecimal HIT = new BigDecimal(2);

    private static final BigDecimal RCV = new BigDecimal(1.5);
    private static final BigDecimal BLK = new BigDecimal(1.5);
    private static final BigDecimal DIG = new BigDecimal(1);
    private static final BigDecimal PASS = new BigDecimal(1);

    // Position Config
    static final BigDecimal OFFENSE_COMPOUND_PENALTY = new BigDecimal(0.2);
    static final BigDecimal DEFENSE_COMPOUND_PENALTY = new BigDecimal(0.5);

    static final BigDecimal WT_MIDDLE_BLK = BLK.multiply(new BigDecimal(0.7));
    static final BigDecimal WT_MIDDLE_HIT = HIT.multiply(new BigDecimal(0.4));
    static final BigDecimal WT_MIDDLE_STRONG_OPP = new BigDecimal(3);

    static final BigDecimal WT_OUTSIDE_HIT = HIT.multiply(new BigDecimal(0.6));
    static final BigDecimal WT_OUTSIDE_BLK = BLK.multiply(new BigDecimal(0.1));

    static final BigDecimal WT_SETTER_SET = SET.multiply(new BigDecimal(1));
    static final BigDecimal WT_SETTER_BLK = BLK.multiply(new BigDecimal(0.2));

    static final BigDecimal WT_BACKLEFT_DIG = DIG.multiply(new BigDecimal(0.6));
    static final BigDecimal WT_BACKLEFT_PASS = PASS.multiply(new BigDecimal(0.4));
    static final BigDecimal WT_BACKLEFT_RCV = RCV.multiply(new BigDecimal(0.3));
    static final BigDecimal WT_BACKLEFT_STRONG_OPP = new BigDecimal(2);

    static final BigDecimal WT_BACKMID_DIG = DIG.multiply(new BigDecimal(0.1));
    static final BigDecimal WT_BACKMID_PASS = PASS.multiply(new BigDecimal(0.2));
    static final BigDecimal WT_BACKMID_RCV = RCV.multiply(new BigDecimal(0.4));

    static final BigDecimal WT_BACKRIGHT_DIG = DIG.multiply(new BigDecimal(0.3));
    static final BigDecimal WT_BACKRIGHT_PASS = PASS.multiply(new BigDecimal(0.4));
    static final BigDecimal WT_BACKRIGHT_RCV = RCV.multiply(new BigDecimal(0.3));
    static final BigDecimal WT_BACKRIGHT_SRV = SRV.multiply(new BigDecimal(1));

    // Stat Config
    static final BigDecimal SRV_BASELINE = new BigDecimal(0.1);
    static final BigDecimal SRV_OPTIMAL = new BigDecimal(0.5);

    static final BigDecimal HIT_BASELINE = new BigDecimal(0.1);
    static final BigDecimal HIT_OPTIMAL = new BigDecimal(0.5);

    static final BigDecimal SET_BASELINE = new BigDecimal(0.6);
    static final BigDecimal SET_OPTIMAL = new BigDecimal(0.9);

    static final BigDecimal RCV_BASELINE = new BigDecimal(0.2);
    static final BigDecimal RCV_OPTIMAL = new BigDecimal(0.7);

    static final BigDecimal BLK_BASELINE = new BigDecimal(0.1);
    static final BigDecimal BLK_OPTIMAL = new BigDecimal(0.5);

    static final BigDecimal DIG_BASELINE = new BigDecimal(0.2);
    static final BigDecimal DIG_OPTIMAL = new BigDecimal(0.7);

    static final BigDecimal PASS_BASELINE = new BigDecimal(0.6);
    static final BigDecimal PASS_OPTIMAL = new BigDecimal(0.9);

    // Court Config
    static final int COURT_SIZE = 6;
    static final int MAX_MALES = 4;
    static final int FEMALE_DIVISOR = COURT_SIZE / (COURT_SIZE - MAX_MALES);

    // Search Config
    static final int NUM_LINEUPS = 10;

    // Data Config
    static final String SPREADSHEET_ID = "1MM28VP7KB4f_jvCpmryN3Tlffq8pLEtn5vXuOD91nKs";
    static final String DATA_RANGE = "L3:L50";
    static final int DATA_MIN = 10;

    static final int DATA_COLUMN_OFFSET = 3;
    static final int DATA_COLUMN_SRV_EFF = 3;
    static final int DATA_COLUMN_SRV_TOT = 8;
    static final int DATA_COLUMN_HIT_EFF = 10;
    static final int DATA_COLUMN_HIT_TOT = 15;
    static final int DATA_COLUMN_SET_EFF = 17;
    static final int DATA_COLUMN_SET_TOT = 22;
    static final int DATA_COLUMN_RCV_EFF = 25;
    static final int DATA_COLUMN_RCV_TOT = 29;
    static final int DATA_COLUMN_BLK_EFF = 31;
    static final int DATA_COLUMN_BLK_TOT = 37;
    static final int DATA_COLUMN_DIG_EFF = 39;
    static final int DATA_COLUMN_DIG_TOT = 44;
    static final int DATA_COLUMN_PASS_EFF = 46;
    static final int DATA_COLUMN_PASS_TOT = 50;
}
