package com.example.blanche.mynews;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.test.InstrumentationRegistry;
import android.support.test.annotation.UiThreadTest;
import android.support.test.espresso.ViewAction;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.transition.Visibility;
import android.widget.EditText;

import com.example.blanche.mynews.controllers.activities.AboutActivity;
import com.example.blanche.mynews.controllers.activities.MainActivity;
import com.example.blanche.mynews.controllers.activities.SearchActivity;
import com.example.blanche.mynews.controllers.utils.ArticlesStreams;
import com.example.blanche.mynews.models.MostPopular.MostPopular;
import com.example.blanche.mynews.models.MostPopular.MostPopularResult;
import com.example.blanche.mynews.models.SearchArticles.SearchArticle;
import com.example.blanche.mynews.models.SearchArticles.SearchArticleMultimedium;
import com.example.blanche.mynews.models.SearchArticles.SearchArticleObject;
import com.example.blanche.mynews.models.SearchArticles.SearchArticleResponse;
import com.example.blanche.mynews.models.TopStories.TopStories;
import com.example.blanche.mynews.models.TopStories.TopStoriesMultimedia;
import com.example.blanche.mynews.models.TopStories.TopStoriesResult;

import static android.content.Context.MODE_PRIVATE;
import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.clearText;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.pressBack;
import static android.support.test.espresso.action.ViewActions.pressBackUnconditionally;
import static android.support.test.espresso.matcher.RootMatchers.withDecorView;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import io.reactivex.Observable;
import io.reactivex.observers.TestObserver;

import static android.support.test.internal.runner.junit4.statement.UiThreadStatement.runOnUiThread;
import static org.hamcrest.core.AllOf.allOf;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {

    private static final String APP_PREFERENCES = "appPreferences";
    private static final String KEYWORD_SEARCH = "keyword";
    private static final String CATEGORIES_SEARCH = "categories";
    private static final String SWITCH_BUTTON_STATE = "state";
    private static final String KEYWORD_NOTIFICATION = "keyword_notif";
    private static final String KEY_ACTIVITY = "key_activity";
    private static final String CATEGORIES_NOTIFICATION = "categories_notif";
    private static final String ARTS = "artsNotif";
    private static final String POLITICS = "politicsNotif";
    private static final String BUSINESS = "businessNotif";
    private static final String SPORTS = "sportsNotif";
    private static final String ENTREPRENEURS = "entrepreneursNotif";
    private static final String TRAVEL = "travelNotif";

    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();
        assertEquals("com.example.blanche.mynews", appContext.getPackageName());
    }

    //--------------------
    //SEARCHING ACTIIVITY
    //--------------------
    @Rule
    public ActivityTestRule<SearchActivity> activityTestRule = new ActivityTestRule<>(SearchActivity.class);

    @Mock
    private SharedPreferences preferences = mock(SharedPreferences.class);

    @Before
    public void init() throws Throwable {
        MockitoAnnotations.initMocks(this);
        Context targetContext = getInstrumentation().getTargetContext();
        preferences = targetContext.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        setPreferencesToNull();
    }

    //check if the user is able to write key words
    @Test
    public void userCanTypeTextInSearchingEditText() {
        onView(withId(R.id.edit_search)).perform(typeText("Politics"));
        onView(withId(R.id.edit_search)).perform(clearText());
    }

    // check if the toast message is displayed when key words are missing
    @Test
    public void validateFormAndKeyWordIsMissing() throws Throwable {
        preferences.edit().putInt(KEY_ACTIVITY, 0).apply();
        runOnUiThread(new Runnable() {
            public void run() {
                activityTestRule.getActivity().displayNotificationOrSearchScreen();
            }
        });
        //we clear
        onView(withId(R.id.edit_search)).perform(clearText());
        activityTestRule.getActivity().uncheckCheckBoxes();
        //check one box
        onView(withText("Arts")).perform(click());
        //click the button
        onView(withId(R.id.search_button)).perform(click());
        //we check if a toast message is displayed
        SearchActivity activity = activityTestRule.getActivity();
        onView(withText(R.string.toast_text_no_keyword_no_checked_category))
            .inRoot(withDecorView(not(is(activity.getWindow().getDecorView()))))
            .check(matches(isDisplayed()));
    }

    //check if toast message is displayed when there is no category checked
    @Test
    public void validateFormAndCheckedCategoryIsMissing() throws Throwable {
        preferences.edit().putInt(KEY_ACTIVITY, 0).apply();
        runOnUiThread(new Runnable() {
            public void run() {
                activityTestRule.getActivity().displayNotificationOrSearchScreen();
            }
        });
        //we clear the text and uncheck the boxes
        onView(withId(R.id.edit_search)).perform(clearText());
        activityTestRule.getActivity().uncheckCheckBoxes();
        //we type some words
        onView(withId(R.id.edit_search)).perform(typeText("Turkey"), closeSoftKeyboard());
        //we click on the search button
        onView(withId(R.id.search_button)).perform(click());
        //we check if the toast message is displayed
        onView(withText(R.string.toast_text_no_keyword_no_checked_category))
                .inRoot(withDecorView(not(is(activityTestRule.getActivity().getWindow().getDecorView()))))
                .check(matches(isDisplayed()));
    }

    //-------------------------------
    //DATA SAVING
    //------------------------------------
    @Test
    public void saveDataTest() throws Throwable {
        preferences.edit().putInt(KEY_ACTIVITY, 0).apply();
        runOnUiThread(new Runnable() {
            public void run() {
                activityTestRule.getActivity().displayNotificationOrSearchScreen();
            }
        });
        //try with one keyword and one category
        //uncheck the boxes and clear the text
        onView(withId(R.id.edit_search)).perform(clearText());
        activityTestRule.getActivity().uncheckCheckBoxes();
        //type some text and check one box
        onView(withId(R.id.edit_search)).perform(typeText("Something"), closeSoftKeyboard());
        onView(withText("Arts")).perform(click());

        activityTestRule.getActivity().saveData();

        assertEquals("Something", preferences.getString(KEYWORD_SEARCH, null));
        assertEquals('"' + "arts" + '"', preferences.getString(CATEGORIES_SEARCH, null));

        //try with two keywords and two categories
        setPreferencesToNull();
        onView(withId(R.id.edit_search)).perform(clearText());
        runOnUiThread(new Runnable() {
            public void run() {
                // Do stuff…
                activityTestRule.getActivity().uncheckCheckBoxes();
            }
        });

        onView(withId(R.id.edit_search)).perform(typeText("Something else"), closeSoftKeyboard());
        onView(withText("Arts")).perform(click());
        onView(withText("Business")).perform(click());
        activityTestRule.getActivity().saveData();

        assertEquals("Something else", preferences.getString(KEYWORD_SEARCH, null));
        assertEquals('"' + "arts" + '"' + " " + '"' + "business" + '"', preferences.getString(CATEGORIES_SEARCH, null));
    }

    //OPEN THE SEARCH
    @Test
    public void saveDataForNotificationTest() throws Throwable {
        preferences.edit().putInt(KEY_ACTIVITY, 1).apply();
        runOnUiThread(new Runnable() {
            public void run() {
                // Do stuff…
                activityTestRule.getActivity().displayNotificationOrSearchScreen();
            }
        });
        //we clear text and uncheck the boxes
        onView(withId(R.id.edit_search)).perform(clearText());
        activityTestRule.getActivity().uncheckCheckBoxes();
        //we type a new text and check a box
        onView(withId(R.id.edit_search)).perform(typeText("hello"), closeSoftKeyboard());
        onView(withText("Arts")).perform(click());
        //we click on the switch button
        onView(withId(R.id.switch_button)).perform(click());

        //if the switch button is active
        if (preferences.getInt(SWITCH_BUTTON_STATE, -1) == 0) {
            //activityTestRule.getActivity().saveDataForNotificationActivity(true);
            assertEquals(0, preferences.getInt(SWITCH_BUTTON_STATE, -1));
            assertEquals("hello", preferences.getString(KEYWORD_NOTIFICATION, null));
        } else {
            //activityTestRule.getActivity().saveDataForNotificationActivity(false);
            assertEquals(1, preferences.getInt(SWITCH_BUTTON_STATE, -1));
            assertNull(preferences.getString(KEYWORD_NOTIFICATION, null));
        }
    }
    @After
    public void after() throws Throwable {
        setPreferencesToNull();
        onView(withId(R.id.edit_search)).perform(clearText());
        runOnUiThread(new Runnable() {
                public void run() {
                    // Do stuff…
                    activityTestRule.getActivity().uncheckCheckBoxes(); }
            });

    }
    //------------------------------------
    //TESTS STREAM THAT FETCH THE TOP STORIES
    //-----------------------------------------
    //test that the number of onNext event is good
    @Test
    public void NumberOfOnNextEventReceivedForTopStoriesTest() throws Exception {
        //get the stream
        Observable<TopStories> observable = ArticlesStreams.streamFetchTopStoriesArticle("home");
        //create an observer
        TestObserver<TopStories> testObserver = new TestObserver<>();
        observable.subscribeWith(testObserver)
                .assertNoErrors()
                .assertNoTimeout()
                .awaitTerminalEvent();
        if (testObserver.values().size() != 0) {
            testObserver.assertValueCount(1);
        }
    }

    //test that the string section of the fetched article is the same
    @Test
    public void topStoriesReceivedObjectIsCorrectTest() throws Exception {
        Observable<TopStories> observable = ArticlesStreams.streamFetchTopStoriesArticle("arts");
        TestObserver<TopStories> testObserver = new TestObserver<>();
        observable.subscribeWith(testObserver)
                .assertNoErrors()
                .assertNoTimeout()
                .awaitTerminalEvent();
        if(testObserver.values().size() != 0) {
            TopStories topStories = testObserver.values().get(0);
            if (topStories.getResults().size() != 0) {
                TopStoriesResult result = topStories.getResults().get(0);
                assertEquals("Arts", result.getSection());
                assertNotNull(result.getTitle());
                assertNotNull(result.getUrl());
                assertEquals("OK", topStories.getStatus());
                if (result.getMultimedia().size() != 0) {
                    TopStoriesMultimedia multimedia = result.getMultimedia().get(0);
                    assertNotNull(multimedia.getUrl());
                }
            }
        }
    }

    //test in progress, must test that if we sent a wrong param, we need to throw an exception
    @Test
    public  void topStoriesWrongParamSentTest() throws Exception {
        Observable<TopStories> observable = ArticlesStreams.streamFetchTopStoriesArticle("cat");
        TestObserver<TopStories> testObserver = new TestObserver<>();
        observable.subscribeWith(testObserver);
        testObserver.assertNotComplete()
                .assertNoValues();
    }


    //------------------------------------
    //TESTS STREAM THAT FETCH THE MOST POPULAR ARTICLES
    //-----------------------------------------

    //test that the number of onNext event is good
    @Test
    public void NumberOfOnNextEventReceivedForMostPopularTest() throws Exception {
        //get the stream
        Observable<MostPopular> observable = ArticlesStreams.streamFetchMostPopularArticle(1);
        //create an observer
        TestObserver<MostPopular> testObserver = new TestObserver<>();
        observable.subscribeWith(testObserver)
                .assertNoErrors()
                .assertNoTimeout()
                .awaitTerminalEvent();
        if (testObserver.values().size() != 0) {
            testObserver.assertValueCount(1);
        }
    }

    //test that the title of the article is not null
    @Test
    public void mostPopularReceivedObjectIsCorrectTest() throws Exception {
        Observable<MostPopular> observable = ArticlesStreams.streamFetchMostPopularArticle(1);
        TestObserver<MostPopular> testObserver = new TestObserver<>();
        observable.subscribeWith(testObserver)
                .assertNoErrors()
                .assertNoTimeout()
                .awaitTerminalEvent();
        if(testObserver.values().size() != 0) {
            MostPopular mostPopular = testObserver.values().get(0);
            if(mostPopular.getMostPopularResults().size() != 0) {
                MostPopularResult mostPopularResult = mostPopular.getMostPopularResults().get(0);
                assertNotNull(mostPopularResult.getTitle());
                assertEquals("OK", mostPopular.getStatus());
                assertNotNull(mostPopularResult.getUrl());
                assertNotNull(mostPopularResult.getSection());
            }
        }
    }

    @Test
    public  void mostPopularWrongParamSentTest() throws Exception {
        Observable<MostPopular> observable = ArticlesStreams.streamFetchMostPopularArticle(0);
        TestObserver<MostPopular> testObserver = new TestObserver<>();
        observable.subscribeWith(testObserver);
        testObserver.assertNotComplete()
                .assertNoValues();
    }

    //-----------------------------
    //TESTS FOR THE SEARCH STREAM
    //-------------------------------

    //test that the number of onNext event is good
    @Test
    public void NumberOfOnNextEventReceivedForSearchTest() throws Exception {
        //get the stream
        Observable<SearchArticleObject> observable = ArticlesStreams.streamFetchSearchedArticle("20130102", "20140102", "business", "economic", "newest","TL8pNgjOXgnrDvkaCjdUI0N2AIvOGdyS");
        //create an observer
        TestObserver<SearchArticleObject> testObserver = new TestObserver<>();
        observable.subscribeWith(testObserver)
                .assertNoErrors()
                .assertNoTimeout()
                .awaitTerminalEvent();
        if (testObserver.values().size() != 0) {
            testObserver.assertValueCount(1);
        }
    }

    @Test
    public void searchArticleReceivedObjectIsCorrectTest() throws Exception {
        Observable<SearchArticleObject> observable = ArticlesStreams.streamFetchSearchedArticle("20130102", "20140102", "business", "economic", "newest", "TL8pNgjOXgnrDvkaCjdUI0N2AIvOGdyS");
        TestObserver<SearchArticleObject> testObserver = new TestObserver<>();
        observable.subscribeWith(testObserver)
                .assertNoErrors()
                .assertNoTimeout()
                .awaitTerminalEvent();
        if (testObserver.values().size() != 0) {
            if (testObserver.values().get(0) != null) {
                SearchArticleObject searchObject = testObserver.values().get(0);
                assertEquals("OK", searchObject.getStatus());
                SearchArticleResponse response = searchObject.getResponse();
                if (response.getArticles().size() != 0) {
                    SearchArticle article = response.getArticles().get(0);
                    assertNotNull(article.getHeadline().getMain());
                    assertNotNull(article.getWebUrl());
                    if (article.getMultimedia().size() != 0) {
                        SearchArticleMultimedium media = article.getMultimedia().get(0);
                        assertNotNull(media.getUrl());
                    }
                }
            }
        }
    }

    @Test
    public void searchWrongParamSentTest() throws Exception {
        Observable<SearchArticleObject> observable = ArticlesStreams.streamFetchSearchedArticle("", "", "business", "economic", "newest", "TL8pNgjOXgnrDvkaCjdUI0N2AIvOGdyS");
        TestObserver<SearchArticleObject> testObserver = new TestObserver<>();
        observable.subscribeWith(testObserver);
        testObserver.assertNotComplete()
                .assertNoValues();
    }

    //---------------------
    public void setPreferencesToNull() {
        preferences.edit().putString(ARTS, null).apply();
        preferences.edit().putString(POLITICS, null).apply();
        preferences.edit().putString(BUSINESS, null).apply();
        preferences.edit().putString(SPORTS, null).apply();
        preferences.edit().putString(ENTREPRENEURS, null).apply();
        preferences.edit().putString(TRAVEL, null).apply();
        preferences.edit().putString(KEYWORD_SEARCH, null).apply();
        preferences.edit().putString(KEYWORD_NOTIFICATION, null).apply();
        preferences.edit().putString(CATEGORIES_SEARCH, null).apply();
        preferences.edit().putString(CATEGORIES_NOTIFICATION, null).apply();
    }
}
