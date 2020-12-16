package io.drogue.iot.rodney.rules;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;

import io.drogue.iot.rodney.Command;

@ApplicationScoped
public class TellMeAJoke implements RuleProvider {

    @Override
    public List<Command> check(String phrase) {

        if (phrase.equalsIgnoreCase("tell me a joke")) {
            return List.of(Command.of("echo", "Very funny! Ha, ha!"));
        }

        return List.of();

    }
}
