import com.google.api.services.sheets.v4.Sheets;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class Player {
    private final String _name;
    private final boolean _isFemale;
    private final Position[] _positions;

    private BigDecimal _srv;
    private BigDecimal _hit;
    private BigDecimal _set;
    private BigDecimal _rcv;
    private BigDecimal _blk;
    private BigDecimal _dig;
    private BigDecimal _pass;

    Player(String name, boolean isFemale, Position... positions) {
        _name = name;
        _isFemale = isFemale;
        _positions = positions;
    }

    String getName() {
        return _name;
    }

    Position[] getPositions() {
        return _positions;
    }

    boolean isFemale() {
        return _isFemale;
    }

    void initData(Sheets sheets) throws IOException {
        Main._import_str.append("\nImported player: ").append(toString()).append("\n");
        PlayerData data = new PlayerData(sheets, _name);

        if (_srv != null) Main._import_str.append("!Serve: ").append(getSrv()).append(" -> ").append(getSrvNormalized()).append("\n");
        else {
            withSrv(data.getSrv());
            Main._import_str.append("Serve: ").append(getSrv()).append(" -> ").append(getSrvNormalized()).append("\n");
        }

        if (_hit != null) Main._import_str.append("!Hit: ").append(getHit()).append(" -> ").append(getHitNormalized()).append("\n");
        else {
            withHit(data.getHit());
            Main._import_str.append("Hit: ").append(getHit()).append(" -> ").append(getHitNormalized()).append("\n");
        }

        if (_set != null) Main._import_str.append("!Set: ").append(getSet()).append(" -> ").append(getSetNormalized()).append("\n");
        else {
            withSet(data.getSet());
            Main._import_str.append("Set: ").append(getSet()).append(" -> ").append(getSetNormalized()).append("\n");
        }

        if (_rcv != null) Main._import_str.append("!Receive: ").append(getRcv()).append(" -> ").append(getRcvNormalized()).append("\n");
        else {
            withRcv(data.getRcv());
            Main._import_str.append("Receive: ").append(getRcv()).append(" -> ").append(getRcvNormalized()).append("\n");
        }
        
        if (_blk != null) Main._import_str.append("!Block: ").append(getBlk()).append(" -> ").append(getBlkNormalized()).append("\n");
        else {
            withBlk(data.getBlk());
            Main._import_str.append("Block: ").append(getBlk()).append(" -> ").append(getBlkNormalized()).append("\n");
        }

        if (_dig != null) Main._import_str.append("!Dig: ").append(getDig()).append(" -> ").append(getDigNormalized()).append("\n");
        else {
            withDig(data.getDig());
            Main._import_str.append("Dig: ").append(getDig()).append(" -> ").append(getDigNormalized()).append("\n");
        }

        if (_pass != null) Main._import_str.append("!Pass: ").append(getPass()).append(" -> ").append(getPassNormalized()).append("\n");
        else {
            withPass(data.getPass());
            Main._import_str.append("Pass: ").append(getPass()).append(" -> ").append(getPassNormalized()).append("\n");
        }
    }

    BigDecimal getSrv() {
        if (_srv != null) {
            return _srv.setScale(3, RoundingMode.HALF_UP);
        } else {
            return null;
        }
    }

    BigDecimal getSrvNormalized() {
        if (_srv != null) {
            return _srv.divide(Config.SRV_OPTIMAL, RoundingMode.HALF_UP).setScale(3, RoundingMode.HALF_UP);
        } else {
            return null;
        }
    }

    Player withSrv(BigDecimal srv) {
        _srv = srv;
        return this;
    }

    BigDecimal getHit() {
        if (_hit != null) {
            return _hit.setScale(3, RoundingMode.HALF_UP);
        } else {
            return null;
        }
    }

    BigDecimal getHitNormalized() {
        if (_hit != null) {
            return _hit.divide(Config.HIT_OPTIMAL, RoundingMode.HALF_UP).setScale(3, RoundingMode.HALF_UP);
        } else {
            return null;
        }
    }

    Player withHit(BigDecimal hit) {
        _hit = hit;
        return this;
    }

    BigDecimal getSet() {
        if (_set != null) {
            return _set.setScale(3, RoundingMode.HALF_UP);
        } else {
            return null;
        }
    }

    BigDecimal getSetNormalized() {
        if (_set != null) {
            return _set.divide(Config.SET_OPTIMAL, RoundingMode.HALF_UP).setScale(3, RoundingMode.HALF_UP);
        } else {
            return null;
        }
    }

    Player withSet(BigDecimal set) {
        _set = set;
        return this;
    }

    BigDecimal getRcv() {
        if (_rcv != null) {
            return _rcv.setScale(3, RoundingMode.HALF_UP);
        } else {
            return null;
        }
    }

    BigDecimal getRcvNormalized() {
        if (_rcv != null) {
            return _rcv.divide(Config.RCV_OPTIMAL, RoundingMode.HALF_UP).setScale(3, RoundingMode.HALF_UP);
        } else {
            return null;
        }
    }

    Player withRcv(BigDecimal rcv) {
        _rcv = rcv;
        return this;
    }

    BigDecimal getBlk() {
        if (_blk != null) {
            return _blk.setScale(3, RoundingMode.HALF_UP);
        } else {
            return null;
        }
    }

    BigDecimal getBlkNormalized() {
        if (_blk != null) {
            return _blk.divide(Config.BLK_OPTIMAL, RoundingMode.HALF_UP).setScale(3, RoundingMode.HALF_UP);
        } else {
            return null;
        }
    }

    Player withBlk(BigDecimal blk) {
        _blk = blk;
        return this;
    }

    BigDecimal getDig() {
        if (_dig != null) {
            return _dig.setScale(3, RoundingMode.HALF_UP);
        } else {
            return null;
        }
    }

    BigDecimal getDigNormalized() {
        if (_dig != null) {
            return _dig.divide(Config.DIG_OPTIMAL, RoundingMode.HALF_UP).setScale(3, RoundingMode.HALF_UP);
        } else {
            return null;
        }
    }

    Player withDig(BigDecimal dig) {
        _dig = dig;
        return this;
    }

    BigDecimal getPass() {
        if (_pass != null) {
            return _pass.setScale(3, RoundingMode.HALF_UP);
        } else {
            return null;
        }
    }

    BigDecimal getPassNormalized() {
        if (_pass != null) {
            return _pass.divide(Config.PASS_OPTIMAL, RoundingMode.HALF_UP).setScale(3, RoundingMode.HALF_UP);
        } else {
            return null;
        }
    }

    Player withPass(BigDecimal pass) {
        _pass = pass;
        return this;
    }

    BigDecimal getValue() {
        return _srv.add(_hit).add(_set).add(_rcv).add(_blk).add(_dig).add(_pass);
    }
    
    @Override
    public String toString() {
        StringBuilder str = new StringBuilder(_name + "(");
        for (int i = 0; i < _positions.length; i++) {
            str.append(_positions[i]);
            if (i < _positions.length - 1) {
                str.append("/");
            }
        }
        str.append(")");
        return str.toString();
    }
}
