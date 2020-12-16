package io.drogue.iot.rodney.rules;

import java.util.List;

import javax.validation.constraints.NotNull;

import io.drogue.iot.rodney.Command;

public interface RuleProvider {
    @NotNull List<Command> check(String phrase);
}
