package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.util.SecurityUtils
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("وكالة شجين", appName)
  }

  @Test
  fun `verify password hashing and verification`() {
    val plainPassword = "admin123"
    val hash = SecurityUtils.hashPassword(plainPassword)
    assertTrue(SecurityUtils.verifyPassword("admin123", hash))
    org.junit.Assert.assertFalse(SecurityUtils.verifyPassword("wrongpass", hash))
  }
}
