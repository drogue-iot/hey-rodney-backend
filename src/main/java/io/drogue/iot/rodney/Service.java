package io.drogue.iot.rodney;

import java.io.IOException;

public interface Service {

    void execute(byte[] payload) throws IOException;

}
