import com.google.common.primitives.Ints;
import javafx.util.Pair;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

class Lineup42 {
    private BigDecimal _value;
    private List<Rotation42> _rotations;

    private final List<Player> _players;
    private List<Player> _playerBuffer;
    private int[] _nextOut = new int[Config.NUM_ROTATION_SIDES];
    private int[] _nextIn = new int[Config.NUM_ROTATION_SIDES];

    Lineup42(List<Player> players) {
        _players = players;
        _playerBuffer = new ArrayList<>(players);
        _rotations = createRotations();
        _value = generateValue();
    }

    @SuppressWarnings("CopyConstructorMissesField")
    Lineup42(Lineup42 lineup) {
        _players = new ArrayList<>(lineup._players);
        _playerBuffer = new ArrayList<>(lineup._players);
        _rotations = createRotations();
        _value = generateValue();
    }

    BigDecimal getValue() {
        return _value.setScale(3, RoundingMode.HALF_UP);
    }

    private int countFemales() {
        return (int) _playerBuffer.stream().filter(Player::isFemale).count();
    }

    private BigDecimal generateValue() {
        BigDecimal sum = BigDecimal.ZERO;
        if (_rotations != null) {
            for (Rotation42 rotation : _rotations) {
                sum = sum.add(rotation.getValue());
            }

            return sum.divide(new BigDecimal(_rotations.size()), RoundingMode.HALF_UP);
        }

        return BigDecimal.ZERO;
    }

    private List<Rotation42> createRotations() {
        final Rotation42 starting = getOneRotation(0);

        int starting_females = starting.countFemales();
        if (starting_females < countFemales() && starting_females < (Config.COURT_SIZE - Config.MAX_MALES)) {
            return null;
        }

        List<Rotation42> rotations = new ArrayList<>();
        for (int i = 0; i < Config.MAX_ROTATIONS; i++) {
            Rotation42 rotation = getOneRotation(i);

            if (!rotation.isPlayablePositions()) return null;

            if (i > 0 && rotation.isEquivalentTo(starting)) break;

            rotations.add(rotation);
            enforceMaxMales(rotation);
        }

        return rotations;
    }

    private Rotation42 getOneRotation(int index) {
        int max_rotation_size = (int) (Config.MAX_MALES + countFemales());
        if (max_rotation_size > Config.COURT_SIZE) {
            max_rotation_size = Config.COURT_SIZE;
        }
        final int full_rotation_size = _playerBuffer.size() < max_rotation_size ? _playerBuffer.size() : max_rotation_size;
        final int sub_rotation_size = full_rotation_size/Config.NUM_ROTATION_SIDES;

        final int[] startPositions = getRotationStartPositions(full_rotation_size, sub_rotation_size);

        List<Player> rotationPlayers = new ArrayList<>();
        for (int i = 0; i < Config.NUM_ROTATION_SIDES; i++) {
            final int from = wrap(index + startPositions[i]);
            int to = from + sub_rotation_size;
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

        return new Rotation42(rotationPlayers, index);
    }

    private int[] getRotationStartPositions(int rotationSize, int sub_rotation_size) {
        int[] starts = new int[Config.NUM_ROTATION_SIDES];
        final int num_players_off_per_side = (_playerBuffer.size() - rotationSize) / Config.NUM_ROTATION_SIDES;

        if (num_players_off_per_side < 0) {
            for (int i = 0; i < starts.length; i++) {
                starts[i] = 0;
            }

            return starts;
        }

        int num_players_off_remainder = (_playerBuffer.size() - rotationSize) % Config.NUM_ROTATION_SIDES;
        int pos = 0;

        for (int i = 0; i < starts.length; i++) {
            starts[i] = pos;

            pos += sub_rotation_size + num_players_off_per_side;
            if (Config.MULTIPLE_ROTATION_SHIFT_LEFT) {
                if (num_players_off_remainder > 0) {
                    pos++;
                    num_players_off_remainder--;
                }
            } else if (i >= (starts.length - num_players_off_remainder)) {
                pos++;
            }
        }

        return starts;
    }

    private void enforceMaxMales(Rotation42 rotation) {
        if (Config.MINIMIZE_SWAPPING) {
            final int female_divisor = Config.COURT_SIZE / (Config.COURT_SIZE - Config.MAX_MALES);
            if (countFemales() * female_divisor >= _playerBuffer.size()) {
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
        StringBuilder str = new StringBuilder(Arrays.toString(_players.toArray()) + " - " + getValue());
        if (_rotations == null) return str.toString();
        for (Rotation42 rotation : _rotations) {
            str.append("\n").append(rotation);
        }

        return str.toString();
    }


}
