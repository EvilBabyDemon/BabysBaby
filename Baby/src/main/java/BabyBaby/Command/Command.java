package BabyBaby.Command;

import java.util.List;

public interface Command {

    String getName();

    default List<String> getAliases() {
        return List.of();
    }
}