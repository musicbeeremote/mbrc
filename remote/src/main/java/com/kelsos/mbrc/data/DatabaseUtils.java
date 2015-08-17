package com.kelsos.mbrc.data;

import android.database.sqlite.SQLiteDatabase;
import com.kelsos.mbrc.dao.QueueTrack;
import com.kelsos.mbrc.dao.QueueTrack$Table;
import com.raizlabs.android.dbflow.sql.builder.Condition;
import com.raizlabs.android.dbflow.sql.language.Update;
import com.raizlabs.android.dbflow.sql.trigger.CompletedTrigger;
import com.raizlabs.android.dbflow.sql.trigger.Trigger;

public final class DatabaseUtils {

  private DatabaseUtils() {
  }

  public static void createPositionTrigger() {
    CompletedTrigger<QueueTrack> trigger = Trigger.create("update_position")
        .after()
        .delete(QueueTrack.class)
        .begin(new Update<>(QueueTrack.class).set(Condition.column(QueueTrack$Table.POSITION)
                .is(QueueTrack$Table.POSITION)
                .postfix(" - 1"))
                .where(Condition.column(QueueTrack$Table.POSITION)
                    .greaterThan("old." + QueueTrack$Table.POSITION))

        );
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
