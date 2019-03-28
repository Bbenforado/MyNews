package com.example.blanche.mynews;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.Menu;

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
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.matcher.RootMatchers.withDecorView;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import io.reactivex.Observable;
import io.reactivex.observers.TestObserver;
import static org.hamcrest.core.AllOf.allOf;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.*;


/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {

    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("com.example.blanche.mynews", appContext.getPackageName());
    }

    //-------------------
    //MAIN ACTIVITY   NOT WORKING ANYMORE HAVE TO WORK ON THEM, PROBLEM IS RECYCLER VIEW + VIEW PAGER
    //-------------------

    @Rule
    public ActivityTestRule<MainActivity> mActivityRule = new ActivityTestRule<>(MainActivity.class);


    // check if the search button display the searching activity
 //   @Test
 //   public void launchSearchActivity() {
        //on récupère le bouton search et on clique dessus
//        onView(withId(R.id.menu_main_search)).perform(click());
//        onView(withId(R.id.edit_search)).check(matches(isDisplayed()));
//        pressBack();
//        onView(withId(R.id.viewpager)).check(matches(isDisplayed()));
//    }

    //check if the about button in the toolbar display the about activity
//    @Test
//    public void LaunchAboutActivity() {
//        openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());
//        onView(withText("About")).perform(click());
//        onView(withId(R.id.main_text)).check(matches(isDisplayed()));
//        pressBack();
//        onView(withId(R.id.viewpager)).check(matches(isDisplayed()));
//    }

    //check if the notifications button in the toolbar display the notifications activity
 //   @Test
 //   public void LaunchNotificationsActivity() {
 //       openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());
 //       onView(withText("Notifications")).perform(click());
 //       onView(withId(R.id.categories)).check(matches(isDisplayed()));
 //       pressBack();
 //       onView(withId(R.id.viewpager)).check(matches(isDisplayed()));
 //   }

    //check that the navigation drawer opens and closes
 //   @Test
 //   public void openAndCloseTheDrawer() {
 //       onView(withContentDescription(R.string.navigation_drawer_open)).perform(click());
 //       onView(withId(R.id.drawer_layout)).check(matches(isDisplayed()));
 //       pressBack();
 //       onView(withId(R.id.viewpager)).check(matches(isDisplayed()));
 //   }


    //--------------------
    //SEARCHING ACTIIVITY
    //--------------------
    @Rule
    public ActivityTestRule<SearchActivity> activityTestRule = new ActivityTestRule<>(SearchActivity.class);

    //check that the user is able to write key words
    @Test
    public void userCanTypeTextInSearchingEditText() {
        onView(withId(R.id.edit_search)).perform(typeText("Politics"));
    }

    // check if the toast message is displayed if key words are missing
    @Test
    public void validateFormAndKeyWordIsMissing() {
        onView(allOf(withText("Arts"))).perform(click());
        onView(withId(R.id.search_button)).perform(click());
        //vérifier qu'un toast est affiché
        SearchActivity activity = activityTestRule.getActivity();
        onView(withText(R.string.toast_text_missing_keyword))
            .inRoot(withDecorView(not(is(activity.getWindow().getDecorView()))))
            .check(matches(isDisplayed()));
    }

    //check if toast message is displayed if there is no category checked
    @Test
    public void validateFormAndCheckedCategoryIsMissing() {
        onView(withId(R.id.edit_search)).perform(typeText("Turkey"), closeSoftKeyboard());
        onView(withId(R.id.search_button)).perform(click());

        SearchActivity activity = activityTestRule.getActivity();
        onView(withText(R.string.toast_text_checked_category_missing))
                .inRoot(withDecorView(not(is(activity.getWindow().getDecorView()))))
                .check(matches(isDisplayed()));
    }

    //------------------------------------
    //TESTS STREAM THAT FETCH THE TOP STORIES
    //-----------------------------------------

    //test that the number of onNext event is good
    @Test
    public void NumberOfOnNextEventReceivesTest() throws Exception {
        //get the stream
        Observable<TopStories> observable = ArticlesStreams.streamFetchTopStoriesArticle("home");
        //create an observer
        TestObserver<TopStories> testObserver = new TestObserver<>();
        observable.subscribeWith(testObserver)
                .assertNoErrors()
                .assertNoTimeout()
                .awaitTerminalEvent();
        testObserver.assertValueCount(1);
    }

    //test that the string section of the fetched article is the same
    @Test
    public void topStoriesArticleHasStringSectionTest() throws Exception {
        Observable<TopStories> observable = ArticlesStreams.streamFetchTopStoriesArticle("arts");
        TestObserver<TopStories> testObserver = new TestObserver<>();
        observable.subscribeWith(testObserver)
                .assertNoErrors()
                .assertNoTimeout()
                .awaitTerminalEvent();
        TopStories topStories = testObserver.values().get(0);
        TopStoriesResult result = topStories.getResults().get(0);
        assertEquals("Arts", result.getSection());
    }

    //test that the title of the fetched article is not null
    @Test
    public void topStoriesArticleTitleIsNotNullTest() throws Exception {
        Observable<TopStories> observable = ArticlesStreams.streamFetchTopStoriesArticle("home");
        TestObserver<TopStories> testObserver = new TestObserver<>();
        observable.subscribeWith(testObserver)
                .assertNoErrors()
                .assertNoTimeout()
                .awaitTerminalEvent();
        if(testObserver.values().size() != 0) {
            TopStories topStories = testObserver.values().get(0);
            if(topStories.getResults().size() != 0) {
                TopStoriesResult result = topStories.getResults().get(0);
                assertNotNull(result.getTitle());
            }
        }
    }

    //test that the article fetched url is not null
    @Test
    public void topStoriesArticleUrlIsNotNullTest() throws Exception {
        Observable<TopStories> observable = ArticlesStreams.streamFetchTopStoriesArticle("home");
        TestObserver<TopStories> testObserver = new TestObserver<>();
        observable.subscribeWith(testObserver)
                .assertNoErrors()
                .assertNoTimeout()
                .awaitTerminalEvent();
        TopStories topStories = testObserver.values().get(0);
        TopStoriesResult result = topStories.getResults().get(0);
        assertNotNull(result.getUrl());
    }

    //test that the status of the object retrieved in onNext is OK
    @Test
    public void topStoriesStatusIsOkTest() throws Exception {
        Observable<TopStories> observable = ArticlesStreams.streamFetchTopStoriesArticle("home");
        TestObserver<TopStories> testObserver = new TestObserver<>();
        observable.subscribeWith(testObserver)
                .assertNoErrors()
                .assertNoTimeout()
                .awaitTerminalEvent();
        if(testObserver.values().size() != 0) {
            TopStories topStories = testObserver.values().get(0);
            assertEquals("OK", topStories.getStatus());
        }
    }

    //test that the multimedia url is not null
    @Test
    public void topStoriesArticleMultimediaTest() throws Exception {
        Observable<TopStories> observable = ArticlesStreams.streamFetchTopStoriesArticle("home");
        TestObserver<TopStories> testObserver = new TestObserver<>();
        observable.subscribeWith(testObserver)
                .assertNoErrors()
                .assertNoTimeout()
                .awaitTerminalEvent();
        if(testObserver.values().size() != 0) {
            TopStories topStories = testObserver.values().get(0);
            if (topStories != null) {
                TopStoriesResult result = topStories.getResults().get(0);

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

    //test that the title of the article is not null
    @Test
    public void mostPopularArticleTitleIsNotNullTest() throws Exception {
        Observable<MostPopular> observable = ArticlesStreams.streamFetchMostPopularArticle(1);
        TestObserver<MostPopular> testObserver = new TestObserver<>();
        observable.subscribeWith(testObserver)
                .assertNoErrors()
                .assertNoTimeout()
                .awaitTerminalEvent();
        if(testObserver.values().size() != 0) {
            MostPopular mostPopular = testObserver.values().get(0);
            MostPopularResult mostPopularResult = mostPopular.getMostPopularResults().get(0);
            assertNotNull(mostPopularResult.getTitle());
        }
    }

    //test that the status of the object retrieved in onNext is OK
    @Test
    public void mostPopularStatusIsOkTest() {
        Observable<MostPopular> observable = ArticlesStreams.streamFetchMostPopularArticle(1);
        TestObserver<MostPopular> testObserver = new TestObserver<>();
        observable.subscribeWith(testObserver)
                .assertNoErrors()
                .assertNoTimeout()
                .awaitTerminalEvent();
        if(testObserver.values().size() != 0) {
            MostPopular mostPopular = testObserver.values().get(0);
            assertEquals("OK", mostPopular.getStatus());
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

    @Test
    public void mostPopularArticleUrlIsNotNullTest() throws Exception {
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
                assertNotNull(mostPopularResult.getUrl());
            }
        }
    }

    @Test
    public void mostPopularArticleSectionIsNotNull() throws Exception {
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
                assertNotNull(mostPopularResult.getSection());
            }
        }
    }

    //-----------------------------
    //TESTS FOR THE SEARCH STREAM
    //-------------------------------
    @Test
    public void searchArticleStatusIsOkTest() throws Exception {
        Observable<SearchArticleObject> observable = ArticlesStreams.streamFetchSearchedArticle("20130102", "20140102", "business", "economic", "newest", "TL8pNgjOXgnrDvkaCjdUI0N2AIvOGdyS");
        TestObserver<SearchArticleObject> testObserver = new TestObserver<>();
        observable.subscribeWith(testObserver)
                .assertNoErrors()
                .assertNoTimeout()
                .awaitTerminalEvent();
        SearchArticleObject searchObject = testObserver.values().get(0);
        assertEquals("OK", searchObject.getStatus());
    }

    @Test
    public void searchArticleTitleIsNotNullTest() throws Exception {
        Observable<SearchArticleObject> observable = ArticlesStreams.streamFetchSearchedArticle("20180102", "20190102", "sports", "football", "newest", "TL8pNgjOXgnrDvkaCjdUI0N2AIvOGdyS");
        TestObserver<SearchArticleObject> testObserver = new TestObserver<>();
        observable.subscribeWith(testObserver)
                .assertNoErrors()
                .assertNoTimeout()
                .awaitTerminalEvent();
        if(testObserver.values().get(0) != null) {
            SearchArticleObject search = testObserver.values().get(0);
            SearchArticleResponse response = search.getResponse();
            SearchArticle article = response.getArticles().get(0);
            assertNotNull(article.getHeadline().getMain());
        }
    }

    @Test
    public void searchArticleUrlIsNotNullTest() throws Exception {
        Observable<SearchArticleObject> observable = ArticlesStreams.streamFetchSearchedArticle("20130102", "20140102", "business", "economic", "newest", "TL8pNgjOXgnrDvkaCjdUI0N2AIvOGdyS");
        TestObserver<SearchArticleObject> testObserver = new TestObserver<>();
        observable.subscribeWith(testObserver)
                .assertNoErrors()
                .assertNoTimeout()
                .awaitTerminalEvent();
        if(testObserver.values().get(0) != null) {
            SearchArticleObject search = testObserver.values().get(0);
            SearchArticleResponse response = search.getResponse();
            SearchArticle article = response.getArticles().get(0);
            assertNotNull(article.getWebUrl());
        }
    }

    @Test
    public void searchArticleMultimediaUrlIsNotNullTest() throws Exception {
        Observable<SearchArticleObject> observable = ArticlesStreams.streamFetchSearchedArticle("20130102", "20140102", "business", "economic", "newest", "TL8pNgjOXgnrDvkaCjdUI0N2AIvOGdyS");
        TestObserver<SearchArticleObject> testObserver = new TestObserver<>();
        observable.subscribeWith(testObserver)
                .assertNoErrors()
                .assertNoTimeout()
                .awaitTerminalEvent();
        if(testObserver.values().get(0) != null) {
            SearchArticleObject search = testObserver.values().get(0);
            SearchArticleResponse response = search.getResponse();
            if(response.getArticles().size() != 0) {
                SearchArticle article = response.getArticles().get(0);
                if(article.getMultimedia().size() != 0) {
                    SearchArticleMultimedium media = article.getMultimedia().get(0);
                    assertNotNull(media.getUrl());
                }
            }
        }
    }

    @Test
    public void testSearch() throws Exception {
        Observable<SearchArticleObject> observable = ArticlesStreams.streamFetchSearchedArticle("", "", "business", "economic", "newest", "TL8pNgjOXgnrDvkaCjdUI0N2AIvOGdyS");
        TestObserver<SearchArticleObject> testObserver = new TestObserver<>();
        observable.subscribeWith(testObserver);
        testObserver.assertNotComplete()
                .assertNoValues();
    }

}
