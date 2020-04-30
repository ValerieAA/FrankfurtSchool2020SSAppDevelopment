package de.fs.fintech.geogame;

import android.app.Instrumentation;
import android.content.Context;

import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4ClassRunner.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        Instrumentation instrumentation = InstrumentationRegistry.getInstrumentation();
        Context appContext = instrumentation.getContext();

        assertEquals("de.fs.fintech.geogame", appContext.getPackageName());
    }
}
