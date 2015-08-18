package com.kelsos.mbrc.data;

import android.database.sqlite.SQLiteDatabase;
import com.kelsos.mbrc.dao.QueueTrack;
import com.kelsos.mbrc.dao.QueueTrack$Table;
import com.raizlabs.android.dbflow.sql.builder.Condition;
import com.raizlabs.android.dbflow.sql.builder.ConditionQueryBuilder;
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

  public static void updatePosition(int fromPosition, int toPosition) {
    ConditionQueryBuilder<QueueTrack> queryBuilder =
        fromPosition < toPosition ? new ConditionQueryBuilder<>(QueueTrack.class,
            Condition.column(QueueTrack$Table.POSITION).greaterThan(fromPosition)).and(
            Condition.column(QueueTrack$Table.POSITION).lessThanOrEq(toPosition))
            : new ConditionQueryBuilder<>(QueueTrack.class,
                Condition.column(QueueTrack$Table.POSITION).lessThan(fromPosition)).and(
                Condition.column(QueueTrack$Table.POSITION).greaterThanOrEq(toPosition));

    final String change = fromPosition < toPosition ? "-1" : "+1";
    new Update<>(QueueTrack.class).set(
        Condition.column(QueueTrack$Table.POSITION).is(QueueTrack$Table.POSITION).postfix(change))
        .where(queryBuilder)
        .queryClose();
  }
}
