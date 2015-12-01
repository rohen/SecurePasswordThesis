/*
 * Copyright (c) 2015 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

/*
 * Adapted from com.android.internal.widget.LockPatternUtils at
 * 2cb687e7b9d0cbb1af5ba753453a9a05350a100e.
 */

package me.zhanghai.patternlock;

import android.util.Base64;

import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;

public class PatternUtils {

	private PatternUtils() {
	}

	public static String bytesToString(byte[] bytes) {
		return Base64.encodeToString(bytes, Base64.NO_WRAP);
	}

	public static byte[] stringToBytes(String string) {
		return Base64.decode(string, Base64.DEFAULT);
	}

	public static byte[] patternToBytes(List<PatternView.Cell> pattern) {
		int patternSize = pattern.size();
		byte[] bytes = new byte[patternSize];
		for (int i = 0; i < patternSize; ++i) {
			PatternView.Cell cell = pattern.get(i);
			bytes[i] = (byte) (cell.getRow() * 3 + cell.getColumn());
		}
		return bytes;
	}

	public static List<PatternView.Cell> bytesToPattern(byte[] bytes) {
		List<PatternView.Cell> pattern = new ArrayList<>();
		for (byte b : bytes) {
			pattern.add(PatternView.Cell.of(b / 3, b % 3));
		}
		return pattern;
	}

	public static String patternToString(List<PatternView.Cell> pattern) {
		return bytesToString(patternToBytes(pattern));
	}

	public static List<PatternView.Cell> stringToPattern(String string) {
		return bytesToPattern(stringToBytes(string));
	}

	public static String getSha1Hex(List<PatternView.Cell> cellList)
	{
		try
		{
			String cells = PatternUtils.patternToString(cellList);
			MessageDigest messageDigest = MessageDigest.getInstance("SHA-1");
			messageDigest.update(cells.getBytes());
			byte[] bytes = messageDigest.digest();
			StringBuilder buffer = new StringBuilder();
			for (byte b : bytes)
			{
				buffer.append(Integer.toString((b & 0xff) + 0x100, 16).substring(1));
			}
			return buffer.toString();
		}
		catch (Exception ignored)
		{
			ignored.printStackTrace();
			return null;
		}
	}

}
