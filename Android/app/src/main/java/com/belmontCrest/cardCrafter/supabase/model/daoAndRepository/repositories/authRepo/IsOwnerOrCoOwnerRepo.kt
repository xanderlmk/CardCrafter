package com.belmontCrest.cardCrafter.supabase.model.daoAndRepository.repositories.authRepo

import com.belmontCrest.cardCrafter.BuildConfig
import com.belmontCrest.cardCrafter.supabase.model.tables.SBDeckCoOwnerDto
import com.belmontCrest.cardCrafter.supabase.model.tables.SBDeckOwnerDto
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns

interface IsOwnerOrCoOwnerRepo {
    suspend fun isCoOwnerOrCoOwner(uuid: String): Boolean
}

class IsOwnerOrCoOwnerRepoImpl(
    private val sharedSupabase: SupabaseClient
) : IsOwnerOrCoOwnerRepo {
    companion object {
        private const val SB_DECK_TN = BuildConfig.SB_DECK_TN
        private const val SB_DACO_TN = BuildConfig.SB_DACO_TN
    }

    override suspend fun isCoOwnerOrCoOwner(uuid: String) = userCanEdit(uuid)

    private suspend fun userCanEdit(deckUUID: String): Boolean {

        val userId = sharedSupabase.auth.currentUserOrNull()?.id

        if (userId == null) {
            return false
        }
        /** Is the user the deck owner? */
        val isOwner = sharedSupabase.from(SB_DECK_TN)
            .select(Columns.type<SBDeckOwnerDto>()) {
                filter {
                    eq("deckUUID", deckUUID)
                    eq("user_id", userId)
                }
            }
            .decodeSingleOrNull<SBDeckOwnerDto>() != null

        if (isOwner) return true

        /** Is the user an accepted co-owner? */
        val isCoOwner = sharedSupabase.from(SB_DACO_TN)
            .select(Columns.raw("co_owner_id")) {
                filter {
                    eq("deckUUID", deckUUID)
                    eq("co_owner_id", userId)
                    eq("status", "accepted")
                }
            }
            .decodeSingleOrNull<SBDeckCoOwnerDto>() != null

        return isCoOwner
    }
}