package ua.nure.moleculis.models.enums;

public enum SortDirection {
    ASC("ASC"), DESC("DESC");
    private final String directionCode;

    private SortDirection(String direction) {
        this.directionCode = direction;
    }

    public String getDirectionCode() {
        return this.directionCode;
    }
}