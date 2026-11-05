package com.example.campusmarket.offline

/** Pure decision logic kept separate so offline fallback behaviour is unit-testable. */
object OfflineModePolicy {
    fun canUseCache(networkRequestFailed: Boolean, cachedItemCount: Int): Boolean =
        networkRequestFailed && cachedItemCount > 0
}
