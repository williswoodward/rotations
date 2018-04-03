public class PlayerAssignment {
    private Player _player;
    private Position _position;

    PlayerAssignment(Player player, Position position) {
        _player = player;
        _position = position;
    }

    Player getPlayer() {
        return _player;
    }

    Position getPosition() {
        return _position;
    }

    @Override
    public String toString() {
        return _player.getName() + "(" + _position + ")";
    }
}
