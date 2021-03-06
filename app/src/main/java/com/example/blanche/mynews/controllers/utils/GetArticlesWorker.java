package com.example.blanche.mynews.controllers.utils;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import com.example.blanche.mynews.R;
import com.example.blanche.mynews.models.SearchArticles.SearchArticleObject;
import com.example.blanche.mynews.models.SearchArticles.SearchArticleResponse;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;
import static android.content.Context.MODE_PRIVATE;

/**
 * in this class we have the work that will be executed when the notifications are "on"
 * the work is in the method doWork()
 * we go check articles found for the criterias entered by the user and inform the user once a day how many articles are found
 * by sending a notification
 */
public class GetArticlesWorker extends Worker {
    public static final String APP_PREFERENCES = "appPreferences";
    public static final String IS_THE_FIRST_NOTIFICATION = "notification";
    public static final String CATEGORIES_WORKER = "categories";
    public static final String KEYWORD_WORKER= "keyword";
    private int size;

    public GetArticlesWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        SharedPreferences preferences = getApplicationContext().getSharedPreferences(APP_PREFERENCES, MODE_PRIVATE);
        //get the strings that will be passed as param
        String isTheFirstNotification = preferences.getString(IS_THE_FIRST_NOTIFICATION, null);
        String categories = getInputData().getString(CATEGORIES_WORKER);
        String dataKeyword = getInputData().getString(KEYWORD_WORKER);
        String keyword = "headline:(\""+ dataKeyword +"\")";

        //first notification is immediate, but we want it only once a day and not immediately, so won't send the first notification
        //if it's not the first time, we can send notification
        if (isTheFirstNotification.equals("false")) {
            executeHttpRequest(null, null, categories, keyword);
        }
        preferences.edit().putString(IS_THE_FIRST_NOTIFICATION, "false").apply();
        return Result.success();
    }

    private void sendNotification(String title, String message) {
            NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

            //If on Oreo then notification required a notification channel.
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel("default", "Default", NotificationManager.IMPORTANCE_DEFAULT);
                notificationManager.createNotificationChannel(channel);
            }

            NotificationCompat.Builder notification = new NotificationCompat.Builder(getApplicationContext(), "default")
                    .setContentTitle(title)
                    .setContentText(message)
                    .setSmallIcon(R.drawable.img_newspaper)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);

            notificationManager.notify(1, notification.build());
        System.out.println("WE SENT NOTIFICATION NOW");
    }

    public void executeHttpRequest(String beginDate, String endDate, String category, String keyword) {
        Disposable disposable = ArticlesStreams.streamFetchSearchedArticle(beginDate, endDate, category, keyword, "newest", "TL8pNgjOXgnrDvkaCjdUI0N2AIvOGdyS")
                .subscribeWith(new DisposableObserver<SearchArticleObject>() {

                    @Override
                    public void onNext(SearchArticleObject searchArticleObject) {
                        size = 0;
                        Log.e("TAG", "on next");
                        SearchArticleResponse searchArticleResponse = searchArticleObject.getResponse();
                        size = searchArticleResponse.getArticles().size();
                        displayTextNotificationDependingOnResults(size);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e("TAG", Log.getStackTraceString(e));
                    }

                    @Override
                    public void onComplete() {
                        Log.e("TAG", "on complete");
                    }
                });
    }

    /**
     * displays the good texts for notification, depending on the results found
     * @param size
     */
    private void displayTextNotificationDependingOnResults(int size) {
        if (size > 0) {
            String sizeStr = Integer.toString(size);
            sendNotification("Good news!", "We have found " + sizeStr + " articles for the filters you chose! :)");
        } else {
            sendNotification("Sorry...", "No articles found for the filters you chose! :(");
        }
    }
}
