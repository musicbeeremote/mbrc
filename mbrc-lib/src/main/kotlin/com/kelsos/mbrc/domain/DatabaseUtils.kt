package com.kelsos.mbrc.domain

import android.database.sqlite.SQLiteDatabase

object DatabaseUtils {

    fun createPositionTrigger() {
        //CompletedTrigger<QueueTrackDao> trigger = Trigger.create("update_position")
        //    .after()
        //    .delete(QueueTrackDao.class)
        //    .begin(new Update<>(QueueTrackDao.class).set(Condition.column(QueueTrackDao$Table.POSITION)
        //            .is(QueueTrackDao$Table.POSITION)
        //            .postfix(" - 1"))
        //            .where(Condition.column(QueueTrackDao$Table.POSITION)
        //                .greaterThan("old." + QueueTrackDao$Table.POSITION))
        //
        //    );
    }

    fun createDatabaseTrigger(db: SQLiteDatabase) {
//        val query = "CREATE TRIGGER IF NOT EXISTS update_position AFTER DELETE\n"
//        +"ON QUEUE_TRACK\n"
//        +"BEGIN\n"
//        +"  UPDATE QUEUE_TRACK SET POSITION = POSITION - 1\n"
//        +"  WHERE POSITION > OLD.POSITION;\n"
//        +"END; "
//        db.execSQL(query)
    }

    fun updatePosition(fromPosition: Int, toPosition: Int) {
        //ConditionQueryBuilder<QueueTrackDao> queryBuilder =
        //    fromPosition < toPosition ? new ConditionQueryBuilder<>(QueueTrackDao.class,
        //        Condition.column(QueueTrackDao$Table.POSITION).greaterThan(fromPosition)).and(
        //        Condition.column(QueueTrackDao$Table.POSITION).lessThanOrEq(toPosition))
        //        : new ConditionQueryBuilder<>(QueueTrackDao.class,
        //            Condition.column(QueueTrackDao$Table.POSITION).lessThan(fromPosition)).and(
        //            Condition.column(QueueTrackDao$Table.POSITION).greaterThanOrEq(toPosition));
        //
        //final String change = fromPosition < toPosition ? "-1" : "+1";
        //new Update<>(QueueTrackDao.class).set(
        //    Condition.column(QueueTrackDao$Table.POSITION).is(QueueTrackDao$Table.POSITION).postfix(change))
        //    .where(queryBuilder)
        //    .queryClose();
    }
}
