import com.google.common.base.Preconditions;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.*;

public abstract class Rotation {

    private final List<Player> _players;
    final int _index;
    private BigDecimal _value;
    boolean _isPlayablePositions;

    Lineup _lineup;

    Set<List<PlayerAssignment>> _frontRowOptions = new HashSet<>();
    private List<PlayerAssignment> _bestFrontRow;
    Set<List<PlayerAssignment>> _backRowOptions = new HashSet<>();
    private List<PlayerAssignment> _bestBackRow;

    public Rotation(List<Player> players, int index, Lineup lineup) {
        Preconditions.checkArgument(players.size() <= Config.COURT_SIZE);

        _players = players;
        _index = index;
        _isPlayablePositions = false;
        _lineup = lineup;

        generateFrontRowOptions(getFrontRow());
        generateBackRowOptions(getBackRow(), 0);
        _value = findBestPositions();
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

    List<PlayerAssignment> getAssignments() {
        ArrayList<PlayerAssignment> assignments = new ArrayList<>();
        if (_bestFrontRow != null) assignments.addAll(_bestFrontRow);
        if (_bestBackRow != null) assignments.addAll(_bestBackRow);
        return assignments;
    }

    protected abstract void generateFrontRowOptions(List<Player> row);

    protected abstract void generateBackRowOptions(List<Player> row, int pos);

    protected abstract BigDecimal evaluateFrontRowOption(List<PlayerAssignment> option);

    protected abstract BigDecimal evaluateBackRowOption(List<PlayerAssignment> option);

    abstract boolean lackingHitters(List<PlayerAssignment> option);

    private List<Player> getFrontRow() {
        return _players.subList(0, 3);
    }

    List<Player> getBackRow() {
        return _players.subList(3, _players.size());
    }

    private BigDecimal findBestPositions() {
        BigDecimal value = BigDecimal.ZERO;

        value = value.add(findBestFrontRow());
        value = value.add(findBestBackRow());

        value = value.add(serveValue());

        // Falloff for less likely rotations
        if (_index > Config.EXPECTED_NUM_ROTATIONS) {
            BigDecimal penalty = new BigDecimal((10 + (Config.EXPECTED_NUM_ROTATIONS - (_index)) * 2)).setScale(3, RoundingMode.HALF_UP)
                    .divide(BigDecimal.TEN, RoundingMode.HALF_UP);
            if (penalty.compareTo(BigDecimal.ZERO) < 0) {
                penalty = BigDecimal.ZERO;
            }

            value = value.multiply(penalty);
        }

        return value;
    }

    private BigDecimal findBestFrontRow() {
        BigDecimal total = BigDecimal.ZERO;

        for (List<PlayerAssignment> option : _frontRowOptions) {
            BigDecimal rowValue = evaluateFrontRowOption(option);
            if (rowValue.compareTo(total) > 0 || _bestFrontRow == null) {
                total = rowValue;
                _bestFrontRow = option;
            }
        }

        if (_bestFrontRow != null) {
            for (PlayerAssignment assignment : _bestFrontRow) {
                _lineup.getPlayerPositions().put(assignment.getPlayer(), assignment.getPosition());
            }
        }

        return total;
    }

    private BigDecimal findBestBackRow() {
        BigDecimal total = BigDecimal.ZERO;

        for (List<PlayerAssignment> option : _backRowOptions) {
            BigDecimal rowValue = evaluateBackRowOption(option);
            if (rowValue.compareTo(total) > 0 || _bestBackRow == null) {
                total = rowValue;
                _bestBackRow = option;
            }
        }

        if (_bestBackRow != null) {
            for (PlayerAssignment assignment : _bestBackRow) {
                _lineup.getPlayerPositions().put(assignment.getPlayer(), assignment.getPosition());
            }
        }

        // Compound lack of serve receive
        if (lackingReceivers(getBackRow())) {
            total = total.multiply(Config.DEFENSE_COMPOUND_PENALTY);
        }

        return total;
    }

    private BigDecimal serveValue() {
        BigDecimal value = BigDecimal.ZERO;

        if (_bestBackRow != null) {
            for (PlayerAssignment assignment : _bestBackRow) {
                if (assignment.getPosition() == Position.BACKLEFT) {
                    value = value.add(assignment.getPlayer().getRcvNormalized().multiply(Config.WT_BACKLEFT_RCV));
                } else if (assignment.getPosition() == Position.BACKMID) {
                    value = value.add(assignment.getPlayer().getRcvNormalized().multiply(Config.WT_BACKMID_RCV));
                } else if (assignment.getPosition() == Position.BACKRIGHT) {
                    value = value.add(assignment.getPlayer().getRcvNormalized().multiply(Config.WT_BACKRIGHT_RCV));
                    value = value.add(assignment.getPlayer().getRcvNormalized().multiply(Config.WT_BACKRIGHT_SRV));
                }
            }
        }

        return value;
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

    void generateRowOptionsUniquePositions(List<Player> row, Set<List<PlayerAssignment>> optionsList, boolean setPlayable) {
        Preconditions.checkArgument(row.size() == 3);

        for (int i = 0; i < row.get(0).getPositions().length; i++) {
            for (int j = 0; j < row.get(1).getPositions().length; j++) {
                for (int k = 0; k < row.get(2).getPositions().length; k++) {
                    if ((row.get(0).getPositions()[i] != row.get(1).getPositions()[j]) &&
                            (row.get(0).getPositions()[i] != row.get(2).getPositions()[k]) &&
                            (row.get(1).getPositions()[j] != row.get(2).getPositions()[k])) {
                        List<PlayerAssignment> rowOption = new ArrayList<>();
                        rowOption.add(new PlayerAssignment(row.get(0), row.get(0).getPositions()[i]));
                        rowOption.add(new PlayerAssignment(row.get(1), row.get(1).getPositions()[j]));
                        rowOption.add(new PlayerAssignment(row.get(2), row.get(2).getPositions()[k]));
                        optionsList.add(rowOption);
                        if (setPlayable) {
                            _isPlayablePositions = true;
                        }
                    }
                }
            }
        }
    }

    @Override
    public String toString() {
        return _index + ": " + getAssignments() + " - " + getValue();
    }
}
