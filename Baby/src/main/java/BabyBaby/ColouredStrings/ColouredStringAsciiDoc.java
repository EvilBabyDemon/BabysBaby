package BabyBaby.ColouredStrings;

public class ColouredStringAsciiDoc extends ColouredStringBase {
    String value;

    @Override
    public String build() {
        return "```asciidoc" + this.value + "\n```";
    }

    public ColouredStringAsciiDoc() {
        this.value = "";
    }

    public ColouredStringAsciiDoc addBlue(String value) {
        this.value += "\n= " + value + " =";
        return this;
    }

    public ColouredStringAsciiDoc addBlueAboveEq(String value) {
        this.value += "\n" + value + "\n" + "=".repeat(value.length());
        return this;
    }

    public ColouredStringAsciiDoc addBlueAboveEq(String value, int n) {
        this.value += "\n" + value + "\n" + "=".repeat(n);
        return this;
    }

    public ColouredStringAsciiDoc addBlueAboveDash(String value) {
        this.value += "\n" + value + "\n" + "-".repeat(value.length());
        return this;
    }

    public ColouredStringAsciiDoc addBlueAboveDash(String value, int n) {
        this.value += "\n" + value + "\n" + "-".repeat(n);
        return this;
    }

    public ColouredStringAsciiDoc addOrange(String value) {
        this.value += "\n[" + value + "]";
        return this;
    }

    public ColouredStringAsciiDoc addNormal(String value) {
        this.value += "\n" + value;
        return this;
    }

    public ColouredStringAsciiDoc addDiff(String value) {
        this.value += "\n``` ```diff\n" + value + "";
        return this;
    }
}

