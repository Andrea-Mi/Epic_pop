package com.example.epic_pop.database;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import android.content.Context;
import com.example.epic_pop.models.Usuario;
import com.example.epic_pop.models.Reto;
import com.example.epic_pop.models.Logro;

@Database(
    entities = {Usuario.class, Reto.class, Logro.class},
    version = 3, // Incrementando a versión 3 para forzar recreación
    exportSchema = false
)
public abstract class EpicPopDatabase extends RoomDatabase {

    private static volatile EpicPopDatabase INSTANCE;

    public abstract UsuarioDao usuarioDao();
    public abstract RetoDao retoDao();
    public abstract LogroDao logroDao();

    public static EpicPopDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (EpicPopDatabase.class) {
                if (INSTANCE == null) {
                    try {
                        INSTANCE = Room.databaseBuilder(
                                context.getApplicationContext(),
                                EpicPopDatabase.class,
                                "epic_pop_database_v3" // Nuevo nombre para forzar recreación
                        )
                        .fallbackToDestructiveMigration()
                        .allowMainThreadQueries()
                        .build();

                        android.util.Log.d("EpicPop", "Base de datos creada exitosamente");
                    } catch (Exception e) {
                        android.util.Log.e("EpicPop", "Error crítico creando base de datos", e);
                        throw e;
                    }
                }
            }
        }
        return INSTANCE;
    }
}
