package io.drogue.iot.rodney;

import java.util.List;

public interface CommandExecutor {
    void execute(List<Command> commands);
}
