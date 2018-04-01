import java.math.BigDecimal;

final class Config {

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

    // Match Config
    static Player[] _players = {
            new Player("Andrew", true, Position.MIDDLE, Position.OUTSIDE)
                .withBlk(new BigDecimal(0.15)),
            new Player("Avi", true, Position.MIDDLE, Position.OUTSIDE),
            new Player("Chris", true, Position.OUTSIDE),
            new Player("Denise", false, Position.OUTSIDE, Position.SETTER),
//            new Player("Dennis", true, Position.OUTSIDE, Position.MIDDLE),
            new Player("Hoff", true, Position.MIDDLE),
//            new Player("Jiyu", false, Position.SETTER),
//            new Player("Katie", false, Position.SETTER),
            new Player("Mark", true, Position.OUTSIDE, Position.SETTER),
//            new Player("Regena", false, Position.MIDDLE, Position.OUTSIDE, Position.SETTER),
            new Player("Sue",false, Position.SETTER)
    };

    static final int[] STRONG_OPP_HIT_ROTATIONS = {0, 1, 2, 7, 8, 9};

    // Position Config
    static final BigDecimal OFFENSE_COMPOUND_PENALTY = new BigDecimal(0.5);
    static final BigDecimal DEFENSE_COMPOUND_PENALTY = new BigDecimal(0.2);

    static final BigDecimal WT_MIDDLE_BLK = new BigDecimal(1);
    static final BigDecimal WT_MIDDLE_HIT = new BigDecimal(0.5);

    static final BigDecimal WT_OUTSIDE_HIT = new BigDecimal(0.8);
    static final BigDecimal WT_OUTSIDE_BLK = new BigDecimal(0.2);

    static final BigDecimal WT_BACKLEFT_DIG = new BigDecimal(0.6);
    static final BigDecimal WT_BACKLEFT_PASS = new BigDecimal(0.3);
    static final BigDecimal WT_BACKLEFT_RCV = new BigDecimal(0.3);

    static final BigDecimal WT_BACKMID_DIG = new BigDecimal(0.1);
    static final BigDecimal WT_BACKMID_PASS = new BigDecimal(0.1);
    static final BigDecimal WT_BACKMID_RCV = new BigDecimal(0.4);

    static final BigDecimal WT_BACKRIGHT_DIG = new BigDecimal(0.4);
    static final BigDecimal WT_BACKRIGHT_PASS = new BigDecimal(0.3);
    static final BigDecimal WT_BACKRIGHT_RCV = new BigDecimal(0.3);

    // Rotation Config
    static final int NUM_ROTATION_SIDES = 1;
    static final boolean MULTIPLE_ROTATION_SHIFT_LEFT = true;

    static final int EXPECTED_NUM_ROTATIONS = 9;
    static final int MAX_ROTATIONS = 14;

    // Court Config
    static final int COURT_SIZE = 6;
    static final int MAX_MALES = 4;

    // Search Config
    static final int NUM_LINEUPS = 5;

    // Data Config
    static final String SPREADSHEET_ID = "1wPdRvmWy286esCAmKd66aRI8R-3Ni2-AI-xMK7mjGnA";
    static final String DATA_RANGE = "H3:H48";
    static final int DATA_MIN = 10;

    static final int DATA_COLUMN_OFFSET = 3;
    static final int DATA_COLUMN_SRV_EFF = 3;
    static final int DATA_COLUMN_SRV_TOT = 8;
    static final int DATA_COLUMN_HIT_EFF = 10;
    static final int DATA_COLUMN_HIT_TOT = 15;
    static final int DATA_COLUMN_SET_EFF = 17;
    static final int DATA_COLUMN_SET_TOT = 21;
    static final int DATA_COLUMN_RCV_EFF = 24;
    static final int DATA_COLUMN_RCV_TOT = 28;
    static final int DATA_COLUMN_BLK_EFF = 30;
    static final int DATA_COLUMN_BLK_TOT = 35;
    static final int DATA_COLUMN_DIG_EFF = 37;
    static final int DATA_COLUMN_DIG_TOT = 42;
    static final int DATA_COLUMN_PASS_EFF = 44;
    static final int DATA_COLUMN_PASS_TOT = 48;
}
