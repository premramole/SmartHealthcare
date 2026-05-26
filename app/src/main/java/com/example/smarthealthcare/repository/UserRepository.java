package com.example.smarthealthcare.repository;

import android.app.Application;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.smarthealthcare.database.AppDatabase;
import com.example.smarthealthcare.database.dao.UserDao;
import com.example.smarthealthcare.database.entities.UserEntity;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class UserRepository {
    
    private UserDao userDao;
    private LiveData<List<UserEntity>> allUsers;
    private LiveData<List<UserEntity>> allDoctors;
    private MutableLiveData<UserEntity> currentUser = new MutableLiveData<>();
    
    private ExecutorService executorService = Executors.newSingleThreadExecutor();
    
    public UserRepository(Application application) {
        AppDatabase database = AppDatabase.getDatabase(application);
        userDao = database.userDao();
        allUsers = userDao.getAllUsers();
        allDoctors = userDao.getAllDoctors();
    }
    
    public LiveData<List<UserEntity>> getAllUsers() {
        return allUsers;
    }
    
    public LiveData<List<UserEntity>> getAllDoctors() {
        return allDoctors;
    }
    
    public LiveData<List<UserEntity>> getUsersByRole(String role) {
        return userDao.getUsersByRole(role);
    }
    
    public MutableLiveData<UserEntity> getCurrentUser() {
        return currentUser;
    }
    
    public void insertUser(UserEntity user) {
        executorService.execute(() -> {
            userDao.insertUser(user);
        });
    }
    
    public void updateUser(UserEntity user) {
        executorService.execute(() -> {
            userDao.updateUser(user);
        });
    }
    
    public void deleteUser(String userId) {
        executorService.execute(() -> {
            userDao.deleteUser(userId);
        });
    }
    
    public UserEntity login(String email, String password) {
        try {
            return userDao.login(email, password);
        } catch (Exception e) {
            return null;
        }
    }
    
    public UserEntity getUserByEmail(String email) {
        try {
            return userDao.getUserByEmail(email);
        } catch (Exception e) {
            return null;
        }
    }
    
    public UserEntity getUserById(String userId) {
        try {
            return userDao.getUserById(userId);
        } catch (Exception e) {
            return null;
        }
    }
    
    public void setCurrentUser(UserEntity user) {
        currentUser.setValue(user);
    }
}
