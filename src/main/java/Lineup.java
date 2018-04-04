import com.google.common.collect.*;
import com.google.common.primitives.Ints;
import javafx.util.Pair;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

class Lineup {
    private BigDecimal _value;
    private List<Rotation> _rotations;

    private final List<Player> _players;
    private List<Player> _playerBuffer;
    private int[] _nextOut = new int[Config.NUM_ROTATION_SIDES];
    private int[] _nextIn = new int[Config.NUM_ROTATION_SIDES];

    private Multimap<Player, Position> _playerPositions = HashMultimap.create();
    private BigDecimal _multiPositionPenalty = BigDecimal.ZERO;

    Lineup(List<Player> players) {
        _players = players;
        _playerBuffer = new ArrayList<>(players);
        _rotations = createRotations();
        _value = calculateValue();
    }

    @SuppressWarnings("CopyConstructorMissesField")
    Lineup(Lineup lineup) {
        _players = new ArrayList<>(lineup._players);
        _playerBuffer = new ArrayList<>(lineup._players);
        _rotations = createRotations();
        _value = calculateValue();
    }

    BigDecimal getValue() {
        return _value.setScale(3, RoundingMode.HALF_UP);
    }

    public Multimap<Player, Position> getPlayerPositions() {
        return _playerPositions;
    }

    private int countFemales() {
        return (int) _playerBuffer.stream().filter(Player::isFemale).count();
    }

    private BigDecimal calculateValue() {
        BigDecimal sum = BigDecimal.ZERO;
        if (_rotations != null) {
            for (Rotation rotation : _rotations) {
                sum = sum.add(rotation.getValue());
            }

            return sum.divide(new BigDecimal(_rotations.size()), RoundingMode.HALF_UP);
        }

        return BigDecimal.ZERO;
    }

    private List<Rotation> createRotations() {
        final Rotation starting = getOneRotation(0);

        int startingFemales = starting.countFemales();
        if (startingFemales < countFemales() && startingFemales < (Config.COURT_SIZE - Config.MAX_MALES)) {
            return null;
        }

        List<Rotation> rotations = new ArrayList<>();
        for (int i = 0; i < Config.MAX_ROTATIONS; i++) {
            Rotation rotation = getOneRotation(i);

            if (!rotation.isPlayablePositions()) return null;

            rotations.add(rotation);
            enforceMaxMales(rotation);
        }

        return rotations;
    }

    private Rotation getOneRotation(int index) {
        int maxRotationSize = (int) (Config.MAX_MALES + countFemales());
        if (maxRotationSize > Config.COURT_SIZE) {
            maxRotationSize = Config.COURT_SIZE;
        }
        final int fullRotationSize = _playerBuffer.size() < maxRotationSize ? _playerBuffer.size() : maxRotationSize;
        final int subRotationSize = fullRotationSize/Config.NUM_ROTATION_SIDES;

        final int[] startPositions = getRotationStartPositions(fullRotationSize, subRotationSize);

        List<Player> rotationPlayers = new ArrayList<>();
        for (int i = 0; i < Config.NUM_ROTATION_SIDES; i++) {
            final int from = wrap(index + startPositions[i]);
            int to = from + subRotationSize;
            int remainder = 0;

            if (to > _playerBuffer.size()) {
                remainder = to - _playerBuffer.size();
                to = _playerBuffer.size();
            }

            _nextOut[i] = from;
            _nextIn[i] = wrap(to);

            rotationPlayers.addAll(_playerBuffer.subList(from, to));
            if (remainder > 0) {
                rotationPlayers.addAll(_playerBuffer.subList(0, remainder));
                _nextIn[i] = remainder;
            }
        }

        // TODO: Rotation switch goes here
        return new Rotation42(rotationPlayers, index, this);
    }

    private int[] getRotationStartPositions(int rotationSize, int sub_rotation_size) {
        int[] starts = new int[Config.NUM_ROTATION_SIDES];
        final int numPlayersOffPerSide = (_playerBuffer.size() - rotationSize) / Config.NUM_ROTATION_SIDES;

        if (numPlayersOffPerSide < 0) {
            for (int i = 0; i < starts.length; i++) {
                starts[i] = 0;
            }

            return starts;
        }

        int numPlayersOffRemainder = (_playerBuffer.size() - rotationSize) % Config.NUM_ROTATION_SIDES;
        int pos = 0;

        for (int i = 0; i < starts.length; i++) {
            starts[i] = pos;

            pos += sub_rotation_size + numPlayersOffPerSide;
            if (Config.MULTIPLE_ROTATION_SHIFT_LEFT) {
                if (numPlayersOffRemainder > 0) {
                    pos++;
                    numPlayersOffRemainder--;
                }
            } else if (i >= (starts.length - numPlayersOffRemainder)) {
                pos++;
            }
        }

        return starts;
    }

    private void enforceMaxMales(Rotation rotation) {
        if (Config.MINIMIZE_SWAPPING) {
            final int femaleDivisor = Config.COURT_SIZE / (Config.COURT_SIZE - Config.MAX_MALES);
            if (countFemales() * femaleDivisor >= _playerBuffer.size()) {
                return;
            }
        }

        int femalesLeaving = 0;
        int femalesArriving = 0;

        for (int nextOut : _nextOut) {
            if (_playerBuffer.get(nextOut).isFemale()) {
                femalesLeaving++;
            }
        }

        for (int nextIn : _nextIn) {
            if (_playerBuffer.get(nextIn).isFemale()) {
                femalesArriving++;
            }
        }

        int femalesLost = femalesLeaving - femalesArriving;
        int femalesNeeded = rotation.countMales() + femalesLost - Config.MAX_MALES;

        if (femalesNeeded > 0) {
            // Find next closest Female(s)
            ArrayList<Pair<Player, Integer>> femalesToAdvance = new ArrayList<>();
            for (int nextIn : _nextIn) {
                int next = nextIn;
                int steps = 0;
                while (!_playerBuffer.get(next).isFemale()) {
                    next = wrap(next + 1);
                    steps++;
                }
                femalesToAdvance.add(new Pair<>(_playerBuffer.get(next), steps));
            }

            // If there are multiple, sort by: proximity, value
            femalesToAdvance.sort((f1, f2) -> {
                int compareSteps = f1.getValue().compareTo(f2.getValue());

                if (compareSteps == 0) {
                    return f2.getKey().getValue().compareTo(f1.getKey().getValue());
                } else {
                    return compareSteps;
                }
            });

            // Move Female(s) up into on deck position
            for (int i = 0; i < femalesNeeded; i++) {
                int currentPos = _playerBuffer.indexOf(femalesToAdvance.get(i).getKey());
                while (!Ints.contains(_nextIn, currentPos)) {
                    Collections.swap(_playerBuffer, currentPos, wrap(currentPos - 1));
                    currentPos = wrap(currentPos - 1);
                }
            }
        }
    }

    private int wrap(int index) {
        return (index + _playerBuffer.size()) % _playerBuffer.size();
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder("[");
        for (int i = 0; i < _players.size(); i++) {
            Player player = _players.get(i);
            str.append(player.getName()).append("(");
            for (Position position : _playerPositions.get(player)) {
                if (position.isFrontRow()) {
                    str.append(position);
                    str.append("/");
                }
            }
            str.replace(str.length() - 1, str.length(), "");
            str.append(")");
            if (i < (_players.size() - 1)) str.append(", ");
        }
        str.append("] - ").append(getValue());
        if (_rotations != null) {
            for (Rotation rotation : _rotations) {
                str.append("\n").append(rotation);
            }
        }

        return str.toString();
    }


}
