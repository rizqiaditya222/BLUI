package com.kotlin.blui.utils

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

object IconMapper {
    /**
     * Convert category icon string (emoji or name) to ImageVector
     */
    fun getIconForCategory(iconString: String): ImageVector {
        return when (iconString) {
            // Food & Drink
            "🍔", "🍕", "🍜", "🍝", "🍱", "🍣", "🍛" -> Icons.Default.Restaurant
            "☕", "🍵", "🥤" -> Icons.Default.LocalCafe

            // Transportation
            "🚗", "🚙", "🚕" -> Icons.Default.DirectionsCar
            "🚌", "🚎" -> Icons.Default.DirectionsBus
            "✈️", "🛫" -> Icons.Default.Flight
            "🚇", "🚆", "🚄" -> Icons.Default.Train

            // Shopping
            "🛒", "🛍️" -> Icons.Default.ShoppingCart
            "🏪", "🏬" -> Icons.Default.Store

            // Entertainment
            "🎮", "🎯", "🎲" -> Icons.Default.SportsEsports
            "🎬", "🎥", "📽️" -> Icons.Default.Movie
            "🎵", "🎶", "🎧" -> Icons.Default.MusicNote

            // Health & Fitness
            "🏥", "⚕️" -> Icons.Default.LocalHospital
            "💊" -> Icons.Default.MedicalServices
            "💪", "🏃" -> Icons.Default.FitnessCenter

            // Home & Utilities
            "🏠", "🏡" -> Icons.Default.Home
            "💡", "⚡" -> Icons.Default.Lightbulb
            "💧", "🚿" -> Icons.Default.WaterDrop

            // Work & Education
            "💼", "👔" -> Icons.Default.Work
            "📚", "✏️", "📝" -> Icons.Default.School

            // Money & Finance
            "💰", "💵", "💸" -> Icons.Default.AttachMoney
            "💳" -> Icons.Default.CreditCard
            "🏦" -> Icons.Default.AccountBalance

            // Communication
            "📱", "☎️" -> Icons.Default.Phone
            "📧", "✉️" -> Icons.Default.Email
            "💬" -> Icons.Default.Chat

            // Others
            "🎁" -> Icons.Default.CardGiftcard
            "✈️" -> Icons.Default.FlightTakeoff
            "🏨" -> Icons.Default.Hotel
            "⛽" -> Icons.Default.LocalGasStation

            // Default fallback
            else -> Icons.Default.Category
        }
    }

    /**
     * Map icon string name to ImageVector
     * Used for category templates and stored category icons
     */
    fun mapIcon(iconName: String): ImageVector {
        return when (iconName.lowercase()) {
            "restaurant", "makanan" -> Icons.Default.Restaurant
            "directionscar", "transport", "car" -> Icons.Default.DirectionsCar
            "shoppingcart", "belanja", "shopping" -> Icons.Default.ShoppingCart
            "home", "rumah" -> Icons.Default.Home
            "localhospital", "kesehatan", "health" -> Icons.Default.LocalHospital
            "school", "pendidikan", "education" -> Icons.Default.School
            "sports", "olahraga" -> Icons.Default.Sports
            "electricalservices", "listrik", "electrical" -> Icons.Default.ElectricalServices
            "checkroom", "pakaian", "clothing" -> Icons.Default.Checkroom
            "localgasstation", "bensin", "gas" -> Icons.Default.LocalGasStation
            "localcafe", "cafe" -> Icons.Default.LocalCafe
            "directionsbus", "bus" -> Icons.Default.DirectionsBus
            "flight", "plane" -> Icons.Default.Flight
            "train" -> Icons.Default.Train
            "store", "toko" -> Icons.Default.Store
            "sportsesports", "gaming" -> Icons.Default.SportsEsports
            "movie", "film" -> Icons.Default.Movie
            "musicnote", "music" -> Icons.Default.MusicNote
            "medicalservices", "medical" -> Icons.Default.MedicalServices
            "fitnesscenter", "fitness", "gym" -> Icons.Default.FitnessCenter
            "lightbulb", "light" -> Icons.Default.Lightbulb
            "waterdrop", "water" -> Icons.Default.WaterDrop
            "work", "kerja" -> Icons.Default.Work
            "attachmoney", "money" -> Icons.Default.AttachMoney
            "creditcard", "card" -> Icons.Default.CreditCard
            "accountbalance", "bank" -> Icons.Default.AccountBalance
            "phone", "telephone" -> Icons.Default.Phone
            "email" -> Icons.Default.Email
            "chat" -> Icons.Default.Chat
            "cardgiftcard", "gift" -> Icons.Default.CardGiftcard
            "flighttakeoff", "takeoff" -> Icons.Default.FlightTakeoff
            "hotel" -> Icons.Default.Hotel
            else -> Icons.Default.Category
        }
    }
}
