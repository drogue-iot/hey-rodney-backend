package io.drogue.iot.rodney.rules;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.scada.utils.str.StringReplacer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import io.drogue.iot.rodney.Command;

@ApplicationScoped
public class FileBasedProvider implements RuleProvider {

    private static final Logger LOG = LoggerFactory.getLogger(FileBasedProvider.class);

    private static class Rules {
        public List<Rule> rules;
    }

    /**
     * A matching rule.
     *
     * <pre>{@code
     * rules:
     *   - matcher: "Hello (.*)"
     *     command:
     *       - echo
     *       - "Hello ${1}"
     * }</pre>
     */
    private static class Rule {
        public String matcher;
        public List<String> command;

        public List<Command> match(final String phrase) {

            // FIXME: need to pre-compile when loading
            var p = Pattern.compile(this.matcher);

            var m = p.matcher(phrase);
            if (!m.matches()) {
                return null;
            }

            var cmd = command.stream()
                    .map(entry -> StringReplacer.replace(entry, new ExtendedPropertiesReplacer(name -> {

                        try {
                            return m.group(name);
                        } catch (IllegalArgumentException ignored) {
                        }

                        try {
                            return m.group(Integer.parseInt(name, 10));
                        } catch (NumberFormatException ignored) {
                        }

                        return null;

                    }), StringReplacer.DEFAULT_PATTERN))
                    .collect(Collectors.toList());

            return List.of(Command.of(cmd));
        }
    }

    @ConfigProperty(name = "fileBasedProvider.file", defaultValue = "/etc/rules.yaml")
    String ruleFile;

    private Instant lastCheck;
    private FileTime lastTime;

    private List<Rule> rules;

    private void checkReload() {

        var now = Instant.now();
        if (this.lastCheck != null && Duration.between(this.lastCheck, now).toSeconds() < 10) {
            // don't re-check
            return;
        }

        var p = Path.of(this.ruleFile);

        try {
            if (!Files.exists(p)) {
                this.rules = null;
            } else if (needRefresh(p)) {
                this.rules = load(p);
            }
        } catch (final Exception e) {
            LOG.warn("Failed to load rules", e);
            this.rules = null;
        }

        // even if we failed, we do not re-check before we should again
        this.lastCheck = now;
    }

    private boolean needRefresh(final Path path) throws IOException {
        if (lastTime == null) {
            return true;
        }

        var time = Files.getLastModifiedTime(path);
        return lastTime.compareTo(time) < 0;
    }

    private List<Rule> load(final Path path) throws IOException {

        var yaml = new Yaml(new Constructor(Rules.class));

        this.lastTime = Files.getLastModifiedTime(path);
        try (InputStream input = Files.newInputStream(path)) {
            final Rules rules = yaml.load(input);
            return rules.rules;
        }

    }

    @Override
    public List<Command> check(final String phrase) {

        checkReload();

        if (this.rules == null) {
            return List.of();
        }

        for (final Rule rule : this.rules) {
            var c = rule.match(phrase);
            if (c != null) {
                return c;
            }
        }

        return List.of();

    }

}