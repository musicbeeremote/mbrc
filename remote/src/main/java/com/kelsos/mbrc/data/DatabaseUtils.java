package com.kelsos.mbrc.data;

import android.database.sqlite.SQLiteDatabase;

public final class DatabaseUtils {

	private DatabaseUtils() { }

	public static void createDatabaseTrigger(SQLiteDatabase db) {
		String query = "CREATE TRIGGER IF NOT EXISTS update_position AFTER DELETE\n"
				+ "ON QUEUE_TRACK\n"
				+ "BEGIN\n"
				+ "  UPDATE QUEUE_TRACK SET POSITION = POSITION - 1\n"
				+ "  WHERE POSITION > OLD.POSITION;\n"
				+ "END; ";
		db.execSQL(query);
	}

	public static void updatePosition(SQLiteDatabase db, int fromPosition, int toPosition) {
		String query;
		if (fromPosition < toPosition) {
			query = "UPDATE QUEUE_TRACK\n"
					+ "SET POSITION = POSITION-1\n"
					+ "WHERE POSITION > ? AND POSITION <= ? ";
		} else {
			query = "UPDATE QUEUE_TRACK\n"
					+ "SET POSITION = POSITION+1\n"
					+ "WHERE POSITION < ? AND POSITION >= ? ";
		}

		Integer[] args = new Integer[] {
				fromPosition,
				toPosition
		};

		db.execSQL(query, args);


	}
}
