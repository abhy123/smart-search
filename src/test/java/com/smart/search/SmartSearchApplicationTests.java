package com.smart.search;

import com.smart.search.model.Searchable;
import com.smart.search.service.ApproximitySearch;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@SpringBootTest
class SmartSearchApplicationTests {

	@Test
	void contextLoads() {
	}

	@Test
	public void testgetResults() {
		Set<Searchable> set = getSearchables();

		List<Searchable> res = ApproximitySearch.getResults("Ron", set);
    	System.out.println("Res : " + res.toString());
	}

	@Test
	public void testgetResults2() {
		Set<Searchable> set = getSearchables();

		List<Searchable> res = ApproximitySearch.getResults("Ronaldo", set);
		System.out.println("Res : " + res.toString());
	}

	@Test
	public void testgetResults3() {
		Set<Searchable> set = getSearchables();

		List<Searchable> res = ApproximitySearch.getResults("Roaldo", set);
		System.out.println("Res : " + res.toString());
	}

	@Test
	public void testgetResults4() {
		Set<Searchable> set = getSearchables();

		List<Searchable> res = ApproximitySearch.getResults("beham", set);
		System.out.println("Res : " + res.toString());
	}

	@Test
	public void testgetResults5() {
		Set<Searchable> set = getSearchables();

		List<Searchable> res = ApproximitySearch.getResults("beham", set, 0.9);
		System.out.println("Res : " + res.toString());
	}

	private Set<Searchable> getSearchables() {
		Players p1 = new Players("Ronaldo");
		Players p2 = new Players("Roberto");
		Players p3 = new Players("Beckham");
		Players p4 = new Players("Ronaldinho");
		Players p5 = new Players("rivaldo");
		Players p6 = new Players("cristiano ronaldo");
		Players p7 = new Players("ronald");
		Set<Searchable> set = new HashSet<>();
		set.add(p1);
		set.add(p2);
		set.add(p3);
		set.add(p4);
		set.add(p5);
		set.add(p6);
		set.add(p7);
		return set;
	}


}
