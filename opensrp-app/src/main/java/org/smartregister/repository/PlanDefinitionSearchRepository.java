package org.smartregister.repository;

import android.content.ContentValues;

import net.sqlcipher.Cursor;
import net.sqlcipher.database.SQLiteDatabase;

import org.joda.time.LocalDate;
import org.smartregister.domain.PlanDefinition;

import java.util.HashSet;
import java.util.Set;

import timber.log.Timber;

/**
 * Created by samuelgithengi on 5/7/19.
 */
public class PlanDefinitionSearchRepository extends BaseRepository {

    protected static final String PLAN_ID = "plan_id";
    protected static final String JURISDICTION_ID = "jurisdiction_id";
    protected static final String NAME = "name";
    protected static final String STATUS = "status";
    protected static final String START = "start";
    protected static final String END = "end";


    protected static final String[] COLUMNS = new String[]{PLAN_ID, JURISDICTION_ID, NAME, STATUS, START, END};
    protected static final String ACTIVE = "active";

    protected static final String PLAN_DEFINITION_SEARCH_TABLE = "plan_definition_search";

    private PlanDefinitionRepository planDefinitionRepository;

    private static final String CREATE_PLAN_DEFINITION_TABLE =
            "CREATE TABLE " + PLAN_DEFINITION_SEARCH_TABLE + " (" +
                    PLAN_ID + " VARCHAR NOT NULL," +
                    JURISDICTION_ID + " VARCHAR NOT NULL," +
                    NAME + " VARCHAR NOT NULL," +
                    STATUS + " VARCHAR NOT NULL," +
                    START + " INTEGER NOT NULL," +
                    END + " INTEGER NOT NULL, PRIMARY KEY (" +
                    PLAN_ID + "," + JURISDICTION_ID + "))";

    private static final String CREATE_PLAN_DEFINITION_STATUS_INDEX = "CREATE INDEX "
            + PLAN_DEFINITION_SEARCH_TABLE + "_status_ind  ON " + PLAN_DEFINITION_SEARCH_TABLE + "(" + STATUS + ")";

    public static void createTable(SQLiteDatabase database) {
        database.execSQL(CREATE_PLAN_DEFINITION_TABLE);
        database.execSQL(CREATE_PLAN_DEFINITION_STATUS_INDEX);
    }


    public void addOrUpdate(PlanDefinition planDefinition, String jurisdiction) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(PLAN_ID, planDefinition.getIdentifier());
        contentValues.put(JURISDICTION_ID, jurisdiction);
        contentValues.put(NAME, planDefinition.getName());
        contentValues.put(STATUS, planDefinition.getStatus());
        contentValues.put(START, planDefinition.getEffectivePeriod().getStart().toDate().getTime());
        contentValues.put(END, planDefinition.getEffectivePeriod().getEnd().toDate().getTime());
        contentValues.put(JURISDICTION_ID, jurisdiction);
        getWritableDatabase().replace(PLAN_DEFINITION_SEARCH_TABLE, null, contentValues);
    }

    public Set<PlanDefinition> findActivePlansByJurisdiction(String jurisdiction) {

        Set<String> planIds = new HashSet<>();
        Cursor cursor = null;
        try {
            String query = String.format("SELECT %s FROM %s " +
                            "WHERE %s=? AND %s=?  AND %s  >=? ", PLAN_ID,
                    PLAN_DEFINITION_SEARCH_TABLE, JURISDICTION_ID, STATUS, END);
            cursor = getReadableDatabase().rawQuery(query, new String[]{jurisdiction, ACTIVE,
                    String.valueOf(LocalDate.now().toDate().getTime())});
            while (cursor.moveToNext()) {
                planIds.add(cursor.getString(0));
            }
        } catch (Exception e) {
            Timber.e(e);
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return getPlanDefinitionRepository().findPlanDefinitionByIds(planIds);
    }

    public boolean planExists(String planId, String jurisdictionId) {

        Cursor cursor = null;
        try {
            String query = String.format("SELECT %s FROM %s " +
                            "WHERE %s=? AND %s=? AND %s=?  AND %s  >=? ", PLAN_ID,
                    PLAN_DEFINITION_SEARCH_TABLE, PLAN_ID, JURISDICTION_ID, STATUS, END);
            cursor = getReadableDatabase().rawQuery(query, new String[]{planId, jurisdictionId, ACTIVE,
                    String.valueOf(LocalDate.now().toDate().getTime())});
            return cursor.moveToFirst();
        } catch (Exception e) {
            Timber.e(e);
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return false;
    }

    public void setPlanDefinitionRepository(PlanDefinitionRepository planDefinitionRepository) {
        this.planDefinitionRepository = planDefinitionRepository;

    }

    public void updateEndDate(String jurisdictionId, String planIdentifier, Long end) {

        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(END, end);
        db.update(PLAN_DEFINITION_SEARCH_TABLE, contentValues, String.format("%s= ? AND %s= ? AND %s = ?",
                JURISDICTION_ID, PLAN_ID, STATUS), new String[] {jurisdictionId, planIdentifier, ACTIVE});
        db.close();
    }

    public PlanDefinitionRepository getPlanDefinitionRepository() {
        if (planDefinitionRepository == null) {
            planDefinitionRepository = new PlanDefinitionRepository();
        }
        return planDefinitionRepository;
    }
}
