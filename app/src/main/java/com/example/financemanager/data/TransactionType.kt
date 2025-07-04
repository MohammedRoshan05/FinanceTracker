package com.example.financemanager.data

enum class TransactionType(val displayName: String) {
    HOME("Rent"),
    GROCERIES("Groceries"),
    FOOD("Food"),
    ELECTRONICS("Electronics"),
    CAR("Car"),
    TRAVEL("Travel"),
    OTHER("Other");

    override fun toString() = displayName
}
