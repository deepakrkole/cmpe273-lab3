package edu.sjsu.cmpe.cache.client;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.SortedMap;
import java.util.TreeMap;

import com.google.common.base.Charsets;
import com.google.common.hash.Funnel;
import com.google.common.hash.Funnels;
import com.google.common.hash.HashCode;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;

import java.security.MessageDigest;
@SuppressWarnings("unused")
public class Client {
	
	 private static final Funnel<CharSequence> funnel = Funnels.stringFunnel(Charset.defaultCharset());
	// MessageDigest message=MessageDigest.getInstance("SHA1");
	 
	 private final static  HashFunction getHash=Hashing.md5();
	 String[] nodes={"http://localhost:3000","http://localhost:3001", "http://localhost:3002" };

	 public static void main(String[] args) throws Exception {
		 System.out.println("Starting Cache Client...");
		 Client client =new Client();
		 client.consistenthashing();
		 //client.rhashing();

	 }

	 public void consistenthashing(){
		 SortedMap<Long,String> servers=new TreeMap<Long,String>();
		 for(int i=0;i<nodes.length;i++){
			 servers.put(Hashing.md5().hashString(nodes[i], Charsets.UTF_8).padToLong(), nodes[i]);
		 }
		 char ch='a';
		 Character[] obj=new Character[10];
		 for(int i=0;i<10;i++){
			 obj[i]=ch;
			 ch++;
		 }
		 
		 for(int i=0;i<obj.length;i++){	
	        	//Consistent Hashing
	   		 String node=getNode(Hashing.md5()
	   				 .hashString(obj[i].toString(), Charsets.UTF_8).padToLong(),servers);
	   		
			 
			 CacheServiceInterface cache = new DistributedCacheService(
	        			node);

	        	cache.put(i+1, obj[i].toString());
	        	System.out.print("{");
	        	System.out.print("put("+(i+1)+")} => " +obj[i]+" ");
	        	
	        }
		 
		 System.out.println("");
		 
	        for(int i=0;i<obj.length;i++){
	        	//Consistent Hashing
	        	String node=getNode(Hashing.md5().hashString(obj[i].toString(), Charsets.UTF_8).padToLong(),servers);
	        	CacheServiceInterface cache = new DistributedCacheService(
	        			node);
	        	String value=cache.get(i+1);
	        	System.out.print("{");
	        	System.out.print("get("+(i+1)+")} => "+value+" ");
	        	
	        }
	        System.out.println("");
	        System.out.println("Existing Cache Client...");
	 }

    public static String getNode(Long hashFunction,SortedMap<Long,String>servers){
		if(!servers.containsKey(hashFunction)){
			SortedMap<Long, String> tailMap =servers.tailMap(hashFunction);
			
			hashFunction=tailMap.isEmpty() ? servers.firstKey() : tailMap.firstKey();		
		}
		return servers.get(hashFunction);
	}


    public void rhashing(){
		 SortedMap<Long,String> servers=new TreeMap<Long,String>();
		 for(int i=0;i<nodes.length;i++){
			 servers.put(Hashing.md5().hashString(nodes[i], Charsets.UTF_8).padToLong(), nodes[i]);
		 }
		 char ch='a';
		 Character[] obj=new Character[10];
		 for(int i=0;i<10;i++){
			 obj[i]=ch;
			 ch++;
		 }
		    for(int i=0;i<obj.length;i++){	
	        String node=performrendHash(obj[i].toString());
	        	CacheServiceInterface cache = new DistributedCacheService(
	        			node);

	        	cache.put(i+1, obj[i].toString());
	        	System.out.print("{");
	        	System.out.print("put("+(i+1)+")} => " +obj[i]+" ");
	        	
	        }
		    System.out.println("");
	        for(int i=0;i<obj.length;i++){
	        
	         String node=performrendHash(obj[i].toString());
	        	CacheServiceInterface cache = new DistributedCacheService(
	        			node);
	        	String value=cache.get(i+1);
	        	System.out.print("{");
	        	System.out.print("get("+(i+1)+")} => "+value+" ");
	        	
	        }
	        System.out.println("");
	        System.out.println("Existing Cache Client...");
	 }
    
    
    public String performrendHash(String key) {
    	long maxValue = Long.MIN_VALUE;
    	String max = null;
    	for (String node : nodes) {
    		long nodesHash = getHash.newHasher()
    				.putObject(key, funnel)
    				.putObject(node, funnel)
    				.hash().asLong();
    		if (nodesHash > maxValue) {
    			max = node;
    			maxValue = nodesHash;
    		}
    	}
    	return max;
    }


}

