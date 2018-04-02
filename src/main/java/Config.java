import java.math.BigDecimal;

final class Config {
    // Match Config
    // IF RUNNING LOCALLY: Comment out individual players to remove them from the lineup
    static Player[] _players = {
            new Player("Andrew", false, Position.MIDDLE, Position.OUTSIDE, Position.SETTER)
                    .withBlk(new BigDecimal(0.15))
                    .withSet(new BigDecimal(0.75)),
            new Player("Avi", false, Position.MIDDLE, Position.OUTSIDE),
            new Player("Chris", false, Position.OUTSIDE),
            new Player("Denise", true, Position.OUTSIDE, Position.SETTER)
                    .withBlk(BigDecimal.ZERO),
            new Player("Dennis", false, Position.OUTSIDE, Position.MIDDLE)
                    .withSrv(new BigDecimal(0.2))
                    .withRcv(new BigDecimal(0.5))
                    .withBlk(new BigDecimal(0.3))
                    .withDig(new BigDecimal(0.4))
                    .withPass(new BigDecimal(0.9)),
            new Player("Hoff", false, Position.MIDDLE),
            new Player("Jiyu", true, Position.SETTER).withBlk(BigDecimal.ZERO)
                    .withBlk(BigDecimal.ZERO),
            new Player("Katie", true, Position.SETTER),
            new Player("Mark", false, Position.OUTSIDE, Position.SETTER),
            new Player("Regena", true, Position.MIDDLE, Position.OUTSIDE, Position.SETTER),
            new Player("Sue", true, Position.SETTER)
                    .withBlk(BigDecimal.ZERO)
    };

    static final int[] STRONG_OPP_HIT_ROTATIONS = {0, 1, 2, 7, 8, 9};


    // Rotation Config

    // The number of rotations we expect to play per set. When exceeded, the value of rotations will drop off sharply.
    static final int EXPECTED_NUM_ROTATIONS = 9;

    // Cap on the number of rotations to look at for any given lineup.
    static final int MAX_ROTATIONS = 12;

    // Set to 2 for Double Rotation. You COULD set it to any integer. Have a party!
    static final int NUM_ROTATION_SIDES = 1;

    // For double rotation, this determines whether the odd number of players out ends up on Left or Right court.
    static final boolean MULTIPLE_ROTATION_SHIFT_LEFT = true;

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
    static final BigDecimal OFFENSE_COMPOUND_PENALTY = new BigDecimal(0.5);
    static final BigDecimal DEFENSE_COMPOUND_PENALTY = new BigDecimal(0.2);

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
