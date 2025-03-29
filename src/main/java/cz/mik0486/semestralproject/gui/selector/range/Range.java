package cz.mik0486.semestralproject.gui.selector.range;

public record Range(int lower, int upper) {

    @Override
    public String toString() {
        return lower + " - " + upper;
    }
}
