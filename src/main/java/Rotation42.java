import javafx.util.Pair;

import java.math.BigDecimal;
import java.util.Arrays;
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
        generateRowOptionsAll(row, pos, _backRowOptions);
    }

    @Override
    protected BigDecimal evaluateFrontRowOption(List<Pair<Player, Position>> option) {
        BigDecimal total = BigDecimal.ZERO;

        for (Pair<Player, Position> playerAssignment : option) {
            switch (playerAssignment.getValue()) {
                case MIDDLE:
                    total = total.add(evaluateMiddle(playerAssignment.getKey()));
                    break;
                case OUTSIDE:
                    total = total.add(evaluateOutside(playerAssignment.getKey()));
                    break;
                case SETTER:
                    total = total.add(evaluateSetter(playerAssignment.getKey()));
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
    protected BigDecimal evaluateBackRowOption(List<Player> option) {
        BigDecimal total = BigDecimal.ZERO;

        switch (option.size()) {
            case 1:
                total = total.add(evaluateBackLeft(option.get(0)));
                break;
            case 2:
                total = total.add(evaluateBackLeft(option.get(0)));
                total = total.add(evaluateBackRight(option.get(1)));
                break;
            case 3:
                total = total.add(evaluateBackLeft(option.get(0)));
                total = total.add(evaluateBackMid(option.get(1)));
                total = total.add(evaluateBackRight(option.get(2)));
                break;
            default:
                break;
        }

        return total;
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

        return dig.add(player.getRcvNormalized().multiply(Config.WT_BACKLEFT_RCV))
                .add(player.getPassNormalized().multiply(Config.WT_BACKLEFT_PASS));
    }

    private BigDecimal evaluateBackMid(Player player) {
        return player.getDigNormalized().multiply(Config.WT_BACKMID_DIG)
                .add(player.getRcvNormalized().multiply(Config.WT_BACKMID_RCV))
                .add(player.getPassNormalized().multiply(Config.WT_BACKMID_PASS));
    }

    private BigDecimal evaluateBackRight(Player player) {
        return player.getDigNormalized().multiply(Config.WT_BACKRIGHT_DIG)
                .add(player.getRcvNormalized().multiply(Config.WT_BACKRIGHT_RCV))
                .add(player.getPassNormalized().multiply(Config.WT_BACKRIGHT_PASS))
                .add(player.getSrvNormalized().multiply(Config.WT_BACKRIGHT_SRV));
    }

    @Override
    boolean lackingHitters(List<Pair<Player, Position>> option) {
        for (Pair<Player, Position> playerAssignment : option) {
            if ((playerAssignment.getValue() == Position.MIDDLE || playerAssignment.getValue() == Position.OUTSIDE)
                    && playerAssignment.getKey().getHitNormalized().compareTo(Config.HIT_BASELINE) > 0) {
                return false;
            }
        }

        return true;
    }
}
