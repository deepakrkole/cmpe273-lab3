package edu.sjsu.cmpe.cache.repository;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import net.openhft.chronicle.map.ChronicleMap;
import edu.sjsu.cmpe.cache.domain.Entry;

public class ChronicleMapCache implements CacheInterface {
    /** In-memory map cache. (Key, Value) -> (Key, Entry) */
    private final ChronicleMap<Long, Entry> chronicleMap;

    public ChronicleMapCache(ChronicleMap<Long, Entry> entries) {
    	chronicleMap = entries;
    }

    @Override
    public Entry save(Entry newEntry) {
        checkNotNull(newEntry, "newEntry instance must not be null");
        chronicleMap.putIfAbsent(newEntry.getKey(), newEntry);

        return newEntry;
    }

    @Override
    public Entry get(Long key) {
        checkArgument(key > 0,
                "Key was %s but expected greater than zero value", key);
        return chronicleMap.get(key);
    }

    @Override
    public List<Entry> getAll() {
        return new ArrayList<Entry>(chronicleMap.values());
    }
}
