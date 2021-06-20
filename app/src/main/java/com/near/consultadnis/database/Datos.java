package com.near.consultadnis.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.near.consultadnis.database.entidades.Personas;
import com.near.consultadnis.utils.Utils;

@Database(
        entities = {
                Personas.tb.class
        },
        version = 1,
        exportSchema = false
)

public abstract class Datos extends RoomDatabase {

    public static Datos INSTANCE;

    /*** TABLAS **/
    public abstract Personas.sql tb_personas();


    public static Datos DB(Context context) {
        if (INSTANCE == null){
            INSTANCE = Room.databaseBuilder(context, Datos.class, Utils.var.DATABASE_NAME)
                    //.addMigrations( version_x1_x2 )
                    .allowMainThreadQueries()
                    .build();
        }
        return INSTANCE;
    }

}
