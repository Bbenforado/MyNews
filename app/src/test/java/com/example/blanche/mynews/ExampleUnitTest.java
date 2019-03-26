package com.example.blanche.mynews;

import android.widget.Button;

import com.example.blanche.mynews.controllers.activities.SearchActivity;

import org.junit.Test;

import java.util.Calendar;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void addZeroBehindOneNumber() {
        SearchActivity searchActivity = new SearchActivity();
        String string = "5";
        string = searchActivity.addZeroToDate(string);
        assertTrue(string.equals("05"));
    }

    @Test
    public void dontAddZeroBehindNumber() {
        SearchActivity searchActivity = new SearchActivity();
        String str = "52";
        str = searchActivity.addZeroToDate(str);
        assertFalse(str.equals("052"));
    }

    @Test
    public void getCurrentDateTest() {
        SearchActivity searchActivity = new SearchActivity();
        String currentDate = searchActivity.getCurrentDate();
        assertNotNull(currentDate);
    }

}