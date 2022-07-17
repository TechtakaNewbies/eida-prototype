package org.outofoffice.eida.manager.service;

import lombok.RequiredArgsConstructor;
import org.outofoffice.eida.common.exception.RowNotFoundException;
import org.outofoffice.eida.common.table.Table;
import org.outofoffice.eida.common.table.TableService;
import org.outofoffice.eida.manager.domain.ShardMapping;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static java.util.stream.Collectors.toSet;


@RequiredArgsConstructor
public class DllService {

    private final TableService tableService;
    private final ShardMappingService shardMappingService;
    private final SchemeService schemeService;

    private final Partitioner partitioner;


    public Set<String> getAllShardUrls() {
        ShardMapping shardMapping = shardMappingService.find();
        return shardMapping.getAllShardUrls();
    }

    public String getShardUrlsAndScheme(String tableName) {
        Table table = tableService.findByName(tableName);
        Map<String, String> content = table.copyContent();
        Set<String> shardIds = content.values().stream()
            .map(line -> line.split(",")[1])
            .collect(toSet());
        ShardMapping shardMapping = shardMappingService.find();
        String shardUrls = String.join(",", shardMapping.getShardUrls(shardIds));
        String schemeString = schemeService.findByName(tableName);
        return shardUrls + "\n" + schemeString;
    }

    public String getDestination(String tableName) {
        String shardId = partitioner.nextShardId(tableName);
        ShardMapping shardMapping = shardMappingService.find();
        return shardMapping.getShardUrl(shardId).orElseThrow();
    }

    public String getSource(String tableName, String entityId) {
        Table table = tableService.findByName(tableName);
        ShardMapping shardMapping = shardMappingService.find();

        Optional<String> oRow = table.getRow(entityId);
        Optional<String> oShardId = oRow.map(s -> s.split(",")[1]);
        String shardUrl = oShardId.map(i -> shardMapping.getShardUrl(i).orElseThrow()).orElse("");
        String schemeString = schemeService.findByName(tableName);
        return shardUrl + "\n" + schemeString;
    }

    public void reportInsert(String shardUrl, String tableName, String id) {
        ShardMapping shardMapping = shardMappingService.find();
        String shardId = shardMapping.getShardId(shardUrl).orElseThrow();
        tableService.appendRow(tableName, id, shardId);
        partitioner.arrange(tableName);
    }

    public void reportDelete(String tableName, String id) {
        Table table = tableService.findByName(tableName);
        if (table.getRow(id).isEmpty()) throw new RowNotFoundException(tableName, id);
        tableService.deleteRow(tableName, id);
    }

}
