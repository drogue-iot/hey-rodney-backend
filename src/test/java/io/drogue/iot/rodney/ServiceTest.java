package io.drogue.iot.rodney;

import static org.hamcrest.MatcherAssert.assertThat;

import java.io.IOException;

import javax.inject.Inject;

import org.hamcrest.core.IsIterableContaining;
import org.hamcrest.core.IsNull;
import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

@QuarkusTest
public class ServiceTest {

    @Inject
    ServiceImpl service;

    @Test
    public void testParse() throws IOException {

        var json = new JsonObject()
                .put("results", new JsonArray()
                        .add(new JsonObject()
                                .put("final", true)
                                .put("alternatives", new JsonArray()
                                        .add(new JsonObject()
                                                .put("confidence", 0.9)
                                                .put("transcript", "tell me a joke ")
                                        )
                                )
                        )
                )
                .put("result_index", 0);

        System.out.println(json.encodePrettily());

        var result = service.parseAndEval(json.toBuffer().getBytes());

        assertThat(result, IsNull.notNullValue());
        assertThat(result, IsIterableContaining.hasItem(Command.of("echo", "Very funny! Ha, ha!")));
    }

    private void testPhrase(final String phrase, final Command command) throws IOException {
        var json = new JsonObject()
                .put("results", new JsonArray()
                        .add(new JsonObject()
                                .put("final", true)
                                .put("alternatives", new JsonArray()
                                        .add(new JsonObject()
                                                .put("confidence", 0.9)
                                                .put("transcript", phrase)
                                        )
                                )
                        )
                )
                .put("result_index", 0);

        System.out.println(json.encodePrettily());

        var result = service.parseAndEval(json.toBuffer().getBytes());

        assertThat(result, IsNull.notNullValue());
        assertThat(result, IsIterableContaining.hasItem(command));
    }

    @Test
    public void testFile1() throws IOException {
        testPhrase("Hello Rodney", Command.of("echo", "hi Rodney!"));
    }

}
