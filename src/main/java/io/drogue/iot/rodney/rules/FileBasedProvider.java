package io.drogue.iot.rodney.rules;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.regex.Matcher;
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

    public static class Rules {
        private List<Rule> rules;

        public void setRules(List<Rule> rules) {
            this.rules = rules;
        }

        public List<Rule> getRules() {
            return rules;
        }
    }

    /**
     * A matching rule.
     *
     * <pre>{@code
     * rules:
     *   - matcher: "Hello (.*)"
     *     commands:
     *       - execute:
     *         - echo
     *         - "Hello ${1}"
     * }</pre>
     */
    public static class Rule {
        private String matcher;
        private List<Execute> commands;

        public void setMatcher(final String matcher) {
            this.matcher = matcher;
        }

        public String getMatcher() {
            return matcher;
        }

        public void setCommands(final List<Execute> commands) {
            this.commands = commands;
        }

        public List<Execute> getCommands() {
            return commands;
        }

        public List<Command> match(final String phrase) {

            // FIXME: need to pre-compile when loading
            var p = Pattern.compile(this.matcher);

            var m = p.matcher(phrase);
            if (!m.matches()) {
                return null;
            }

            return commands.stream()
                    .map(entry -> entry.toCommand(m))
                    .collect(Collectors.toList());
        }
    }

    public static class Execute {
        private List<String> execute;

        public void setExecute(final List<String> execute) {
            this.execute = execute;
        }

        public List<String> getExecute() {
            return execute;
        }

        public Command toCommand(final Matcher matcher) {
            return Command.of(
                    this.execute.stream()
                            .map(entry ->
                                    StringReplacer.replace(entry, new ExtendedPropertiesReplacer(name -> {

                                        try {
                                            return matcher.group(name);
                                        } catch (IllegalArgumentException ignored) {
                                        }

                                        try {
                                            return matcher.group(Integer.parseInt(name, 10));
                                        } catch (NumberFormatException ignored) {
                                        }

                                        return null;

                                    }), StringReplacer.DEFAULT_PATTERN)
                            )
                            .collect(Collectors.toList()));
        }

    }

    @ConfigProperty(name = "fileBasedProvider.file", defaultValue = "/etc/config/rules.yaml")
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
