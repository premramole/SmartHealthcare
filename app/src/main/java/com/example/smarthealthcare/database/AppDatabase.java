package com.example.smarthealthcare.database;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.room.migration.Migration;

import com.example.smarthealthcare.database.dao.AppointmentDao;
import com.example.smarthealthcare.database.dao.ChatMessageDao;
import com.example.smarthealthcare.database.dao.MedicalReportDao;
import com.example.smarthealthcare.database.dao.PrescriptionDao;
import com.example.smarthealthcare.database.dao.UserDao;
import com.example.smarthealthcare.database.entities.AppointmentEntity;
import com.example.smarthealthcare.database.entities.ChatMessageEntity;
import com.example.smarthealthcare.database.entities.MedicalReportEntity;
import com.example.smarthealthcare.database.entities.PrescriptionEntity;
import com.example.smarthealthcare.database.entities.UserEntity;

@Database(entities = {
    UserEntity.class,
    AppointmentEntity.class,
    ChatMessageEntity.class,
    MedicalReportEntity.class,
    PrescriptionEntity.class
}, version = 7, exportSchema = false)
@TypeConverters({Converters.class})
public abstract class AppDatabase extends RoomDatabase {
    
    private static AppDatabase INSTANCE;
    
    public abstract UserDao userDao();
    public abstract AppointmentDao appointmentDao();
    public abstract ChatMessageDao chatMessageDao();
    public abstract MedicalReportDao medicalReportDao();
    public abstract PrescriptionDao prescriptionDao();
    
    public static final Migration MIGRATION_5_6 = new Migration(5, 6) {
        @Override
        public void migrate(@androidx.annotation.NonNull androidx.sqlite.db.SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE users ADD COLUMN latitude REAL NOT NULL DEFAULT 0.0");
            database.execSQL("ALTER TABLE users ADD COLUMN longitude REAL NOT NULL DEFAULT 0.0");
        }
    };

    public static final Migration MIGRATION_6_7 = new Migration(6, 7) {
        @Override
        public void migrate(@androidx.annotation.NonNull androidx.sqlite.db.SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE appointments ADD COLUMN symptoms TEXT");
        }
    };

    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, "smarthealthcare_database")
                            .allowMainThreadQueries()
                            .fallbackToDestructiveMigration()
                            .addMigrations(MIGRATION_5_6, MIGRATION_6_7)
                            .addCallback(new RoomDatabase.Callback() {
                                @Override
                                public void onCreate(@androidx.annotation.NonNull androidx.sqlite.db.SupportSQLiteDatabase db) {
                                    super.onCreate(db);
                                    // Room will call this inside its own thread/transaction
                                }
                                @Override
                                public void onOpen(@androidx.annotation.NonNull androidx.sqlite.db.SupportSQLiteDatabase db) {
                                    super.onOpen(db);
                                    // Run seeding synchronously on the background thread that Room uses to open the DB
                                    if (INSTANCE != null) {
                                        seedData(INSTANCE);
                                    }
                                }
                            })
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    private static void seedData(AppDatabase db) {
        try {
            UserDao userDao = db.userDao();
            android.util.Log.d("AppDatabase", "Seeding started...");
            
            // Check if any users exist
            UserEntity existingDoctor = userDao.getUserByEmail("doctor@test.com");
            if (existingDoctor == null) {
                // Seed Doctor
                UserEntity doctor = new UserEntity();
                doctor.setUserId("doctor_test_001");
                doctor.setName("Dr. Jane Smith");
                doctor.setEmail("doctor@test.com");
                doctor.setPassword("password");
                doctor.setRole("doctor");
                doctor.setSpecialization("General Physician");
                doctor.setFees(500);
                doctor.setLocation("Downtown Clinic");
                doctor.setAvailableDays("Mon,Wed,Fri");
                doctor.setTimeSlots("9:00 AM - 12:00 PM");
                doctor.setLatitude(12.9716); // Bangalore central coords
                doctor.setLongitude(77.5946);
                userDao.insertUser(doctor);
                android.util.Log.d("AppDatabase", "Seeded doctor@test.com");
            }

            UserEntity existingPatient = userDao.getUserByEmail("patient@test.com");
            if (existingPatient == null) {
                // Seed Patient
                UserEntity patient = new UserEntity();
                patient.setUserId("patient_test_001");
                patient.setName("John Doe");
                patient.setEmail("patient@test.com");
                patient.setPassword("password");
                patient.setRole("patient");
                patient.setMedicalHistory("No significant history.");
                userDao.insertUser(patient);
                android.util.Log.d("AppDatabase", "Seeded patient@test.com");
            }
            android.util.Log.d("AppDatabase", "Seeding completed");
        } catch (Exception e) {
            android.util.Log.e("AppDatabase", "Error seeding data", e);
        }
    }
    
    public static void destroyInstance() {
        INSTANCE = null;
    }
}
