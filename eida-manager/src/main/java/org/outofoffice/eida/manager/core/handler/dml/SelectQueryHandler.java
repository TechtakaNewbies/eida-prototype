package org.outofoffice.eida.manager.core.handler.dml;

import org.outofoffice.eida.manager.core.handler.QueryHandler;

public class SelectQueryHandler implements QueryHandler {
    @Override
    public String handle(String parameter) {
        String[] params = parameter.split(" ");
        String tableName = params[0];
        String id = params[1];

        return "table result";
    }
}
