package smartAmigos.com.nammakarnataka.helper;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by avinashk on 07/01/18.
 */

public class SQLiteDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "nk_database";
    private static final String TABLE_PLACES = "nk_places";
    private static final String TABLE_FAVOURITE = "nk_fav";
    private static final String TABLE_VISITED = "nk_visited";
    private static final String TABLE_RATED_PLACES = "nk_rated_places";

    private static final String PLACE_ID = "id";
    private static final String PLACE_NAME = "name";
    private static final String PLACE_DESCRIPTION = "description";
    private static final String PLACE_DISTRICT = "district";
    private static final String PLACE_BESTSEASON = "bestSeason";
    private static final String PLACE_ADDITIONALINFO = "additionalInformation";
    private static final String PLACE_LATITUDE = "latitude";
    private static final String PLACE_LONGITUDE = "longitude";
    private static final String PLACE_CATEGORY = "category";
    private static final String PLACE_AVGTIMESPENT = "averageTime";
    private static final String PLACE_RATING = "rating";


    public SQLiteDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String create_place_table =
                "create table "+TABLE_PLACES+" ("
                        +PLACE_ID+" integer primary key, "
                        +PLACE_NAME+"  text, "
                        +PLACE_DESCRIPTION+" text, "
                        +PLACE_DISTRICT+" text, "
                        +PLACE_BESTSEASON+" text, "
                        +PLACE_ADDITIONALINFO+" text, "
                        +PLACE_LATITUDE+" double, "
                        +PLACE_LONGITUDE+" double, "
                        +PLACE_CATEGORY+" text, "
                        +PLACE_AVGTIMESPENT+" integer, "
                        +PLACE_RATING+" double);";
        db.execSQL(create_place_table);

        String create_favourite_table = "create table "
                +TABLE_FAVOURITE+" ("+PLACE_ID+" integer primary key);";
        db.execSQL(create_favourite_table);

        String create_visited_table = "create table "
                +TABLE_VISITED+" ("+PLACE_ID+" integer primary key);";
        db.execSQL(create_visited_table);

        String create_places_rated_table = "create table "
                +TABLE_RATED_PLACES+" ("+PLACE_ID+" integer primary key);";
        db.execSQL(create_places_rated_table);




    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("drop table if exists "+TABLE_PLACES);
        db.execSQL("drop table if exists "+TABLE_FAVOURITE);
        db.execSQL("drop table if exists "+TABLE_VISITED);
        db.execSQL("drop table if exists "+TABLE_RATED_PLACES);

        onCreate(db);
    }

    public void deleteTables(){
        SQLiteDatabase db = this.getWritableDatabase();
        onUpgrade(db,0,1);
    }


    public void insertIntoPlace(int id, String name, String description,
                                   String district, String bestseason,
                                   String additionalInfo,
                                   double latitude,
                                   double longitude,
                                   String category,
                                   int avgTimeSpent,
                                   double rating){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(PLACE_ID, id);
        contentValues.put(PLACE_NAME, name);
        contentValues.put(PLACE_DESCRIPTION, description);
        contentValues.put(PLACE_DISTRICT, district);
        contentValues.put(PLACE_BESTSEASON, bestseason);
        contentValues.put(PLACE_ADDITIONALINFO, additionalInfo);
        contentValues.put(PLACE_LATITUDE, latitude);
        contentValues.put(PLACE_LONGITUDE, longitude);
        contentValues.put(PLACE_CATEGORY, category);
        contentValues.put(PLACE_AVGTIMESPENT, avgTimeSpent);
        contentValues.put(PLACE_RATING, rating);

        db.insert(TABLE_PLACES, null, contentValues);
    }

    public void insertIntoFavourites(int id){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(PLACE_ID, id);
        db.insert(TABLE_FAVOURITE, null, contentValues);
    }

    public void insertIntoVisited(int id){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(PLACE_ID, id);
        db.insert(TABLE_VISITED, null, contentValues);
    }

    public void insertIntoRatedPlaces(int id){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(PLACE_ID, id);
        db.insert(TABLE_RATED_PLACES, null, contentValues);
    }

    public Cursor getAllPlacesByCategory(String category){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery("select * from "+TABLE_PLACES+" where "+PLACE_CATEGORY+" = '"+category +"';",null);
    }


    public boolean checkIfVisited(int id){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("select count(*) from "+TABLE_VISITED+" where "+PLACE_ID+" = "+id+";",null);
        cursor.moveToNext();
        int count = cursor.getInt(0);
        cursor.close();
        return count > 0;
    }

    public boolean checkIfRatedPlace(int id){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("select count(*) from "+TABLE_RATED_PLACES+" where "+PLACE_ID+" = "+id+";",null);
        cursor.moveToNext();
        int count = cursor.getInt(0);
        cursor.close();
        return count > 0;
    }

    public boolean checkIfFavourited(int id){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("select count(*) from "+TABLE_FAVOURITE+" where "+PLACE_ID+" = "+id+";",null);
        cursor.moveToNext();
        int count = cursor.getInt(0);
        cursor.close();
        return count > 0;
    }

    public Cursor getAllPlaces(){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery("select * from "+TABLE_PLACES+";",null);
    }

    public Cursor getPlaceById(int id)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery("select * from "+TABLE_PLACES+" where "+PLACE_ID+" = "+id+";",null);
    }

    public Cursor getPlaceByDistrict(String dist){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery("select * from "+TABLE_PLACES+" where "+PLACE_DISTRICT+" = '"+dist+"' ;",null);
    }
    public Cursor getPlaceByString(String str){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery("select * from "+TABLE_PLACES+" where "+PLACE_NAME+" like '%"+str+"%' ;",null);
    }
    public Cursor getAllDistricts(){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery("select distinct "+PLACE_DISTRICT+" from "+TABLE_PLACES+" order by "+PLACE_DISTRICT+" ;",null);
    }


}
