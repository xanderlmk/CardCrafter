package com.belmontCrest.cardCrafter.supabase.model.daoAndRepository.repositories

import android.util.Log
import com.belmontCrest.cardCrafter.BuildConfig
import com.belmontCrest.cardCrafter.supabase.model.tables.SBDeckDto
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.annotations.SupabaseExperimental
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.filter.FilterOperation
import io.github.jan.supabase.postgrest.query.filter.FilterOperator
import io.github.jan.supabase.realtime.selectAsFlow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.withContext

interface UserExportedDecksRepository {
    suspend fun userExportedDecks(): Flow<List<SBDeckDto>>
}

@OptIn(SupabaseExperimental::class)
class UserExportDecksRepositoryImpl(
    private val sharedSupabase: SupabaseClient
) : UserExportedDecksRepository {
    override suspend fun userExportedDecks(): Flow<List<SBDeckDto>> {
        return withContext(Dispatchers.IO) {
            try {
                val userInfo = sharedSupabase.auth.currentUserOrNull()
                if (userInfo == null) {
                    throw IllegalAccessException("User is not authenticated.")
                }
                val result = sharedSupabase.from(SBDeckTN)
                    .selectAsFlow(
                        SBDeckDto::deckUUID,
                        filter = FilterOperation(
                            column = "user_id",
                            operator = FilterOperator.EQ,
                            value = userInfo.id
                        )
                    )
                result
            } catch (e: Exception) {
                Log.e("userExportedDecks", "Something went wrong: $e")
                flowOf(emptyList())
            }
        }
    }
}

private const val SBDeckTN = BuildConfig.SB_DECK_TN