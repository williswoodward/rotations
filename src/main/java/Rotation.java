import com.google.common.base.Preconditions;
import javafx.util.Pair;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class Rotation {

    private final List<Player> _players;
    final int _index;
    private BigDecimal _value;
    boolean _isPlayablePositions;

    List<List<Pair<Player, Position>>> _frontRowOptions = new ArrayList<>();
    List<List<Player>> _backRowOptions = new ArrayList<>();

    public Rotation(List<Player> players, int index) {
        Preconditions.checkArgument(players.size() <= Config.COURT_SIZE);

        _players = players;
        _index = index;
        _isPlayablePositions = false;

        generateFrontRowOptions(getFrontRow());
        generateBackRowOptions(getBackRow(), 0);
        _value = generateValue();
    }

    BigDecimal getValue() {
        return _value.round(new MathContext(4));
    }

    int countFemales() {
        return (int) _players.stream().filter(Player::isFemale).count();
    }

    int countMales() {
        return (int) _players.stream().filter(e -> !e.isFemale()).count();
    }

    boolean isPlayablePositions() {
        return _isPlayablePositions;
    }

    protected abstract void generateFrontRowOptions(List<Player> row);

    protected abstract void generateBackRowOptions(List<Player> row, int pos);

    protected abstract BigDecimal evaluateFrontRowOption(List<Pair<Player, Position>> option);

    protected abstract BigDecimal evaluateBackRowOption(List<Player> option);

    abstract boolean lackingHitters(List<Pair<Player, Position>> option);

    private List<Player> getFrontRow() {
        return _players.subList(0, 3);
    }

    private List<Player> getBackRow() {
        return _players.subList(3, _players.size());
    }

    private BigDecimal generateValue() {
        BigDecimal value = BigDecimal.ZERO;

        value = value.add(evaluateFrontRow());
        value = value.add(evaluateBackRow());

        // Falloff for less likely rotations
        if (_index > Config.EXPECTED_NUM_ROTATIONS) {
            BigDecimal modifier = new BigDecimal((10 + (Config.EXPECTED_NUM_ROTATIONS - (_index)) * 2)).setScale(3, RoundingMode.HALF_UP)
                    .divide(BigDecimal.TEN, RoundingMode.HALF_UP);
            if (modifier.compareTo(BigDecimal.ZERO) < 0) {
                modifier = BigDecimal.ZERO;
            }

            value = value.multiply(modifier);
        }

        return value;
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
            total = total.multiply(Config.DEFENSE_COMPOUND_PENALTY);
        }

        return total;
    }

    private boolean lackingReceivers(List<Player> backRow) {
        for (int i = 0; i < backRow.size(); i++) {
            if (backRow.get(i).getRcvNormalized().compareTo(Config.RCV_BASELINE) < 0 && i + 1 < backRow.size()) {
                if (backRow.get(i + 1).getRcvNormalized().compareTo(Config.RCV_BASELINE) < 0) {
                    return true;
                }
            }
        }

        return false;
    }

    void generateRowOptionsUniquePositions(List<Player> row, List<List<Pair<Player, Position>>> optionsList, boolean setPlayable) {
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
                        optionsList.add(rowOption);
                        if (setPlayable) {
                            _isPlayablePositions = true;
                        }
                    }
                }
            }
        }
    }

    void generateRowOptionsAll(List<Player> row, int pos, List<List<Player>> optionsList) {
        for (int i = pos; i < row.size(); i++) {
            Collections.swap(row, i, pos);
            generateRowOptionsAll(row, pos + 1, optionsList);
            Collections.swap(row, pos, i);
        }

        if (pos == row.size() - 1) {
            optionsList.add(new ArrayList<>(row));
        }
    }

    @Override
    public String toString() {
        return _index + ": " + java.util.Arrays.toString(_players.toArray()) + " - " + getValue();
    }
}
