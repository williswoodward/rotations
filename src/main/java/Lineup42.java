import com.google.common.primitives.Ints;
import javafx.util.Pair;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

class Lineup42 {
    private BigDecimal _value;
    private List<Rotation42> _rotations;

    private List<Player> _players;
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

    private BigDecimal generateValue() {
        BigDecimal sum = BigDecimal.ZERO;
        if (_rotations != null) {
            for (Rotation42 rotation : _rotations) {
                sum = sum.add(rotation.getValue());
            }
        }

        return sum;
    }

    private List<Rotation42> createRotations() {
        Rotation42 starting = getSingleRotation(0);

        if (starting.countMales() > Config.MAX_MALES) {
            return null;
        }

        List<Rotation42> rotations = new ArrayList<>();
        for (int i = 0; i < Config.MAX_ROTATIONS; i++) {
            Rotation42 rotation = getSingleRotation(i);

            if (!rotation.isPlayablePositions()) return null;

            if (i > 0 && rotation.isEquivalentTo(starting)) break;

            rotations.add(rotation);
            maxMales(rotation);
        }

        return rotations;
    }

    private Rotation42 getSingleRotation(int index) {
        int full_rotation_size = _playerBuffer.size() < Config.COURT_SIZE ? _playerBuffer.size() : Config.COURT_SIZE;
        int sub_rotation_size = full_rotation_size/Config.NUM_ROTATION_SIDES;

        int[] starts = getSubRotationStarts(full_rotation_size, sub_rotation_size);

        List<Player> rotationPlayers = new ArrayList<>();
        for (int i = 0; i < Config.NUM_ROTATION_SIDES; i++) {
            int from = (index + starts[i]) % _playerBuffer.size();
            int to = from + sub_rotation_size;
            int remainder = 0;

            if (to > _playerBuffer.size()) {
                remainder = to - _playerBuffer.size();
                to = _playerBuffer.size();
            }

            rotationPlayers.addAll(_playerBuffer.subList(from, to));
            _nextIn[i] = to % _playerBuffer.size();
            if (remainder > 0) {
                rotationPlayers.addAll(_playerBuffer.subList(0, remainder));
                _nextIn[i] = remainder;
            }

            _nextOut[i] = from;
        }

        return new Rotation42(rotationPlayers, index);
    }

    private int[] getSubRotationStarts(int rotationSize, int sub_rotation_size) {
        int[] starts = new int[Config.NUM_ROTATION_SIDES];

        int num_players_off_per_side = (_playerBuffer.size() - rotationSize) / Config.NUM_ROTATION_SIDES;

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

    private void maxMales(Rotation42 rotation) {
        int femalesLeaving = 0;
        int femalesArriving = 0;

        for (int nextOut : _nextOut) {
            if (!_playerBuffer.get(nextOut).isMale()) {
                femalesLeaving++;
            }
        }

        for (int nextIn : _nextIn) {
            if (!_playerBuffer.get(nextIn).isMale()) {
                femalesArriving++;
            }
        }

        int femalesLost = femalesLeaving - femalesArriving;
        long femalesNeeded = rotation.countMales() + femalesLost - Config.MAX_MALES;

        if (femalesNeeded > 0) {
            // Find next closest Female(s)
            ArrayList<Pair<Player, Integer>> femalesToAdvance = new ArrayList<>();
            for (int nextIn : _nextIn) {
                int next = nextIn;
                int steps = 0;
                while (_playerBuffer.get(next).isMale()) {
                    next = wrap(next + 1);
                    steps++;
                }
                femalesToAdvance.add(new Pair<>(_playerBuffer.get(next), steps));
            }

            femalesToAdvance.sort(new Comparator<Pair<Player, Integer>>() {
                @Override
                public int compare(Pair<Player, Integer> p1, Pair<Player, Integer> p2) {
                    int compareSteps = p1.getValue().compareTo(p2.getValue());

                    if (compareSteps == 0) {
                        return p2.getKey().getValue().compareTo(p1.getKey().getValue());
                    } else {
                        return compareSteps;
                    }
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
