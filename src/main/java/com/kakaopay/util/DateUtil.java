package com.kakaopay.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {
	/**
	 * 
	 * 두 시간의 차이를 계산
	 * - 차이값은 절대값으로 반환하며 3번째 인자에 따라 단위가 달라짐
	 * 
	 * @param 	s1			첫번째 입력시간(yyyy-mm-dd HH:mm:ss)
	 * @param 	s2			두번째 입력시간(yyyy-mm-dd HH:mm:ss)
	 * @param 	minutes		분단위 출력은 1, 일단위 출력은 1440
	 * @return	calDateDays 시간 차이
	 */
	public static int getDiffTime(String s1, String s2, int minutes) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		long calDate = 0;
		long calDateDays = 0;
		try {
			Date fd = sdf.parse(s1);
			Date sd = sdf.parse(s2);
			calDate = Math.abs(fd.getTime() - sd.getTime());
			calDateDays = calDate / ( minutes*60*1000 );
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return (int)calDateDays;
	}
	
	public static int getDiffBetweenCurrentTime(String s1, int minutes) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		long calDate = 0;
		long calDateDays = 0;
		try {
			Date fd = new Date();
			Date sd = sdf.parse(s1);
			calDate = Math.abs(fd.getTime() - sd.getTime());
			calDateDays = calDate / ( minutes*60*1000 );
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return (int)calDateDays;
	}
	
//	public static void main(String[] args) {
//		System.out.println(getDiffBetweenCurrentTime("2020-08-30 20:00:00", 1));
//	}
	
}
