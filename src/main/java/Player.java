import com.google.api.services.sheets.v4.Sheets;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class Player {
    private String _name;
    private boolean _isFemale;
    private Position[] _positions;

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
        DataColumn data = new DataColumn(sheets, _name);
        
        _srv = data.getSrv();
        System.out.print("Serve: " + _srv + " -> ");
        _srv = _srv.divide(Constants.SRV_OPTIMAL, RoundingMode.HALF_DOWN).setScale(3, RoundingMode.HALF_DOWN);
        System.out.println(_srv);

        _hit = data.getHit();
        System.out.print("Hit: " + _hit + " -> ");
        _hit = _hit.divide(Constants.HIT_OPTIMAL, RoundingMode.HALF_DOWN).setScale(3, RoundingMode.HALF_DOWN);
        System.out.println(_hit);

        _set = data.getSet();
        System.out.print("Set: " + _set + " -> ");
        _set = _set.divide(Constants.SET_OPTIMAL, RoundingMode.HALF_DOWN).setScale(3, RoundingMode.HALF_DOWN);
        System.out.println(_set);

        _rcv = data.getRcv();
        System.out.print("Receive: " + _rcv + " -> ");
        _rcv = _rcv.divide(Constants.RCV_OPTIMAL, RoundingMode.HALF_DOWN).setScale(3, RoundingMode.HALF_DOWN);
        System.out.println(_rcv);

        _blk = data.getBlk();
        System.out.print("Block: " + _blk + " -> ");
        _blk = _blk.divide(Constants.BLK_OPTIMAL, RoundingMode.HALF_DOWN).setScale(3, RoundingMode.HALF_DOWN);
        System.out.println(_blk);

        _dig = data.getDig();
        System.out.print("Dig: " + _dig + " -> ");
        _dig = _dig.divide(Constants.DIG_OPTIMAL, RoundingMode.HALF_DOWN).setScale(3, RoundingMode.HALF_DOWN);
        System.out.println(_dig);

        _pass = data.getPass();
        System.out.print("Pass: " + _pass + " -> ");
        _pass = _pass.divide(Constants.PASS_OPTIMAL, RoundingMode.HALF_DOWN).setScale(3, RoundingMode.HALF_DOWN);
        System.out.println(_pass + "\n");
    }

    public BigDecimal getSrv() {
        return _srv;
    }

    public BigDecimal getHit() {
        return _hit;
    }

    public BigDecimal getSet() {
        return _set;
    }

    public BigDecimal getRcv() {
        return _rcv;
    }

    public BigDecimal getBlk() {
        return _blk;
    }

    public BigDecimal getDig() {
        return _dig;
    }

    public BigDecimal getPass() {
        return _pass;
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
