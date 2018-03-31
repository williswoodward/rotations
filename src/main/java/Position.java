public enum Position {
    MIDDLE,
    OUTSIDE,
    SETTER;

    @Override
    public String toString() {
        switch (this) {
            case MIDDLE: return "M";
            case OUTSIDE: return "O";
            case SETTER: return "S";
            default: return "";
        }
    }
}

