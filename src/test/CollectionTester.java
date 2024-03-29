package test;

import static org.junit.jupiter.api.Assertions.*;

import java.io.FileNotFoundException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterAll;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import static java.lang.Math.toIntExact;

import hw5.DB;
import hw5.DBCollection;
import hw5.DBCursor;

class CollectionTester {
	
	@AfterAll
	public static void afterTests() {
		DB db = new DB("data");
		DBCollection test = db.getCollection("test3");
		test.drop();
		
		JsonObject json = new JsonObject();
		json.add("key", new JsonPrimitive("value"));
		test.insert(json);
		
		// used for testUpdate and testRemove
		test = db.getCollection("test4");
		test.drop();
		test.insert(json);
		
		// used for testUpdateMulti
		test = db.getCollection("test5");
		test.drop();
		test.insert(json, json);
		
		// used for testRemoveMulti
		test = db.getCollection("test6");
		test.drop();
		test.insert(json, json);
	}

	/*
	 * Things to be tested:
	 * 
	 * Document access (done?)
	 * Document insert/update/delete
	 */
	@Test
	public void testGetDocument() {
		DB db = new DB("data");
		DBCollection test = db.getCollection("test");
		JsonObject primitive = test.getDocument(0);			
		assertTrue(primitive.getAsJsonPrimitive("key").getAsString().equals("value"));
	}
	
	@Test
	public void testGetEmbedded() {
		DB db = new DB("data");
		DBCollection test = db.getCollection("test");
		JsonObject embedded = test.getDocument(1);
		assertTrue(embedded.getAsJsonObject("embedded").toString().equals("{\"key2\":\"value2\"}"));
	}
	
	@Test
	public void testGetArray() {
		DB db = new DB("data");
		DBCollection test = db.getCollection("test");
		JsonObject array = test.getDocument(2);
		assertTrue(array.getAsJsonArray("array").get(0).getAsString().equals("one"));
		assertTrue(array.getAsJsonArray("array").get(1).getAsString().equals("two"));
		assertTrue(array.getAsJsonArray("array").get(2).getAsString().equals("three"));

	}
	
	@Test
	public void testCount() {
		DB db = new DB("data");
		DBCollection test = db.getCollection("test");
		assertTrue(test.count() == 3);
	}
	
	@Test
	public void testDrop() {
		DB db = new DB("data");
		DBCollection test = db.getCollection("test2");
		
		JsonObject json = new JsonObject();
		json.add("key", new JsonPrimitive("value"));
		test.insert(json);
		// > 0 because the number of documents in this test file doesn't matter
		assertTrue(test.count() > 0);
		test.drop();
		assertTrue(test.count() == 0);
	}
	
	@Test
	public void testInsert() {
		DB db = new DB("data");
		DBCollection test = db.getCollection("test2");
		
		JsonObject json = new JsonObject();
		json.add("key", new JsonPrimitive("value"));
		test.insert(json);
		// alternatively, we could use test.drop at the end instead of type casting here
		JsonObject primitive = test.getDocument(toIntExact(test.count()-1));
		assertTrue(primitive.getAsJsonPrimitive("key").getAsString().equals("value"));
	}
	
	@Test
	public void testUpdate() {
		DB db = new DB("data");
		DBCollection test = db.getCollection("test3");
		
		JsonObject query = new JsonObject();
		JsonObject update = new JsonObject();
		query.add("key", new JsonPrimitive("value"));
		int newVal = (int)(Math.random() * 10 + 1);
		update.add("newkey", new JsonPrimitive(newVal));
		
		test.update(query, update, false);
		
		JsonObject primitive = test.getDocument(0);
		assertTrue(primitive.getAsJsonPrimitive("newkey").getAsFloat() == newVal);
	}
	
	@Test
	public void testUpdateMulti() {
		DB db = new DB("data");
		DBCollection test = db.getCollection("test5");
		
		JsonObject query = new JsonObject();
		JsonObject update = new JsonObject();
		query.add("key", new JsonPrimitive("value"));
		int newVal = (int)(Math.random() * 10 + 1);
		update.add("newkey", new JsonPrimitive(newVal));
		
		test.update(query, update, true);
		
		JsonObject primitive = test.getDocument(0);
		assertTrue(primitive.getAsJsonPrimitive("newkey").getAsFloat() == newVal);
		primitive = test.getDocument(1);
		assertTrue(primitive.getAsJsonPrimitive("newkey").getAsFloat() == newVal);
	}
	
	@Test
	public void testRemove() {
		DB db = new DB("data");
		DBCollection test = db.getCollection("test4");
		
		// make sure correct # documents in file
		assertTrue(test.count() == 1);
		JsonObject query = new JsonObject();
		query.add("key", new JsonPrimitive("value"));
		test.remove(query, false);
		assertTrue(test.count() == 0);
	}
	
	@Test
	public void testRemoveMulti() {
		DB db = new DB("data");
		DBCollection test = db.getCollection("test6");
		
		// make sure correct # documents in file
		assertTrue(test.count() == 2);
		JsonObject query = new JsonObject();
		query.add("key", new JsonPrimitive("value"));
		test.remove(query, true);
		assertTrue(test.count() == 0);
	}

}
