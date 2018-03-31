import com.google.api.services.sheets.v4.Sheets;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

class DataColumn {
    private List<List<Object>> _values;

    DataColumn(Sheets sheets, String playerName) throws IOException {
        List<List<Object>> values = sheets.spreadsheets().values()
                .get(Constants.SPREADSHEET_ID, playerName + "!" + Constants.DATA_RANGE)
                .execute()
                .getValues();
        _values = values;
    }

    BigDecimal getSrv() {
        return getStat(_values.get(Constants.DATA_COLUMN_SRV_EFF - Constants.DATA_COLUMN_OFFSET).get(0),
                _values.get(Constants.DATA_COLUMN_SRV_TOT - Constants.DATA_COLUMN_OFFSET).get(0),
                Constants.SRV_BASELINE);
    }

    BigDecimal getHit() {
        return getStat(_values.get(Constants.DATA_COLUMN_HIT_EFF - Constants.DATA_COLUMN_OFFSET).get(0),
                _values.get(Constants.DATA_COLUMN_HIT_TOT - Constants.DATA_COLUMN_OFFSET).get(0),
                Constants.HIT_BASELINE);
    }

    BigDecimal getSet() {
        return getStat(_values.get(Constants.DATA_COLUMN_SET_EFF - Constants.DATA_COLUMN_OFFSET).get(0),
                _values.get(Constants.DATA_COLUMN_SET_TOT - Constants.DATA_COLUMN_OFFSET).get(0),
                Constants.SET_BASELINE);
    }

    BigDecimal getRcv() {
        return getStat(_values.get(Constants.DATA_COLUMN_RCV_EFF - Constants.DATA_COLUMN_OFFSET).get(0),
                _values.get(Constants.DATA_COLUMN_RCV_TOT - Constants.DATA_COLUMN_OFFSET).get(0),
                Constants.RCV_BASELINE);
    }

    BigDecimal getBlk() {
        return getStat(_values.get(Constants.DATA_COLUMN_BLK_EFF - Constants.DATA_COLUMN_OFFSET).get(0),
                _values.get(Constants.DATA_COLUMN_BLK_TOT - Constants.DATA_COLUMN_OFFSET).get(0),
                Constants.BLK_BASELINE);
    }

    BigDecimal getDig() {
        return getStat(_values.get(Constants.DATA_COLUMN_DIG_EFF - Constants.DATA_COLUMN_OFFSET).get(0),
                _values.get(Constants.DATA_COLUMN_DIG_TOT - Constants.DATA_COLUMN_OFFSET).get(0),
                Constants.DIG_BASELINE);
    }

    BigDecimal getPass() {
        return getStat(_values.get(Constants.DATA_COLUMN_PASS_EFF - Constants.DATA_COLUMN_OFFSET).get(0),
                _values.get(Constants.DATA_COLUMN_PASS_TOT - Constants.DATA_COLUMN_OFFSET).get(0),
                Constants.PASS_BASELINE);
    }

    private BigDecimal getStat(Object pctObj, Object totObj, BigDecimal baseline) {
        int total;
        if (totObj == null) {
            return baseline.setScale(3, RoundingMode.HALF_DOWN);
        } else {
            total = Integer.parseInt(totObj.toString());
        }

        if (total < Constants.DATA_MIN) {
            System.out.print("*");
            return baseline.setScale(3, RoundingMode.HALF_DOWN);
        } else {
            return new BigDecimal(pctObj.toString());
        }
    }
}
