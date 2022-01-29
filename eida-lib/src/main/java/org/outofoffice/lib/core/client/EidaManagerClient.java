package org.outofoffice.lib.core.client;

import lombok.RequiredArgsConstructor;
import org.outofoffice.lib.core.query.EidaDllGenerator;
import org.outofoffice.lib.core.socket.EidaSocketClient;

import java.util.Arrays;
import java.util.List;


@RequiredArgsConstructor
public class EidaManagerClient implements EidaDllClient, EidaDdlManagerClient {

    private final EidaDllGenerator dllGenerator;
    private final EidaSocketClient eidaClient;

    private final String managerServerUrl;


    @Override
    public List<String> getAllShardUrls(String tableName) {
        String dll = dllGenerator.createGetAllShardUrlsQuery(tableName);
        String response = eidaClient.request(managerServerUrl, dll);
        return Arrays.asList(response.split(","));
    }

    @Override
    public String getDestinationShardUrl(String tableName) {
        String dll = dllGenerator.createGetDestinationShardUrlQuery(tableName);
        return eidaClient.request(managerServerUrl, dll);
    }

    @Override
    public <ID> String getSourceShardUrl(String tableName, ID id) {
        String dll = dllGenerator.createGetSourceShardUrlQuery(tableName, id);
        return eidaClient.request(managerServerUrl, dll);
    }

    @Override
    public <ID> void postShardUrl(String shardUrl, String tableName, ID id) {
        String dll = dllGenerator.createReportInsertShardUrlQuery(shardUrl, tableName, id);
        eidaClient.request(managerServerUrl, dll);
    }

    @Override
    public <ID> void deleteShardUrl(String shardUrl, String tableName, ID id) {
        String dll = dllGenerator.createReportDeleteShardUrlQuery(shardUrl, tableName, id);
        eidaClient.request(managerServerUrl, dll);
    }

}
