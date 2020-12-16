package io.drogue.iot.rodney;

public interface Service {

    void execute(byte[] payload) throws Exception;

}
