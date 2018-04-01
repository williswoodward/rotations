import com.google.api.services.sheets.v4.Sheets;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

class PlayerData {
    private final List<List<Object>> _sheetValues;

    PlayerData(Sheets sheets, String playerName) throws IOException {
        _sheetValues = sheets.spreadsheets().values()
                .get(Config.SPREADSHEET_ID, playerName + "!" + Config.DATA_RANGE)
                .execute()
                .getValues();
    }

    BigDecimal getSrv() {
        return getStat(_sheetValues.get(Config.DATA_COLUMN_SRV_EFF - Config.DATA_COLUMN_OFFSET).get(0),
                _sheetValues.get(Config.DATA_COLUMN_SRV_TOT - Config.DATA_COLUMN_OFFSET).get(0),
                Config.SRV_BASELINE);
    }

    BigDecimal getHit() {
        return getStat(_sheetValues.get(Config.DATA_COLUMN_HIT_EFF - Config.DATA_COLUMN_OFFSET).get(0),
                _sheetValues.get(Config.DATA_COLUMN_HIT_TOT - Config.DATA_COLUMN_OFFSET).get(0),
                Config.HIT_BASELINE);
    }

    BigDecimal getSet() {
        return getStat(_sheetValues.get(Config.DATA_COLUMN_SET_EFF - Config.DATA_COLUMN_OFFSET).get(0),
                _sheetValues.get(Config.DATA_COLUMN_SET_TOT - Config.DATA_COLUMN_OFFSET).get(0),
                Config.SET_BASELINE);
    }

    BigDecimal getRcv() {
        return getStat(_sheetValues.get(Config.DATA_COLUMN_RCV_EFF - Config.DATA_COLUMN_OFFSET).get(0),
                _sheetValues.get(Config.DATA_COLUMN_RCV_TOT - Config.DATA_COLUMN_OFFSET).get(0),
                Config.RCV_BASELINE);
    }

    BigDecimal getBlk() {
        return getStat(_sheetValues.get(Config.DATA_COLUMN_BLK_EFF - Config.DATA_COLUMN_OFFSET).get(0),
                _sheetValues.get(Config.DATA_COLUMN_BLK_TOT - Config.DATA_COLUMN_OFFSET).get(0),
                Config.BLK_BASELINE);
    }

    BigDecimal getDig() {
        return getStat(_sheetValues.get(Config.DATA_COLUMN_DIG_EFF - Config.DATA_COLUMN_OFFSET).get(0),
                _sheetValues.get(Config.DATA_COLUMN_DIG_TOT - Config.DATA_COLUMN_OFFSET).get(0),
                Config.DIG_BASELINE);
    }

    BigDecimal getPass() {
        return getStat(_sheetValues.get(Config.DATA_COLUMN_PASS_EFF - Config.DATA_COLUMN_OFFSET).get(0),
                _sheetValues.get(Config.DATA_COLUMN_PASS_TOT - Config.DATA_COLUMN_OFFSET).get(0),
                Config.PASS_BASELINE);
    }

    private BigDecimal getStat(Object pctObj, Object totObj, BigDecimal baseline) {
        int total;
        if (totObj == null) {
            return baseline.setScale(3, RoundingMode.HALF_UP);
        } else {
            total = Integer.parseInt(totObj.toString());
        }

        if (total < Config.DATA_MIN) {
            System.out.print("*");
            return baseline.setScale(3, RoundingMode.HALF_UP);
        } else {
            return new BigDecimal(pctObj.toString());
        }
    }
}
