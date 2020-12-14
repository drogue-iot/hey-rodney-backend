package io.drogue.iot.rodney;

import java.io.IOException;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.drogue.iot.rodney.model.Alternative;
import io.drogue.iot.rodney.model.Results;

@ApplicationScoped
public class ServiceImpl implements Service {

    private static final Logger LOG = LoggerFactory.getLogger(ServiceImpl.class);

    @Inject
    ObjectMapper mapper;

    @Inject
    CommandExecutor executor;

    List<Command> evaluate(final Results results) {

        if (results.resultIndex != 0) {
            LOG.debug("expecting result index zero, got {}", results.resultIndex);
            return List.of();
        }

        if (results.results == null || results.results.size() != 1) {
            LOG.debug("Expecting exactly one result entry, got {}", results.results == null ? "<null>" : Integer.toString(results.results.size()));
            return List.of();
        }

        var result = results.results.get(0);
        if (!result.fin) {
            LOG.debug("Result is not final, ignoring!");
            return List.of();
        }

        if (result.alternatives == null) {
            LOG.debug("No detected content");
            return List.of();
        }

        Alternative best = null;
        for (Alternative a : result.alternatives) {
            if (a.transcript == null || a.transcript.isBlank()) {
                // no content
                continue;
            }
            if (best != null && best.confidence > a.confidence) {
                // we already have a better candidate
                continue;
            }
            // take as best
            best = a;
        }

        if (best == null) {
            LOG.debug("No best candidate found");
            return List.of();
        }

        return evalRules(best.transcript);
    }

    protected List<Command> evalRules(final String transcript) {
        LOG.debug("Eval: '{}'", transcript);

        if (transcript.equalsIgnoreCase("tell me a joke")) {
            return List.of(Command.of("echo", "Very funny! Ha, ha!"));
        }

        return List.of();
    }

    @Override
    public void execute(final byte[] payload) throws IOException {
        process(parseAndEval(payload));
    }

    List<Command> parseAndEval(final byte[] payload) throws IOException {
        return evaluate(mapper.readValue(payload, Results.class));
    }

    void process(final List<Command> commands) {
        executor.execute(commands);
    }
}
