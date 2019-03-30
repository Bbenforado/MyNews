package com.example.blanche.mynews.controllers.utils;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.example.blanche.mynews.R;
import com.example.blanche.mynews.models.SearchArticles.SearchArticleObject;
import com.example.blanche.mynews.models.SearchArticles.SearchArticleResponse;

import androidx.work.OneTimeWorkRequest;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;

public class GetArticlesWorker extends Worker {

    public static final String CATEGORIES_WORKER = "categories";
    public static final String KEYWORD_WORKER= "keyword";
    Disposable disposable;
    int size;


    public GetArticlesWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {

        String data = getInputData().getString(CATEGORIES_WORKER);
        String keyword = getInputData().getString(KEYWORD_WORKER);
        //do the work here
        executeHttpRequestSearchArticle(data, keyword);

        return Result.success();
    }

    public void sendNotification(String title, String message) {
            NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

            //If on Oreo then notification required a notification channel.
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel("default", "Default", NotificationManager.IMPORTANCE_DEFAULT);
                notificationManager.createNotificationChannel(channel);
            }

            NotificationCompat.Builder notification = new NotificationCompat.Builder(getApplicationContext(), "default")
                    .setContentTitle(title)
                    .setContentText(message)
                    .setSmallIcon(R.drawable.img_newspaper);

            notificationManager.notify(1, notification.build());
        System.out.println("WE SENT NOTIFICATION NOW");
    }

    public void executeHttpRequestSearchArticle(String category, String keyword) {
        this.disposable =
                ArticlesStreams.streamFetchSearchedArticleByCategoryAndKeyWord(category, keyword, "newest", "TL8pNgjOXgnrDvkaCjdUI0N2AIvOGdyS")
                        .subscribeWith(new DisposableObserver<SearchArticleObject>() {

                            @Override
                            public void onNext(SearchArticleObject searchArticleObject) {
                                size = 0;
                                Log.e("TAG", "on next");
                                SearchArticleResponse searchArticleResponse = searchArticleObject.getResponse();
                                size = searchArticleResponse.getArticles().size();
                                String sizeStr = Integer.toString(size);
                                if (size > 0) {
                                    sendNotification("Title", "We have found " + sizeStr + " articles for the filters you chose! :)");
                                } else {
                                    sendNotification("Title", "No articles found for the filters you chose! :(");
                                }
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


}
