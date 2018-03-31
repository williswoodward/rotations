import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

class LineupSingleSide42 {
    private BigDecimal _value;
    private List<Rotation42> _rotations;

    private List<Player> _players;
    private List<Player> _player_loop;
    private int _nextOut;
    private int _nextIn;

    LineupSingleSide42(List<Player> players) {
        _players = players;
        _player_loop = new ArrayList<>(players);
        _rotations = createRotations();
        _value = generateValue();
    }

    @SuppressWarnings("CopyConstructorMissesField")
    LineupSingleSide42(LineupSingleSide42 lineup) {
        _players = new ArrayList<>(lineup._players);
        _player_loop = new ArrayList<>(lineup._players);
        _rotations = createRotations();
        _value = generateValue();
    }

    List<Rotation42> getRotations() {
        return _rotations;
    }

    BigDecimal getValue() {
        return _value.setScale(3, RoundingMode.HALF_DOWN);
    }

    public BigDecimal generateValue() {
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

        if (starting.countFemales() < Constants.MIN_FEMALES) {
            return null;
        }

        List<Rotation42> rotations = new ArrayList<>();
        for (int i = 0; i < Constants.MAX_ROTATIONS; i++) {
            Rotation42 rotation = getSingleRotation(i);

            if (!rotation.isPlayablePositions()) return null;

            if (i > 0 && rotation.isEquivalentTo(starting)) break;

            rotations.add(rotation);
            maintainFemales(rotation);
        }

        return rotations;
    }

    private void maintainFemales(Rotation42 rotation) {
        if (rotation.countFemales() == Constants.MIN_FEMALES && _player_loop.get(_nextOut).isFemale()) {
            int next = _nextIn;
            while (!_player_loop.get(next % _player_loop.size()).isFemale()) {
                next++;
            }

            while (!_player_loop.get(_nextIn).isFemale()) {
                Collections.swap(_player_loop, next % _player_loop.size(), (next - 1 + _player_loop.size()) % _player_loop.size());
                next--;
            }
        }
    }

    private Rotation42 getSingleRotation(int i) {
        int from = i % _player_loop.size();
        int to = from + Constants.COURT_SIZE;
        int remainder = 0;
        if (to > _player_loop.size()) {
            remainder = to - _player_loop.size();
            to = _player_loop.size();
        }

        List<Player> rotation = new ArrayList<>(_player_loop.subList(from, to));
        _nextIn = to % _player_loop.size();
        if (remainder > 0) {
            rotation.addAll(_player_loop.subList(0, remainder));
            _nextIn = remainder;
        }

        _nextOut = from;
        return new Rotation42(rotation, i);
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
