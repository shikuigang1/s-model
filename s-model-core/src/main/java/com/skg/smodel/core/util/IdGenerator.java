package com.skg.smodel.core.util;


import org.springframework.data.keyvalue.core.IdentifierGenerator;
import org.springframework.data.util.TypeInformation;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class IdGenerator implements IdentifierGenerator {
	private static final char nm[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' };
	private static final int bound_nm = 10;
	private static final char ch[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
			'a', 'b','c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z' };
	private static final int bound = 36;

	public static String newId(){
		String id = Long.toString(System.nanoTime()) + gensalt(6) + "0000";
		return id.substring(0, 20);
	}

	/**
	 * 生成订单号
	 * @param prefix
	 * 前缀
	 * @return
	 */
	public static String newBillNo(String prefix, Date date) {
		SimpleDateFormat dateFormatGmt = new SimpleDateFormat("yyyyMMddHHmmssSSS");
		return prefix+dateFormatGmt.format(date);
	}


	public static String gensalt(int length) {
		StringBuilder buf = new StringBuilder();
		Random rnd = new Random();
		for (int i = 0; i < length; i++){
			char c = ch[rnd.nextInt(bound)];
			buf.append(c);
		}
		return buf.toString();
	}

	public static String gensalt_num(int length) {
		StringBuilder buf = new StringBuilder();
		Random rnd = new Random();
		for (int i = 0; i < length; i++){
			char c = nm[rnd.nextInt(bound_nm)];

			if (i == 0 && c == '0') {
				i--;
				continue;
			}

			buf.append(c);
		}
		return buf.toString();
	}

	@Override
	public <T> T generateIdentifierOfType(TypeInformation<T> typeInformation) {
		return null;
	}

	public static void main(String[] args) {
//		String s = gensalt(4);
		String s = newBillNo("SG",new Date());
		System.out.println(s);
	}
}
