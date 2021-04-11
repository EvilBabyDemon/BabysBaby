package BabyBaby.ColouredStrings;

public class ColouredStringDiff {
    String value;

    public ColouredStringDiff() {
        this.value = "";
    }

    public String build() {
        return "```diff" + value + "\n```";
    }

    public ColouredStringDiff addGreen(String value) {
        this.value += "\n+" + value;
        return this;
    }

    public ColouredStringDiff addGreen(String value, boolean symmetric) {
        if (!symmetric) {
            return addGreen(value);
        }
        this.value += "\n+" + value + "+";
        return this;
    }

    public ColouredStringDiff addRed(String value) {
        this.value += "\n-" + value;
        return this;
    }

    public ColouredStringDiff addRed(String value, boolean symmetric) {
        if (!symmetric) {
            return addRed(value);
        }
        this.value += "\n-" + value + "-";
        return this;
    }

    public ColouredStringDiff addGrayDashes(String value) {
        this.value += "\n---" + value;
        return this;
    }

    public ColouredStringDiff addGrayDashes(String value, boolean symmetric) {
        if (!symmetric) {
            return addGrayDashes(value);
        }
        this.value += "\n---" + value + "---";
        return this;
    }

    public ColouredStringDiff addGrayTimes(String value) {
        this.value += "\n*** " + value;
        return this;
    }

    public ColouredStringDiff addGrayTimes(String value, boolean symmetric) {
        if (!symmetric) {
            return addGrayTimes(value);
        }
        this.value += "\n*** " + value + " ***";
        return this;
    }

    public ColouredStringDiff addNormal(String value) {
        this.value += "\n" + value;
        return this;
    }
}   