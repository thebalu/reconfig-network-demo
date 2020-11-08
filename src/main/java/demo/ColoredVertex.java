package demo;

import java.util.Objects;

public class ColoredVertex {
    public String name;
    public boolean isRed;

    public ColoredVertex(String name, boolean isRed) {
        this.name = name;
        this.isRed = isRed;
    }

    public ColoredVertex(String name) {
        this.name = name;
        this.isRed = false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ColoredVertex that = (ColoredVertex) o;
        return name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return "ColoredVertex{" +
                "name='" + name + '\'' +
                ", isRed=" + isRed +
                '}';
    }
}
