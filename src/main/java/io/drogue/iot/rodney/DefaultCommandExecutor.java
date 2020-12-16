package io.drogue.iot.rodney;

import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.enterprise.context.ApplicationScoped;

import org.eclipse.microprofile.config.inject.ConfigProperty;

@ApplicationScoped
public class DefaultCommandExecutor implements CommandExecutor {

    @ConfigProperty(name = "commandExecutor.timeout", defaultValue = "10")
    int timeout;

    @Override
    public void execute(final List<Command> commands) throws Exception {
        for (Command command : commands) {
            executeCommand(command);
        }
    }

    void executeCommand(final Command command) throws Exception {
        var pb = new ProcessBuilder()
                .command(command.command)
                .redirectOutput(ProcessBuilder.Redirect.INHERIT)
                .redirectError(ProcessBuilder.Redirect.INHERIT)
                .redirectInput(ProcessBuilder.Redirect.INHERIT);
        var p = pb.start();
        p.waitFor(timeout, TimeUnit.SECONDS);
    }

}
