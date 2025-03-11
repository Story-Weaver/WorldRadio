package by.roman.worldradio2;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.lang.ref.WeakReference;

import by.roman.worldradio2.data.repository.DatabaseHelper;
import by.roman.worldradio2.data.repository.RadioStationRepository;
import by.roman.worldradio2.ui.activities.MainActivity;

public class RadioService {
    private final Context context;
    private final RadioManager radioManager;
    private final DatabaseHelper dbHelper;
    private final Handler handler;
    private String currentStreamUrl = null;
    private final SQLiteDatabase db;
    private final RadioStationRepository radioStationRepository;
    private static RadioService instance;
    private boolean isMonitoring = false;
    private WeakReference<MainActivity> mainActivityRef;

    public static synchronized RadioService getInstance(Context context, MainActivity activity) {
        if (instance == null) {
            instance = new RadioService(context, activity);
        }
        return instance;
    }

    private RadioService(Context context, MainActivity activity) {
        this.context = context.getApplicationContext();
        this.mainActivityRef = new WeakReference<>(activity);  // Используем WeakReference, чтобы избежать утечек памяти
        this.radioManager = new RadioManager(context);
        this.dbHelper = new DatabaseHelper(context);
        this.handler = new Handler(Looper.getMainLooper());
        this.db = dbHelper.getWritableDatabase();
        this.radioStationRepository = new RadioStationRepository(db);
    }

    private void updateUI() {
        MainActivity mainActivity = mainActivityRef.get();
        if (mainActivity != null) {
            // Безопасно обновляем UI без риска утечек памяти
            mainActivity = mainActivityRef.get();
            if (mainActivity != null) {
                mainActivity.showBottomPlayerFragment(radioStationRepository.getActiveStation());
            }
        }
    }
    public void startMonitoring() {
        if (!isMonitoring) {
            isMonitoring = true;
            handler.post(checkDatabaseRunnable);
        }
    }

    public void stopMonitoring() {
        isMonitoring = false;
        handler.removeCallbacks(checkDatabaseRunnable);
        radioManager.stop();
    }
    public void checkNow() {
        handler.post(checkDatabaseRunnable);
    }
    public void play(){
        radioManager.play(currentStreamUrl);
    }
    public void pause(){
        radioManager.stop();
    }
    private final Runnable checkDatabaseRunnable = new Runnable() {
        @Override
        public void run() {
            String newStreamUrl = radioStationRepository.getActiveStationUrl();

            if (newStreamUrl != null && !newStreamUrl.equals(currentStreamUrl)) {
                Log.d("RadioService", "Новая станция: " + newStreamUrl);
                currentStreamUrl = newStreamUrl;
                radioManager.stop();
                radioManager.play(currentStreamUrl);
                updateUI();
            } else {
                radioManager.stop();
                MainActivity mainActivity = mainActivityRef.get();
                if (mainActivity != null) {
                    mainActivity.hideBottomPlayerFragment();
                }
                currentStreamUrl = null;
            }
        }
    };
}
