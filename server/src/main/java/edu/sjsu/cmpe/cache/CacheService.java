package edu.sjsu.cmpe.cache;

import java.io.File;
import java.util.Calendar;
import java.util.concurrent.ConcurrentHashMap;

import net.openhft.chronicle.map.ChronicleMap;
import net.openhft.chronicle.map.ChronicleMapBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yammer.dropwizard.Service;
import com.yammer.dropwizard.config.Bootstrap;
import com.yammer.dropwizard.config.Environment;

import edu.sjsu.cmpe.cache.api.resources.CacheResource;
import edu.sjsu.cmpe.cache.config.CacheServiceConfiguration;
import edu.sjsu.cmpe.cache.domain.Entry;
import edu.sjsu.cmpe.cache.repository.CacheInterface;
import edu.sjsu.cmpe.cache.repository.ChronicleMapCache;
//import edu.sjsu.cmpe.cache.repository.InMemoryCache;

public class CacheService extends Service<CacheServiceConfiguration> {

    private final Logger log = LoggerFactory.getLogger(getClass());
    private static String filename=null;
    
    public static void main(String[] args) throws Exception {
    	filename=args[2];
    	new CacheService().run(args);
        
    }

    @Override
    public void initialize(Bootstrap<CacheServiceConfiguration> bootstrap) {
        bootstrap.setName("cache-server");
    }

    @Override
    public void run(CacheServiceConfiguration configuration,
            Environment environment) throws Exception {
        /** Cache APIs */
    	
    	Calendar calendar = Calendar.getInstance();
    	int hours = calendar.get(Calendar.HOUR_OF_DAY);
    	int minutes = calendar.get(Calendar.MINUTE);
    	int seconds = calendar.get(Calendar.SECOND);
    	
    	String path="/tmp/server_"+seconds+".txt";
    	File file=new File(path);
        ChronicleMap<Long, Entry> map =  ChronicleMapBuilder
        		.of(Long.class, Entry.class)
        		.createPersistedTo(file);
        CacheInterface cache = new ChronicleMapCache(map);
        environment.addResource(new CacheResource(cache));
        log.info("Loaded resources");

    }
    
    
}
