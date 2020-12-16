package io.drogue.iot.rodney;

import java.util.Arrays;
import java.util.List;
import java.util.StringJoiner;

public class Command {
    String[] command;

    public static Command of(String... command) {
        var result = new Command();
        result.command = command;
        return result;
    }

    public static Command of(List<String> command) {
        var result = new Command();
        result.command = command.toArray(String[]::new);
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Command command1 = (Command) o;
        return Arrays.equals(command, command1.command);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(command);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", Command.class.getSimpleName() + "[", "]")
                .add("command=" + Arrays.toString(command))
                .toString();
    }
}
