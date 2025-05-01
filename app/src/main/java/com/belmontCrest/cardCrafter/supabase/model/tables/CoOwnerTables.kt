package com.belmontCrest.cardCrafter.supabase.model.tables

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SBCoOwner(
    val deckUUID: String,
    @SerialName("co_owner_id")
    val coOwnerId: String,
    val status: Status = Status.Pending,
    @SerialName("invited_by")
    val invitedBy: String,
    @SerialName("created_at")
    val createdAt: String = "",
    @SerialName("updated_on")
    val updatedOn: String = ""
)

@Serializable
enum class Status {
    @SerialName("pending") Pending,
    @SerialName("accepted") Accepted,
    @SerialName("declined") Declined,
    @SerialName("revoked") Revoked,
    @SerialName("cancelled") Cancelled,
    @SerialName("expired") Expired
}

@Serializable
data class SBOwnerName(
    val username: String,
    @SerialName("f_name") val first: String,
    @SerialName("l_name") val last:  String
)
@Serializable
data class CoOwnerWithUsername(
    val deckUUID: String,
    val status: Status,
    @SerialName("inviter")
    val invitedBy: SBOwnerName,
    @SerialName("co_owner")
    val coOwner: SBOwnerName
)

@Serializable
data class SBCoOwnerOf(
    val cof : List<SBCoOwnerWithDeck> = listOf()
)


@Serializable
data class SBCoOwnerWithDeck(
    val deckUUID: String,
    val deckName: String,
    val description: String,
    @SerialName("co_owner_id")
    val coOwnerId: String,
    val status: Status = Status.Pending,
    @SerialName("invited_by")
    val invitedBy: String,
    @SerialName("created_at")
    val createdAt: String = "",
    @SerialName("updated_on")
    val updatedOn: String = ""
)