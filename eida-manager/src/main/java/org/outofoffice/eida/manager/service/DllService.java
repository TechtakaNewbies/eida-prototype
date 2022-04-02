package org.outofoffice.eida.manager.service;

import lombok.RequiredArgsConstructor;
import org.outofoffice.eida.manager.domain.ShardMapping;
import org.outofoffice.eida.manager.repository.ShardMappingRepository;
import org.outofoffice.eida.common.table.TableService;
import org.outofoffice.eida.common.exception.RowNotFoundException;
import org.outofoffice.eida.common.table.Table;
import org.outofoffice.eida.common.table.TableRepository;

import java.util.*;

import static java.util.stream.Collectors.toSet;


@RequiredArgsConstructor
public class DllService {

    private final TableService tableService;
    private final TableRepository tableRepository;

    private final ShardMappingService shardMappingService;
    private final ShardMappingRepository shardMappingRepository;
    
    private final Partitioner partitioner;


    public List<String> getAllShardUrls(String tableName) {
        Table table = tableRepository.findByName(tableName);
        Map<String, String> content = table.getContent();
        Set<String> shardIds = content.values().stream()
            .map(line -> line.split(",")[1])
            .collect(toSet());
        ShardMapping shardMapping = shardMappingRepository.find();
        return shardMapping.getShardUrls(shardIds);
    }

    public String getDestinationShardUrl(String tableName) {
        String shardId = partitioner.nextShardId(tableName);
        ShardMapping shardMapping = shardMappingRepository.find();
        return shardMapping.getShardUrl(shardId).orElseThrow();
    }

    public String getSourceShardUrl(String tableName, String entityId) {
        Table table = tableRepository.findByName(tableName);
        String row = table.getRow(entityId).orElseThrow(() -> new RowNotFoundException(tableName, entityId));
        String shardId = row.split(",")[1];
        ShardMapping shardMapping = shardMappingRepository.find();
        return shardMapping.getShardUrl(shardId).orElseThrow();
    }

    public void reportInsert(String shardUrl, String tableName, String id) {
        ShardMapping shardMapping = shardMappingRepository.find();
        String shardId = shardMapping.getShardId(shardUrl).orElseThrow();
        tableService.appendRow(tableName, id, shardId);
        partitioner.arrange(tableName);
    }

    public void reportDelete(String tableName, String id) {
        tableService.deleteRow(tableName, id);
    }

}
