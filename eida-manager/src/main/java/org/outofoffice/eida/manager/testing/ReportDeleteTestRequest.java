package org.outofoffice.eida.manager.testing;

import lombok.Data;
import org.outofoffice.common.testing.EidaSocketTestFacade;
import org.outofoffice.common.testing.TestRequest;

@Data
public class ReportDeleteTestRequest implements TestRequest {

    private final String address = "localhost:10325";
    private final String message = "report delete, {shardUrl} table {entityId}";

    public static void main(String[] args) {
        EidaSocketTestFacade.request(new ReportDeleteTestRequest());
    }
}
