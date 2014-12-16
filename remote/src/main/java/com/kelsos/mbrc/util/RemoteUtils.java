package com.kelsos.mbrc.util;

import android.database.sqlite.SQLiteDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public final class RemoteUtils {

	private RemoteUtils() {
	}

	public static String getTimeStamp() {
		final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyymmddhhmmss", Locale.getDefault());
		return simpleDateFormat.format(new Date());
	}

	public static boolean isNullOrEmpty(String string) {
		return (string == null || string.equals(""));
	}

	public static void createDatabaseTrigger(SQLiteDatabase db) {
		String query = "CREATE TRIGGER IF NOT EXISTS update_position AFTER DELETE\n"
				+ "ON QUEUE_TRACK\n"
				+ "BEGIN\n"
				+ "  UPDATE QUEUE_TRACK SET POSITION = POSITION - 1\n"
				+ "  WHERE POSITION > OLD.POSITION;\n"
				+ "END; ";
		db.execSQL(query);
	}
}
