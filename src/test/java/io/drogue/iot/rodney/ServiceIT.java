package io.drogue.iot.rodney;

import org.junit.jupiter.api.Disabled;

import io.quarkus.test.junit.NativeImageTest;

@NativeImageTest
@Disabled("JUnit @Inject is not supported in NativeImageTest tests. Offending...")
public class ServiceIT extends ServiceTest {
}
