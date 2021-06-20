package com.near.consultadnis.database.entidades;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Dao;
import androidx.room.Entity;
import androidx.room.Insert;
import androidx.room.PrimaryKey;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

public class Personas {

    @Entity(tableName = "personas")
    public static class tb{
        @PrimaryKey
        @ColumnInfo(name = "dni") @NonNull              private String dni;
        @ColumnInfo(name = "nombre") @NonNull           private String nombre;
        @ColumnInfo(name = "apellido") @NonNull         private String apellido;

        public tb(@NonNull String dni, @NonNull String nombre, @NonNull String apellido) {
            this.dni = dni;
            this.nombre = nombre;
            this.apellido = apellido;
        }

        @NonNull public String getDni() { return dni; }
        public void setDni(@NonNull String dni) { this.dni = dni; }
        @NonNull public String getNombre() { return nombre; }
        public void setNombre(@NonNull String nombre) { this.nombre = nombre; }
        @NonNull public String getApellido() { return apellido; }
        public void setApellido(@NonNull String apellido) { this.apellido = apellido; }
    }

    @Dao
    public interface sql{

        @Insert
        void insert( tb persona );

        @Update
        void update( tb persona );

        @Query( "SELECT * FROM personas" )
        List<tb> all();

        @Query( "SELECT count(*) FROM personas" )
        int count();

        @Query( "SELECT * FROM personas WHERE dni=:dni LIMIT 1" )
        tb find( String dni );

    }

}
