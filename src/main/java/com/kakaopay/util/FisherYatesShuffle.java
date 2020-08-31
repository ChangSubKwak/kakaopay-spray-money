package com.kakaopay.util;

import java.security.SecureRandom;
import java.util.LinkedList;
import java.util.Random;

public class FisherYatesShuffle {
	static String seed = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
	static LinkedList<String> ll = new LinkedList<>();
	static Random random = new SecureRandom();
	
	public static void init() {
		char[] ch = new char[3];
		for (int i = 0 ; i < seed.length() ; i++) {
			ch[0] = seed.charAt(i);
			for (int j = 0 ; j < seed.length() ; j++) {
				ch[1] = seed.charAt(j);
				for (int k = 0 ; k < seed.length() ; k++) {
					ch[2] = seed.charAt(k);
					ll.add(String.valueOf(ch));
				}
			}
		}
	}
	
	public static String getShuffleVal() {
		if (ll.size() < 1) init();
		//int rv = (int) (Math.random() * ll.size());
		int rv = random.nextInt(ll.size());
		return ll.remove(rv);
	}
}