package com.example.smarthealthcare.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.smarthealthcare.models.User;
import com.example.smarthealthcare.models.Appointment;
import com.example.smarthealthcare.models.ChatMessage;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String TAG = "DatabaseHelper";
    private static final String DATABASE_NAME = "SmartHealthcare.db";
    private static final int DATABASE_VERSION = 1;

    // Table names
    public static final String TABLE_USERS = "users";
    public static final String TABLE_HEALTH_RECORDS = "health_records";
    public static final String TABLE_APPOINTMENTS = "appointments";
    public static final String TABLE_MEDICAL_REPORTS = "medical_reports";
    public static final String TABLE_MESSAGES = "messages";

    // Common column names
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_USER_ID = "user_id";
    public static final String COLUMN_CREATED_AT = "created_at";

    // USERS table columns
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_EMAIL = "email";
    public static final String COLUMN_PASSWORD = "password";
    public static final String COLUMN_ROLE = "role";
    public static final String COLUMN_MEDICAL_HISTORY = "medical_history";
    public static final String COLUMN_SPECIALIZATION = "specialization";
    public static final String COLUMN_RATING = "rating";
    public static final String COLUMN_LOCATION = "location";
    public static final String COLUMN_FEES = "fees";

    // HEALTH_RECORDS table columns
    public static final String COLUMN_RECORD_ID = "record_id";
    public static final String COLUMN_PATIENT_ID = "patient_id";
    public static final String COLUMN_HEART_RATE = "heart_rate";
    public static final String COLUMN_BLOOD_PRESSURE = "blood_pressure";
    public static final String COLUMN_TEMPERATURE = "temperature";
    public static final String COLUMN_OXYGEN_LEVEL = "oxygen_level";
    public static final String COLUMN_RECORDED_TIME = "recorded_time";

    // APPOINTMENTS table columns
    public static final String COLUMN_APPOINTMENT_ID = "appointment_id";
    public static final String COLUMN_DOCTOR_ID = "doctor_id";
    public static final String COLUMN_PATIENT_NAME = "patient_name";
    public static final String COLUMN_APPOINTMENT_DATE = "appointment_date";
    public static final String COLUMN_MODE = "mode";
    public static final String COLUMN_STATUS = "status";

    // MEDICAL_REPORTS table columns
    public static final String COLUMN_REPORT_ID = "report_id";
    public static final String COLUMN_REPORT_FILE_PATH = "report_file_path";
    public static final String COLUMN_UPLOAD_DATE = "upload_date";

    // MESSAGES table columns
    public static final String COLUMN_MESSAGE_ID = "message_id";
    public static final String COLUMN_SENDER_ID = "sender_id";
    public static final String COLUMN_RECEIVER_ID = "receiver_id";
    public static final String COLUMN_MESSAGE_TEXT = "message_text";
    public static final String COLUMN_TIMESTAMP = "timestamp";

    // Create table statements
    private static final String CREATE_TABLE_USERS = "CREATE TABLE " + TABLE_USERS + "("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_USER_ID + " TEXT UNIQUE NOT NULL,"
            + COLUMN_NAME + " TEXT NOT NULL,"
            + COLUMN_EMAIL + " TEXT UNIQUE NOT NULL,"
            + COLUMN_PASSWORD + " TEXT NOT NULL,"
            + COLUMN_ROLE + " TEXT NOT NULL,"
            + COLUMN_MEDICAL_HISTORY + " TEXT,"
            + COLUMN_SPECIALIZATION + " TEXT,"
            + COLUMN_RATING + " REAL DEFAULT 0.0,"
            + COLUMN_LOCATION + " TEXT,"
            + COLUMN_FEES + " INTEGER DEFAULT 0,"
            + COLUMN_CREATED_AT + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP"
            + ")";

    private static final String CREATE_TABLE_HEALTH_RECORDS = "CREATE TABLE " + TABLE_HEALTH_RECORDS + "("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_RECORD_ID + " TEXT UNIQUE NOT NULL,"
            + COLUMN_PATIENT_ID + " TEXT NOT NULL,"
            + COLUMN_HEART_RATE + " INTEGER,"
            + COLUMN_BLOOD_PRESSURE + " TEXT,"
            + COLUMN_TEMPERATURE + " REAL,"
            + COLUMN_OXYGEN_LEVEL + " REAL,"
            + COLUMN_RECORDED_TIME + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP,"
            + COLUMN_CREATED_AT + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP,"
            + "FOREIGN KEY(" + COLUMN_PATIENT_ID + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_USER_ID + ")"
            + ")";

    private static final String CREATE_TABLE_APPOINTMENTS = "CREATE TABLE " + TABLE_APPOINTMENTS + "("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_APPOINTMENT_ID + " TEXT UNIQUE NOT NULL,"
            + COLUMN_DOCTOR_ID + " TEXT NOT NULL,"
            + COLUMN_PATIENT_ID + " TEXT NOT NULL,"
            + COLUMN_PATIENT_NAME + " TEXT,"
            + COLUMN_APPOINTMENT_DATE + " TEXT NOT NULL,"
            + COLUMN_MODE + " TEXT DEFAULT 'offline',"
            + COLUMN_STATUS + " TEXT DEFAULT 'pending',"
            + COLUMN_CREATED_AT + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP,"
            + "FOREIGN KEY(" + COLUMN_DOCTOR_ID + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_USER_ID + "),"
            + "FOREIGN KEY(" + COLUMN_PATIENT_ID + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_USER_ID + ")"
            + ")";

    private static final String CREATE_TABLE_MEDICAL_REPORTS = "CREATE TABLE " + TABLE_MEDICAL_REPORTS + "("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_REPORT_ID + " TEXT UNIQUE NOT NULL,"
            + COLUMN_PATIENT_ID + " TEXT NOT NULL,"
            + COLUMN_REPORT_FILE_PATH + " TEXT NOT NULL,"
            + COLUMN_UPLOAD_DATE + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP,"
            + COLUMN_CREATED_AT + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP,"
            + "FOREIGN KEY(" + COLUMN_PATIENT_ID + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_USER_ID + ")"
            + ")";

    private static final String CREATE_TABLE_MESSAGES = "CREATE TABLE " + TABLE_MESSAGES + "("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_MESSAGE_ID + " TEXT UNIQUE NOT NULL,"
            + COLUMN_SENDER_ID + " TEXT NOT NULL,"
            + COLUMN_RECEIVER_ID + " TEXT NOT NULL,"
            + COLUMN_MESSAGE_TEXT + " TEXT NOT NULL,"
            + COLUMN_TIMESTAMP + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP,"
            + COLUMN_CREATED_AT + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP,"
            + "FOREIGN KEY(" + COLUMN_SENDER_ID + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_USER_ID + "),"
            + "FOREIGN KEY(" + COLUMN_RECEIVER_ID + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_USER_ID + ")"
            + ")";

    private static DatabaseHelper instance;

    public static synchronized DatabaseHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseHelper(context.getApplicationContext());
        }
        return instance;
    }

    private DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG, "Creating database tables");
        db.execSQL(CREATE_TABLE_USERS);
        db.execSQL(CREATE_TABLE_HEALTH_RECORDS);
        db.execSQL(CREATE_TABLE_APPOINTMENTS);
        db.execSQL(CREATE_TABLE_MEDICAL_REPORTS);
        db.execSQL(CREATE_TABLE_MESSAGES);
        Log.d(TAG, "Database tables created successfully");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(TAG, "Upgrading database from version " + oldVersion + " to " + newVersion);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MESSAGES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MEDICAL_REPORTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_APPOINTMENTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_HEALTH_RECORDS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        onCreate(db);
    }

    // User CRUD operations
    public long insertUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_ID, user.getUserId());
        values.put(COLUMN_NAME, user.getName());
        values.put(COLUMN_EMAIL, user.getEmail());
        values.put(COLUMN_PASSWORD, user.getPassword() != null ? user.getPassword() : "");
        values.put(COLUMN_ROLE, user.getRole());
        values.put(COLUMN_MEDICAL_HISTORY, user.getMedicalHistory());
        values.put(COLUMN_SPECIALIZATION, user.getSpecialization());
        values.put(COLUMN_RATING, user.getRating());
        values.put(COLUMN_LOCATION, user.getLocation());
        values.put(COLUMN_FEES, user.getFees());

        long result = db.insert(TABLE_USERS, null, values);
        Log.d(TAG, "User inserted with ID: " + result);
        return result;
    }

    public User getUserById(String userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        User user = null;
        
        Cursor cursor = db.query(TABLE_USERS, null, COLUMN_USER_ID + " = ?",
                new String[]{userId}, null, null, null);
        
        if (cursor != null && cursor.moveToFirst()) {
            user = cursorToUser(cursor);
            cursor.close();
        }
        
        return user;
    }

    public User getUserByEmail(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        User user = null;
        
        Cursor cursor = db.query(TABLE_USERS, null, COLUMN_EMAIL + " = ?",
                new String[]{email}, null, null, null);
        
        if (cursor != null && cursor.moveToFirst()) {
            user = cursorToUser(cursor);
            cursor.close();
        }
        
        return user;
    }

    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        
        Cursor cursor = db.query(TABLE_USERS, null, null, null, null, null, COLUMN_NAME + " ASC");
        
        if (cursor != null && cursor.moveToFirst()) {
            do {
                users.add(cursorToUser(cursor));
            } while (cursor.moveToNext());
            cursor.close();
        }
        
        return users;
    }

    public List<User> getUsersByRole(String role) {
        List<User> users = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        
        Cursor cursor = db.query(TABLE_USERS, null, COLUMN_ROLE + " = ?",
                new String[]{role}, null, null, COLUMN_NAME + " ASC");
        
        if (cursor != null && cursor.moveToFirst()) {
            do {
                users.add(cursorToUser(cursor));
            } while (cursor.moveToNext());
            cursor.close();
        }
        
        return users;
    }

    public int updateUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, user.getName());
        values.put(COLUMN_EMAIL, user.getEmail());
        values.put(COLUMN_ROLE, user.getRole());
        values.put(COLUMN_MEDICAL_HISTORY, user.getMedicalHistory());
        values.put(COLUMN_SPECIALIZATION, user.getSpecialization());
        values.put(COLUMN_RATING, user.getRating());
        values.put(COLUMN_LOCATION, user.getLocation());
        values.put(COLUMN_FEES, user.getFees());

        return db.update(TABLE_USERS, values, COLUMN_USER_ID + " = ?",
                new String[]{user.getUserId()});
    }

    public int deleteUser(String userId) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_USERS, COLUMN_USER_ID + " = ?", new String[]{userId});
    }

    private User cursorToUser(Cursor cursor) {
        User user = new User();
        user.setUserId(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USER_ID)));
        user.setName(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME)));
        user.setEmail(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMAIL)));
        user.setPassword(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PASSWORD)));
        user.setRole(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ROLE)));
        user.setMedicalHistory(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_MEDICAL_HISTORY)));
        user.setSpecialization(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SPECIALIZATION)));
        user.setRating(cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_RATING)));
        user.setLocation(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_LOCATION)));
        user.setFees(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_FEES)));
        return user;
    }

    // Health Record CRUD operations
    public long insertHealthRecord(String patientId, int heartRate, String bloodPressure, 
                                  float temperature, float oxygenLevel) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        String recordId = "HR_" + System.currentTimeMillis();
        
        values.put(COLUMN_RECORD_ID, recordId);
        values.put(COLUMN_PATIENT_ID, patientId);
        values.put(COLUMN_HEART_RATE, heartRate);
        values.put(COLUMN_BLOOD_PRESSURE, bloodPressure);
        values.put(COLUMN_TEMPERATURE, temperature);
        values.put(COLUMN_OXYGEN_LEVEL, oxygenLevel);

        long result = db.insert(TABLE_HEALTH_RECORDS, null, values);
        Log.d(TAG, "Health record inserted with ID: " + result);
        return result;
    }

    // Appointment CRUD operations
    public long insertAppointment(Appointment appointment) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        
        values.put(COLUMN_APPOINTMENT_ID, appointment.getAppointmentId());
        values.put(COLUMN_DOCTOR_ID, appointment.getDoctorId());
        values.put(COLUMN_PATIENT_ID, appointment.getPatientId());
        values.put(COLUMN_PATIENT_NAME, appointment.getPatientName());
        values.put(COLUMN_APPOINTMENT_DATE, appointment.getDateTime());
        values.put(COLUMN_MODE, appointment.getMode());
        values.put(COLUMN_STATUS, appointment.getStatus());

        long result = db.insert(TABLE_APPOINTMENTS, null, values);
        Log.d(TAG, "Appointment inserted with ID: " + result);
        return result;
    }

    public List<Appointment> getAppointmentsForDoctor(String doctorId) {
        List<Appointment> appointments = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        
        Cursor cursor = db.query(TABLE_APPOINTMENTS, null, COLUMN_DOCTOR_ID + " = ?",
                new String[]{doctorId}, null, null, COLUMN_APPOINTMENT_DATE + " DESC");
        
        if (cursor != null && cursor.moveToFirst()) {
            do {
                appointments.add(cursorToAppointment(cursor));
            } while (cursor.moveToNext());
            cursor.close();
        }
        
        return appointments;
    }

    public List<Appointment> getAppointmentsForPatient(String patientId) {
        List<Appointment> appointments = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        
        Cursor cursor = db.query(TABLE_APPOINTMENTS, null, COLUMN_PATIENT_ID + " = ?",
                new String[]{patientId}, null, null, COLUMN_APPOINTMENT_DATE + " DESC");
        
        if (cursor != null && cursor.moveToFirst()) {
            do {
                appointments.add(cursorToAppointment(cursor));
            } while (cursor.moveToNext());
            cursor.close();
        }
        
        return appointments;
    }

    public int updateAppointmentStatus(String appointmentId, String status) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_STATUS, status);

        return db.update(TABLE_APPOINTMENTS, values, COLUMN_APPOINTMENT_ID + " = ?",
                new String[]{appointmentId});
    }

    private Appointment cursorToAppointment(Cursor cursor) {
        Appointment appointment = new Appointment();
        appointment.setAppointmentId(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_APPOINTMENT_ID)));
        appointment.setDoctorId(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DOCTOR_ID)));
        appointment.setPatientId(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PATIENT_ID)));
        appointment.setPatientName(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PATIENT_NAME)));
        appointment.setDateTime(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_APPOINTMENT_DATE)));
        appointment.setMode(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_MODE)));
        appointment.setStatus(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_STATUS)));
        return appointment;
    }

    // Message CRUD operations
    public long insertMessage(ChatMessage message) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        
        values.put(COLUMN_MESSAGE_ID, message.getMessageId());
        values.put(COLUMN_SENDER_ID, message.getSenderId());
        values.put(COLUMN_RECEIVER_ID, message.getReceiverId());
        values.put(COLUMN_MESSAGE_TEXT, message.getMessageText());
        values.put(COLUMN_TIMESTAMP, message.getTimestamp());

        long result = db.insert(TABLE_MESSAGES, null, values);
        Log.d(TAG, "Message inserted with ID: " + result);
        return result;
    }

    public List<ChatMessage> getChatMessages(String userId1, String userId2) {
        List<ChatMessage> messages = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        
        String selection = "(" + COLUMN_SENDER_ID + " = ? AND " + COLUMN_RECEIVER_ID + " = ?) OR (" +
                          COLUMN_SENDER_ID + " = ? AND " + COLUMN_RECEIVER_ID + " = ?)";
        String[] selectionArgs = {userId1, userId2, userId2, userId1};
        
        Cursor cursor = db.query(TABLE_MESSAGES, null, selection, selectionArgs, 
                null, null, COLUMN_TIMESTAMP + " ASC");
        
        if (cursor != null && cursor.moveToFirst()) {
            do {
                messages.add(cursorToMessage(cursor));
            } while (cursor.moveToNext());
            cursor.close();
        }
        
        return messages;
    }

    private ChatMessage cursorToMessage(Cursor cursor) {
        ChatMessage message = new ChatMessage();
        message.setMessageId(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_MESSAGE_ID)));
        message.setSenderId(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SENDER_ID)));
        message.setReceiverId(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_RECEIVER_ID)));
        message.setMessageText(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_MESSAGE_TEXT)));
        message.setTimestamp(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TIMESTAMP)));
        return message;
    }

    // Medical Report CRUD operations
    public long insertMedicalReport(String patientId, String filePath) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        String reportId = "RPT_" + System.currentTimeMillis();
        
        values.put(COLUMN_REPORT_ID, reportId);
        values.put(COLUMN_PATIENT_ID, patientId);
        values.put(COLUMN_REPORT_FILE_PATH, filePath);

        long result = db.insert(TABLE_MEDICAL_REPORTS, null, values);
        Log.d(TAG, "Medical report inserted with ID: " + result);
        return result;
    }
}
