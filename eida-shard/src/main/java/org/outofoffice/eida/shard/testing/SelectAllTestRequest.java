package org.outofoffice.eida.shard.testing;

import lombok.Data;
import org.outofoffice.common.testing.EidaSocketTestFacade;
import org.outofoffice.common.testing.TestRequest;

@Data
public class SelectAllTestRequest implements TestRequest {

    private final String address = "localhost:10325";
    private final String message = "select all, table";

    public static void main(String[] args) {
        EidaSocketTestFacade.request(new SelectAllTestRequest());
    }

}
