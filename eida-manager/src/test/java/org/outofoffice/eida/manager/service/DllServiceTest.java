package org.outofoffice.eida.manager.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.outofoffice.eida.common.exception.RowNotFoundException;
import org.outofoffice.eida.manager.repository.*;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;


class DllServiceTest {

    DllService dllService;
    TableRepository tableRepository;
    MetadataRepository metadataRepository;
    Partitioner partitioner;

    @BeforeEach
    void setup() {
        tableRepository = new TableMapRepository();
        metadataRepository = new MetadataMapRepository();
        partitioner = new Partitioner(tableRepository, metadataRepository);
        dllService = new DllService(tableRepository, metadataRepository, partitioner);
    }


    @Test
    void getAllShardUrls() {
        metadataRepository.save("1", "localhost:10830");
        metadataRepository.save("2", "localhost:10831");
        metadataRepository.save("3", "localhost:10832");
        tableRepository.save("Team", "1"/* entityId */, "1"/* sharId */);
        tableRepository.save("Team", "2"/* entityId */, "2"/* sharId */);

        String tableName = "Team";
        List<String> shardUrls = dllService.getAllShardUrls(tableName);
        assertThat(shardUrls).isEqualTo(List.of("localhost:10830", "localhost:10831"));
    }

    @Test
    void getDestinationShardUrl() {
        String tableName = "Team";
        metadataRepository.save("s1", "localhost:10830");
        metadataRepository.save("s2", "localhost:10831");
        tableRepository.save(tableName, "e1", "s1");
        partitioner.init();

        String shardUrl = dllService.getDestinationShardUrl(tableName);
        assertThat(shardUrl).isEqualTo("localhost:10831");
    }

    @Test
    void getSourceShardUrl() {
        metadataRepository.save("3", "localhost:10830");
        tableRepository.save("Team", "1"/* entityId */, "3"/* sharId */);

        String tableName = "Team";
        String id = "1";
        String sourceShardUrl = dllService.getSourceShardUrl(tableName, id);

        assertThat(sourceShardUrl).isEqualTo("localhost:10830");
    }

    @Test
    void reportInsertShardUrl() {
        String shardUrl = "localhost:10830";
        String tableName = "Team";
        String id = "1";
        String shardId = "1";
        metadataRepository.save(shardId, shardUrl);
        tableRepository.save(tableName, id, shardId);
        partitioner.init();

        dllService.reportInsert(shardUrl, tableName, id);

        String sourceShardUrl = dllService.getSourceShardUrl(tableName, id);
        assertThat(sourceShardUrl).isEqualTo(shardUrl);
    }

    @Test
    void reportDeleteShardUrl() {
        String shardUrl = "localhost:10830";
        String tableName = "Team";
        String id = "1";
        String shardId = "1";
        metadataRepository.save(shardId, shardUrl);
        tableRepository.save(tableName, id, shardId);
        partitioner.init();
        dllService.reportInsert(shardUrl, tableName, id);

        dllService.reportDelete(tableName, id);

        Executable action = () -> dllService.getSourceShardUrl(tableName, id);
        assertThrows(RowNotFoundException.class, action);
    }

}
