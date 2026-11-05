package com.example.campusmarket

import com.example.campusmarket.offline.OfflineModePolicy
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class OfflineModePolicyTest {
    @Test fun `uses saved catalogue after network failure`() {
        assertTrue(OfflineModePolicy.canUseCache(networkRequestFailed = true, cachedItemCount = 12))
    }

    @Test fun `does not claim offline support when cache is empty`() {
        assertFalse(OfflineModePolicy.canUseCache(networkRequestFailed = true, cachedItemCount = 0))
    }

    @Test fun `prefers fresh response while network works`() {
        assertFalse(OfflineModePolicy.canUseCache(networkRequestFailed = false, cachedItemCount = 12))
    }
}
