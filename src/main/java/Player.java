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

    Position[] getPositions() {
        return _positions;
    }

    boolean isFemale() {
        return _isFemale;
    }

    void initData(Sheets sheets) throws IOException {
        System.out.printf("Importing player: %s...\n", _name);
        PlayerData data = new PlayerData(sheets, _name);

        if (_srv == null) {
            BigDecimal srv = data.getSrv();
            System.out.print("Serve: " + srv + " -> ");
            withSrv(srv);
            System.out.println(_srv);
        } else {
            System.out.println("!Serve: " + _srv);
        }

        if (_hit == null) {
            BigDecimal hit = data.getHit();
            System.out.print("Hit: " + hit + " -> ");
            withHit(hit);
            System.out.println(_hit);
        } else {
            System.out.println("!Hit: " + _hit);
        }

        if (_set == null) {
            BigDecimal set = data.getSet();
            System.out.print("Set: " + set + " -> ");
            withSet(set);
            System.out.println(_set);
        } else {
            System.out.println("!Set: " + _set);
        }

        if (_rcv == null) {
            BigDecimal rcv = data.getRcv();
            System.out.print("Receive: " + rcv + " -> ");
            withRcv(rcv);
            System.out.println(_rcv);
        } else {
            System.out.println("!Receive: " + _rcv);
        }

        if (_blk == null) {
            BigDecimal blk = data.getBlk();
            System.out.print("Block: " + blk + " -> ");
            withBlk(blk);
            System.out.println(_blk);
        } else {
            System.out.println("!Block: " + _blk);
        }

        if (_dig == null) {
            BigDecimal dig = data.getDig();
            System.out.print("Dig: " + dig + " -> ");
            withDig(dig);
            System.out.println(_dig);
        } else {
            System.out.println("!Dig: " + _dig);
        }

        if (_pass == null) {
            BigDecimal pass = data.getPass();
            System.out.print("Pass: " + pass + " -> ");
            withPass(pass);
            System.out.println(_pass + "\n");
        } else {
            System.out.println("!Pass: " + _pass);
        }
    }

    BigDecimal getSrv() {
        return _srv;
    }

    Player withSrv(BigDecimal rawSrv) {
        _srv = rawSrv.divide(Config.SRV_OPTIMAL, RoundingMode.HALF_UP).setScale(3, RoundingMode.HALF_UP);
        return this;
    }

    BigDecimal getHit() {
        return _hit;
    }

    Player withHit(BigDecimal rawHit) {
        _hit = rawHit.divide(Config.HIT_OPTIMAL, RoundingMode.HALF_UP).setScale(3, RoundingMode.HALF_UP);
        return this;
    }

    BigDecimal getSet() {
        return _set;
    }

    Player withSet(BigDecimal rawSet) {
        _set = rawSet.divide(Config.SET_OPTIMAL, RoundingMode.HALF_UP).setScale(3, RoundingMode.HALF_UP);
        return this;
    }

    BigDecimal getRcv() {
        return _rcv;
    }

    Player withRcv(BigDecimal rawRcv) {
        _rcv = rawRcv.divide(Config.RCV_OPTIMAL, RoundingMode.HALF_UP).setScale(3, RoundingMode.HALF_UP);
        return this;
    }

    BigDecimal getBlk() {
        return _blk;
    }

    Player withBlk(BigDecimal rawBlk) {
        _blk = rawBlk.divide(Config.BLK_OPTIMAL, RoundingMode.HALF_UP).setScale(3, RoundingMode.HALF_UP);
        return this;
    }

    BigDecimal getDig() {
        return _dig;
    }

    Player withDig(BigDecimal rawDig) {
        _dig = rawDig.divide(Config.DIG_OPTIMAL, RoundingMode.HALF_UP).setScale(3, RoundingMode.HALF_UP);
        return this;
    }

    BigDecimal getPass() {
        return _pass;
    }

    Player withPass(BigDecimal rawPass) {
        _pass = rawPass.divide(Config.PASS_OPTIMAL, RoundingMode.HALF_UP).setScale(3, RoundingMode.HALF_UP);
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
