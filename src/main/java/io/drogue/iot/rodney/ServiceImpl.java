package io.drogue.iot.rodney;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.drogue.iot.rodney.model.Alternative;
import io.drogue.iot.rodney.model.Results;
import io.drogue.iot.rodney.rules.RuleProvider;

@ApplicationScoped
public class ServiceImpl implements Service {

    private static final Logger LOG = LoggerFactory.getLogger(ServiceImpl.class);

    @Inject
    ObjectMapper mapper;

    @Inject
    CommandExecutor executor;

    @Inject
    @Any
    Instance<RuleProvider> ruleProviders;

    List<Command> evaluate(final Results results) {

        if (results.getResultIndex() != 0) {
            LOG.debug("expecting result index zero, got {}", results.getResultIndex());
            return List.of();
        }

        if (results.getResults() == null || results.getResults().size() != 1) {
            LOG.debug("Expecting exactly one result entry, got {}", results.getResults() == null ? "<null>" : Integer.toString(results.getResults().size()));
            return List.of();
        }

        var result = results.getResults().get(0);
        if (!result.isFinal()) {
            LOG.debug("Result is not final, ignoring!");
            return List.of();
        }

        if (result.getAlternatives() == null) {
            LOG.debug("No detected content");
            return List.of();
        }

        Alternative best = null;
        for (Alternative a : result.getAlternatives()) {
            if (a.getTranscript() == null || a.getTranscript().isBlank()) {
                // no content
                continue;
            }
            if (best != null && best.getConfidence() > a.getConfidence()) {
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

        return evalRules(best.getTranscript());
    }

    protected List<Command> evalRules(final String transcript) {
        LOG.debug("Eval: '{}'", transcript);

        return this.ruleProviders.stream()
                .flatMap(provider -> {
                    LOG.debug("Provider: {}", provider);
                    return provider.check(transcript).stream();
                })
                .collect(Collectors.toList());

    }

    @Override
    public void execute(final byte[] payload) throws Exception {
        process(parseAndEval(payload));
    }

    List<Command> parseAndEval(final byte[] payload) throws IOException {
        return evaluate(mapper.readValue(payload, Results.class));
    }

    void process(final List<Command> commands) throws Exception {
        executor.execute(commands);
    }
}
