package io.drogue.iot.rodney;

import java.util.List;

/**
 * A service that should execute commands.
 */
public interface CommandExecutor {
    void execute(List<Command> commands) throws Exception;
}
