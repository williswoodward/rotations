import com.google.common.base.Preconditions;
import javafx.util.Pair;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Rotation42 {
    private List<Player> _players;
    private int _index;

    private BigDecimal _value = BigDecimal.ZERO;

    private boolean _isPlayablePositions;
    private List<List<Pair<Player, Position>>> _frontRowOptions = new ArrayList<>();
    ;
    private List<List<Player>> _backRowOptions = new ArrayList<>();

    Rotation42(List<Player> players, int index) {
        Preconditions.checkArgument(players.size() == Constants.COURT_SIZE);

        _players = players;
        _index = index;
        _isPlayablePositions = false;

        // NOTE: Examining the front row will also determine if this rotation is "valid," position-wise.
        generateFrontRowOptions(getFrontRow());

        if (_isPlayablePositions) {
            generateBackRowOptions(getBackRow(), 0);
            _value = generateValue();
        }
    }

    BigDecimal getValue() {
        return _value.round(new MathContext(4));
    }

    long countFemales() {
        return _players.stream().filter(Player::isFemale).count();
    }

    boolean isPlayablePositions() {
        return _isPlayablePositions;
    }

    boolean isEquivalentTo(Rotation42 rotation) {
        return _players.equals(rotation._players);
    }

    private BigDecimal generateValue() {
        BigDecimal value = BigDecimal.ZERO;

        value = value.add(evaluateFrontRow());
        value = value.add(evaluateBackRow());

        // Falloff for less likely rotations
        if (_index > Constants.EXPECTED_NUM_ROTATIONS) {
            BigDecimal modifier = new BigDecimal((10 + Constants.EXPECTED_NUM_ROTATIONS - _index)).divide(BigDecimal.TEN);
            if (modifier.compareTo(BigDecimal.ZERO) < 0) {
                modifier = BigDecimal.ZERO;
            }

            value = value.multiply(modifier);
        }

        return value;
    }

    private List<Player> getFrontRow() {
        return _players.subList(0, 3);
    }

    private List<Player> getBackRow() {
        return _players.subList(3, Constants.COURT_SIZE);
    }

    private void generateFrontRowOptions(List<Player> row) {
        Preconditions.checkArgument(row.size() == 3);

        for (int i = 0; i < row.get(0).getPositions().length; i++) {
            for (int j = 0; j < row.get(1).getPositions().length; j++) {
                for (int k = 0; k < row.get(2).getPositions().length; k++) {
                    if ((row.get(0).getPositions()[i] != row.get(1).getPositions()[j]) &&
                            (row.get(0).getPositions()[i] != row.get(2).getPositions()[k]) &&
                            (row.get(1).getPositions()[j] != row.get(2).getPositions()[k])) {
                        List<Pair<Player, Position>> rowOption = new ArrayList<>();
                        rowOption.add(new Pair<>(row.get(0), row.get(0).getPositions()[i]));
                        rowOption.add(new Pair<>(row.get(1), row.get(1).getPositions()[j]));
                        rowOption.add(new Pair<>(row.get(2), row.get(2).getPositions()[k]));
                        _frontRowOptions.add(rowOption);
                        _isPlayablePositions = true;
                    }
                }
            }
        }

    }

    private void generateBackRowOptions(List<Player> row, int pos) {
        for (int i = pos; i < row.size(); i++) {
            Collections.swap(row, i, pos);
            generateBackRowOptions(row, pos + 1);
            Collections.swap(row, pos, i);
        }

        if (pos == row.size() - 1) {
            _backRowOptions.add(new ArrayList<>(row));
        }
    }

    private BigDecimal evaluateFrontRow() {
        BigDecimal total = BigDecimal.ZERO;

        for (List<Pair<Player, Position>> option : _frontRowOptions) {
            BigDecimal rowValue = evaluateFrontRowOption(option);
            if (rowValue.compareTo(total) > 0) {
                total = rowValue;
            }
        }

        return total;
    }

    private BigDecimal evaluateBackRow() {
        BigDecimal total = BigDecimal.ZERO;

        for (List<Player> option : _backRowOptions) {
            BigDecimal rowValue = evaluateBackRowOption(option);
            if (rowValue.compareTo(total) > 0) {
                total = rowValue;
            }
        }

        // Compound lack of serve receive
        if (lackingReceivers(getBackRow())) {
            total = total.multiply(Constants.DEFENSE_COMPOUND_PENALTY);
        }

        return total;
    }

    private BigDecimal evaluateFrontRowOption(List<Pair<Player, Position>> option) {
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
            total = total.multiply(Constants.OFFENSE_COMPOUND_PENALTY);
        }

        return total;
    }

    private BigDecimal evaluateBackRowOption(List<Player> option) {
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
        BigDecimal block = player.getBlk().multiply(Constants.WT_MIDDLE_BLK);
        // Adjust for expected opponent big hit rotations
        if (Arrays.stream(Constants.STRONG_OPP_HIT_ROTATIONS).anyMatch(e -> e == _index)) {
            block = block.multiply(new BigDecimal(2));
        }
        return block.add(player.getHit().multiply(Constants.WT_MIDDLE_HIT));
    }

    private BigDecimal evaluateOutside(Player player) {
        return player.getBlk().multiply(Constants.WT_OUTSIDE_BLK)
                .add(player.getHit().multiply(Constants.WT_OUTSIDE_HIT));
    }

    private BigDecimal evaluateSetter(Player player) {
        return player.getSet();
    }

    private BigDecimal evaluateBackLeft(Player player) {
        BigDecimal dig = player.getDig().multiply(Constants.WT_BACKLEFT_DIG);
        if (Arrays.stream(Constants.STRONG_OPP_HIT_ROTATIONS).anyMatch(e -> e == _index)) {
            dig = dig.multiply(new BigDecimal(2));
        }

        return dig.add(player.getRcv().multiply(Constants.WT_BACKLEFT_RCV))
                .add(player.getPass().multiply(Constants.WT_BACKLEFT_PASS));
    }

    private BigDecimal evaluateBackMid(Player player) {
        return player.getDig().multiply(Constants.WT_BACKMID_DIG)
                .add(player.getRcv().multiply(Constants.WT_BACKMID_RCV))
                .add(player.getPass().multiply(Constants.WT_BACKMID_PASS));
    }

    private BigDecimal evaluateBackRight(Player player) {
        return player.getDig().multiply(Constants.WT_BACKRIGHT_DIG)
                .add(player.getRcv().multiply(Constants.WT_BACKRIGHT_RCV))
                .add(player.getPass().multiply(Constants.WT_BACKRIGHT_PASS))
                .add(player.getSrv());
    }

    private boolean lackingHitters(List<Pair<Player, Position>> option) {
        for (Pair<Player, Position> playerAssignment : option) {
            if ((playerAssignment.getValue() == Position.MIDDLE || playerAssignment.getValue() == Position.OUTSIDE)
                    && playerAssignment.getKey().getHit().compareTo(Constants.HIT_BASELINE) > 0) {
                return false;
            }
        }

        return true;
    }

    private boolean lackingReceivers(List<Player> backRow) {
        for (int i = 0; i < backRow.size(); i++) {
            if (backRow.get(i).getRcv().compareTo(Constants.RCV_BASELINE) < 0 && i + 1 < backRow.size()) {
                if (backRow.get(i + 1).getRcv().compareTo(Constants.RCV_BASELINE) < 0) {
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public String toString() {
        return _index + ": " + java.util.Arrays.toString(_players.toArray()) + " - " + getValue();
    }
}
