package BabyBaby.Command;

import java.util.List;

public interface ICommand {

    String getName();

    default List<String> getAliases() {
        return List.of();
    }
}