import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Rotation42 extends Rotation {

    Rotation42(List<Player> players, int index) {
        super(players, index);
    }

    @Override
    protected void generateFrontRowOptions(List<Player> row) {
        generateRowOptionsUniquePositions(row, _frontRowOptions, true);
    }

    @Override
    protected void generateBackRowOptions(List<Player> row, int pos) {
        if (!_isPlayablePositions) return;

        for (int i = 0; i < row.size(); i++) {
            for (int j = 0; j < row.size(); j++) {
                if (j != i) {
                    for (int k = 0; k < row.size(); k++) {
                        if (k != i && k != j) {
                            List<PlayerAssignment> option = new ArrayList<>();
                            option.add(new PlayerAssignment(row.get(0), getPositionForRowIndex(i)));
                            if (row.size() > 1) option.add(new PlayerAssignment(row.get(1), getPositionForRowIndex(j)));
                            if (row.size() > 2) option.add(new PlayerAssignment(row.get(2), getPositionForRowIndex(k)));
                            _backRowOptions.add(option);
                        }
                    }
                }
            }
        }
    }

    @Override
    protected BigDecimal evaluateFrontRowOption(List<PlayerAssignment> option) {
        BigDecimal total = BigDecimal.ZERO;

        for (PlayerAssignment playerAssignment : option) {
            switch (playerAssignment.getPosition()) {
                case MIDDLE:
                    total = total.add(evaluateMiddle(playerAssignment.getPlayer()));
                    break;
                case OUTSIDE:
                    total = total.add(evaluateOutside(playerAssignment.getPlayer()));
                    break;
                case SETTER:
                    total = total.add(evaluateSetter(playerAssignment.getPlayer()));
                    break;
                default:
                    break;
            }
        }

        // Compound lack of offense
        if (lackingHitters(option)) {
            total = total.multiply(Config.OFFENSE_COMPOUND_PENALTY);
        }

        return total;
    }

    @Override
    protected BigDecimal evaluateBackRowOption(List<PlayerAssignment> option) {
        BigDecimal total = BigDecimal.ZERO;

        for (PlayerAssignment playerAssignment : option) {
            switch (playerAssignment.getPosition()) {
                case BACKLEFT:
                    total = total.add(evaluateBackLeft(playerAssignment.getPlayer()));
                    break;
                case BACKRIGHT:
                    total = total.add(evaluateBackRight(playerAssignment.getPlayer()));
                    break;
                case BACKMID:
                    total = total.add(evaluateBackMid(playerAssignment.getPlayer()));
                    break;
                default:
                    break;
            }
        }

        return total;
    }

    private Position getPositionForRowIndex(int i) {
        switch (i) {
            case 0:
                return Position.BACKLEFT;
            case 1:
                return Position.BACKRIGHT;
            case 2:
                return Position.BACKMID;
            default:
                return null;
        }
    }

    private BigDecimal evaluateMiddle(Player player) {
        BigDecimal block = player.getBlkNormalized().multiply(Config.WT_MIDDLE_BLK);
        // Adjust for expected opponent big hit rotations
        if (Arrays.stream(Config.STRONG_OPP_HIT_ROTATIONS).anyMatch(e -> e == _index)) {
            block = block.multiply(Config.WT_MIDDLE_STRONG_OPP);
        }
        return block.add(player.getHitNormalized().multiply(Config.WT_MIDDLE_HIT));
    }

    private BigDecimal evaluateOutside(Player player) {
        return player.getBlkNormalized().multiply(Config.WT_OUTSIDE_BLK)
                .add(player.getHitNormalized().multiply(Config.WT_OUTSIDE_HIT));
    }

    private BigDecimal evaluateSetter(Player player) {
        return player.getBlkNormalized().multiply(Config.WT_SETTER_BLK)
                .add(player.getSetNormalized().multiply(Config.WT_SETTER_SET));
    }

    private BigDecimal evaluateBackLeft(Player player) {
        BigDecimal dig = player.getDigNormalized().multiply(Config.WT_BACKLEFT_DIG);
        if (Arrays.stream(Config.STRONG_OPP_HIT_ROTATIONS).anyMatch(e -> e == _index)) {
            dig = dig.multiply(Config.WT_BACKLEFT_STRONG_OPP);
        }

        return dig.add(player.getPassNormalized().multiply(Config.WT_BACKLEFT_PASS));
    }

    private BigDecimal evaluateBackMid(Player player) {
        return player.getDigNormalized().multiply(Config.WT_BACKMID_DIG)
                .add(player.getPassNormalized().multiply(Config.WT_BACKMID_PASS));
    }

    private BigDecimal evaluateBackRight(Player player) {
        return player.getDigNormalized().multiply(Config.WT_BACKRIGHT_DIG)
                .add(player.getPassNormalized().multiply(Config.WT_BACKRIGHT_PASS));
    }

    @Override
    boolean lackingHitters(List<PlayerAssignment> option) {
        for (PlayerAssignment playerAssignment : option) {
            if ((playerAssignment.getPosition() == Position.MIDDLE || playerAssignment.getPosition() == Position.OUTSIDE)
                    && playerAssignment.getPlayer().getHitNormalized().compareTo(Config.HIT_BASELINE) > 0) {
                return false;
            }
        }

        return true;
    }
}
