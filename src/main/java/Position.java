public enum Position {
    BACKLEFT,
    BACKMID,
    BACKRIGHT,
    MIDDLE,
    OUTSIDE,
    SETTER;

    @Override
    public String toString() {
        switch (this) {
            case BACKLEFT: return "BL";
            case BACKMID: return "BM";
            case BACKRIGHT: return "BR";

            case MIDDLE: return "M";
            case OUTSIDE: return "O";
            case SETTER: return "S";
            default: return "";
        }
    }

    boolean isFrontRow() {
        switch (this) {
            case MIDDLE:
            case OUTSIDE:
            case SETTER: return true;
            default: return false;
        }
    }
}

