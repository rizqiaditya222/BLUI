# Dokumentasi Alur Data Transaction - Aplikasi Blui

## Daftar Isi
1. [Overview](#overview)
2. [Arsitektur Transaction](#arsitektur-transaction)
3. [Komponen-Komponen Utama](#komponen-komponen-utama)
4. [Alur Create Transaction](#alur-create-transaction)
5. [Alur Edit Transaction](#alur-edit-transaction)
6. [Alur Delete Transaction](#alur-delete-transaction)
7. [Alur Get Transactions](#alur-get-transactions)
8. [Alur Grouped Transactions](#alur-grouped-transactions)
9. [Data Models](#data-models)
10. [Error Handling](#error-handling)

---

## Overview

Aplikasi Blui memiliki fitur manajemen transaksi keuangan yang lengkap dengan kemampuan:
- **Create Transaction** (Income/Expense)
- **Read Transactions** (Filter by date, month, year)
- **Update Transaction** (Edit existing transaction)
- **Delete Transaction**
- **Grouped Transactions** (Group by date)

Sistem ini menggunakan **JWT Authentication** dan terintegrasi dengan **Category System**.

Base URL API: `https://blui.elginbrian.com/api/v1/`

---

## Arsitektur Transaction

```
┌─────────────────────────────────────────────────────────────────┐
│                    PRESENTATION LAYER                            │
│  ┌──────────────────┐         ┌────────────────────┐            │
│  │TransactionScreen │────────▶│TransactionViewModel│            │
│  │ - Add/Edit UI    │         │ - State Management │            │
│  │ - Form Validation│         │ - Business Logic   │            │
│  └──────────────────┘         └─────────┬──────────┘            │
│                                          │                       │
│  ┌──────────────────┐         ┌─────────┴──────────┐            │
│  │  DetailScreen    │────────▶│  DetailViewModel   │            │
│  │ - List View      │         │ - Filter & Sort    │            │
│  │ - Grouped by Date│         │ - Date Navigation  │            │
│  └──────────────────┘         └─────────┬──────────┘            │
│                                          │                       │
│  ┌──────────────────┐         ┌─────────┴──────────┐            │
│  │   HomeScreen     │────────▶│  HomeScreenViewModel│           │
│  │ - Summary View   │         │ - Summary Data     │            │
│  └──────────────────┘         └─────────┬──────────┘            │
└────────────────────────────────────────────┼──────────────────────┘
                                             │
                                             ▼
┌─────────────────────────────────────────────────────────────────┐
│                      DATA LAYER                                  │
│                  ┌─────────────────────┐                         │
│                  │TransactionRepository│                         │
│                  └──────────┬──────────┘                         │
│                             │                                    │
│              ┌──────────────┼──────────────┐                    │
│              ▼              ▼              ▼                     │
│      ┌──────────┐   ┌──────────┐  ┌────────────┐               │
│      │ApiService│   │ApiConfig │  │TokenManager│               │
│      └────┬─────┘   └────┬─────┘  └────────────┘               │
│           │              │                                       │
│           │        ┌─────▼─────┐                                │
│           │        │OkHttpClient│                               │
│           │        │+ Interceptor│                              │
│           │        └───────────┘                                │
└───────────┼──────────────────────────────────────────────────────┘
            │
            ▼
┌─────────────────────────────────────────────────────────────────┐
│                    BACKEND API                                   │
│  POST   /transactions          - Create transaction              │
│  GET    /transactions          - Get all transactions            │
│  GET    /transactions/:id      - Get single transaction          │
│  PUT    /transactions/:id      - Update transaction              │
│  DELETE /transactions/:id      - Delete transaction              │
│  GET    /transactions/grouped  - Get grouped by date             │
└─────────────────────────────────────────────────────────────────┘
```

---

## Komponen-Komponen Utama

### 1. TransactionRepository
**Location:** `data/repository/TransactionRepository.kt`

**Fungsi:** Mengelola semua operasi CRUD transaksi dan komunikasi dengan API.

**Key Methods:**
```kotlin
// Create
suspend fun createTransaction(
    type: String,           // "income" or "expense"
    name: String,           // Nama transaksi
    categoryId: String,     // ID kategori
    amount: Double,         // Jumlah uang
    date: String,           // Format: "YYYY-MM-DD"
    note: String?           // Catatan opsional
): Result<Transaction>

// Read
suspend fun getTransactions(
    month: Int? = null,
    year: Int? = null,
    date: String? = null,
    startDate: String? = null,
    endDate: String? = null
): Result<List<Transaction>>

suspend fun getGroupedTransactions(
    month: Int? = null,
    year: Int? = null,
    startDate: String? = null,
    endDate: String? = null
): Result<List<TransactionsByDate>>

suspend fun getTransactionById(id: String): Result<Transaction>

// Update
suspend fun updateTransaction(
    id: String,
    name: String,
    categoryId: String,
    amount: Double,
    date: String,
    note: String?
): Result<Transaction>

// Delete
suspend fun deleteTransaction(transactionId: String): Result<Unit>
```

**Extension Function:**
```kotlin
// Convert API response to domain model
fun TransactionResponse.toDomain(): Transaction {
    return Transaction(
        id = this.id,
        userId = this.userId,
        type = this.type,
        name = this.name,
        categoryId = this.categoryId,
        amount = this.amount,
        date = this.date,
        note = this.note,
        category = this.category?.let { Category(...) }
    )
}
```

### 2. TransactionViewModel
**Location:** `presentation/transaction/TransactionViewModel.kt`

**Fungsi:** Mengelola state dan logic untuk TransactionScreen (Add/Edit).

**UI State:**
```kotlin
data class TransactionUiState(
    val transactionId: String? = null,           // ID untuk edit mode
    val transactionType: String = "Expense",     // "Expense" or "Income"
    val transactionName: String = "",            // Nama transaksi
    val selectedCategory: Category? = null,      // Kategori yang dipilih
    val amount: String = "",                     // Jumlah (as String untuk input)
    val date: String = "",                       // Tanggal (YYYY-MM-DD)
    val note: String = "",                       // Catatan
    val categories: List<Category> = emptyList(),// List kategori available
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isSuccess: Boolean = false,
    val transactionMonth: Int? = null,           // Bulan untuk navigation
    val transactionYear: Int? = null             // Tahun untuk navigation
)
```

**Methods:**
```kotlin
// State Changes
fun onTransactionTypeChange(type: String)
fun onTransactionNameChange(name: String)
fun onCategorySelect(category: Category)
fun onAmountChange(amount: String)      // Filter only digits & decimal
fun onDateChange(date: String)
fun onNoteChange(note: String)

// Actions
fun loadCategories()                    // Load available categories
fun loadTransaction(transactionId: String) // Load for edit mode
fun saveTransaction(onSuccess: () -> Unit) // Create or Update
fun deleteTransaction(onSuccess: () -> Unit)
fun onDeleteCategory(categoryId: String)   // Delete from category list

// Validation
private fun validateInput(...): Boolean
fun clearError()
```

### 3. DetailViewModel
**Location:** `presentation/detail/DetailViewModel.kt`

**Fungsi:** Mengelola state untuk DetailScreen (List view dengan grouping).

**UI State:**
```kotlin
data class DetailUiState(
    val transactionGroups: List<TransactionsByDate> = emptyList(),
    val selectedMonth: Int = Calendar.getInstance().get(Calendar.MONTH),
    val selectedYear: Int = Calendar.getInstance().get(Calendar.YEAR),
    val selectedType: String = "All", // "All", "Expense", "Income"
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)
```

**Methods:**
```kotlin
fun loadTransactions()                  // Load with current filters
fun onMonthYearChange(month: Int, year: Int)
fun onTypeChange(type: String)          // Filter by type
fun clearError()
```

### 4. TransactionScreen
**Location:** `presentation/transaction/TransactionScreen.kt`

**Fungsi:** UI untuk add/edit transaksi.

**Features:**
- Transaction Type Toggle (Expense/Income) - hanya di Add mode
- Name Input Field
- Amount Input Field (numbers only)
- Category Picker Dialog
- Date Picker Dialog
- Note Input Field (optional)
- Save Button
- Delete Button (Edit mode only)

**Parameters:**
```kotlin
@Composable
fun TransactionScreen(
    isEditMode: Boolean = false,
    transactionId: String? = null,
    initialTransactionType: String = "Expense",
    onBack: () -> Unit = {},
    onSave: () -> Unit = {},
    onDelete: (month: Int?, year: Int?) -> Unit = { _, _ -> },
    onAddCategory: () -> Unit = {}
)
```

### 5. DetailScreen
**Location:** `presentation/detail/DetailScreen.kt`

**Fungsi:** UI untuk melihat list transaksi yang di-group by date.

**Features:**
- Month/Year Picker
- Type Filter (All/Expense/Income)
- Grouped Transaction List
- Navigate to Edit Transaction
- Summary per date

---

## Alur Create Transaction

### Flow Diagram
```
┌─────────────────────┐
│ User Input          │
│ - Select Type       │
│ - Enter Name        │
│ - Select Category   │
│ - Enter Amount      │
│ - Select Date       │
│ - Enter Note (opt)  │
└──────────┬──────────┘
           │
           ▼
┌─────────────────────────┐
│ TransactionScreen       │
│ - User click Save       │
└──────────┬──────────────┘
           │
           ▼
┌─────────────────────────────┐
│ TransactionViewModel        │
│ 1. Validate all inputs      │
│ 2. Check category selected  │
│ 3. Check amount > 0         │
│ 4. Set isLoading = true     │
└──────────┬──────────────────┘
           │
           ▼
┌─────────────────────────────┐
│ TransactionRepository       │
│ - Create request object     │
│ - Convert type to lowercase │
└──────────┬──────────────────┘
           │
           ▼
┌─────────────────────────────┐
│ ApiService                  │
│ POST /transactions          │
│ + JWT Token (auto inject)  │
└──────────┬──────────────────┘
           │
           ▼
┌─────────────────────────────┐
│ Backend API                 │
│ - Validate JWT token        │
│ - Validate user owns cat    │
│ - Save to database          │
│ - Return transaction data   │
└──────────┬──────────────────┘
           │
           ▼
┌─────────────────────────────┐
│ TransactionRepository       │
│ - Convert response          │
│ - Map to domain model       │
└──────────┬──────────────────┘
           │
           ▼
┌─────────────────────────────┐
│ TransactionViewModel        │
│ - Set isSuccess = true      │
│ - Set isLoading = false     │
│ - Call onSuccess callback   │
└──────────┬──────────────────┘
           │
           ▼
┌─────────────────────────────┐
│ TransactionScreen           │
│ - LaunchedEffect detects    │
│   isSuccess = true          │
│ - Navigate back             │
│ - Refresh previous screen   │
└─────────────────────────────┘
```

### Step-by-Step Detail

#### 1. User Input
User mengisi form transaksi:
- **Transaction Type**: Expense atau Income (toggle)
- **Name**: Nama transaksi (contoh: "Makan siang")
- **Category**: Pilih dari dialog (contoh: "Makanan")
- **Amount**: Jumlah uang (contoh: "50000")
- **Date**: Pilih tanggal dari date picker
- **Note**: Catatan opsional

#### 2. Input Validation
```kotlin
private fun validateInput(
    name: String,
    category: Category?,
    amount: String,
    date: String
): Boolean {
    return when {
        name.isEmpty() -> {
            _uiState.value = _uiState.value.copy(
                errorMessage = "Nama transaksi tidak boleh kosong"
            )
            false
        }
        category == null -> {
            _uiState.value = _uiState.value.copy(
                errorMessage = "Pilih kategori terlebih dahulu"
            )
            false
        }
        amount.isEmpty() -> {
            _uiState.value = _uiState.value.copy(
                errorMessage = "Jumlah tidak boleh kosong"
            )
            false
        }
        amount.toDoubleOrNull() == null || amount.toDouble() <= 0 -> {
            _uiState.value = _uiState.value.copy(
                errorMessage = "Jumlah harus lebih dari 0"
            )
            false
        }
        date.isEmpty() -> {
            _uiState.value = _uiState.value.copy(
                errorMessage = "Tanggal tidak boleh kosong"
            )
            false
        }
        else -> true
    }
}
```

#### 3. Create Transaction Request
```kotlin
fun saveTransaction(onSuccess: () -> Unit) {
    val type = _uiState.value.transactionType.lowercase() // "expense" or "income"
    val name = _uiState.value.transactionName.trim()
    val category = _uiState.value.selectedCategory
    val amountStr = _uiState.value.amount.trim()
    val date = _uiState.value.date
    val note = _uiState.value.note.trim().ifBlank { null }
    
    if (!validateInput(name, category, amountStr, date)) {
        return
    }
    
    val amount = amountStr.toDoubleOrNull() ?: 0.0
    
    viewModelScope.launch {
        _uiState.value = _uiState.value.copy(isLoading = true)
        
        val result = transactionRepository.createTransaction(
            type = type,
            name = name,
            categoryId = category!!.id,
            amount = amount,
            date = date,
            note = note
        )
        
        // Handle result...
    }
}
```

#### 4. API Request
**Request:**
```
POST https://blui.elginbrian.com/api/v1/transactions
Authorization: Bearer {JWT_TOKEN}
Content-Type: application/json

{
  "type": "expense",
  "name": "Makan siang",
  "categoryId": "cat_123",
  "amount": 50000,
  "date": "2025-11-20",
  "note": "Makan di restoran"
}
```

**Response (Success):**
```json
{
  "id": "tx_456",
  "userId": "user_123",
  "type": "expense",
  "name": "Makan siang",
  "categoryId": "cat_123",
  "amount": 50000,
  "date": "2025-11-20",
  "note": "Makan di restoran",
  "category": {
    "id": "cat_123",
    "name": "Makanan",
    "icon": "restaurant",
    "color": "#FF5733",
    "type": "expense"
  }
}
```

#### 5. Update UI & Navigate
```kotlin
result.onSuccess {
    _uiState.value = _uiState.value.copy(
        isLoading = false,
        isSuccess = true,
        errorMessage = null
    )
    onSuccess()
}

// Di TransactionScreen
LaunchedEffect(uiState.isSuccess) {
    if (uiState.isSuccess) {
        onSave() // Navigate back
    }
}
```

---

## Alur Edit Transaction

### Flow Diagram
```
┌─────────────────────────┐
│ User Click Transaction  │
│ from DetailScreen       │
└──────────┬──────────────┘
           │
           ▼
┌─────────────────────────────┐
│ Navigate to TransactionScreen│
│ - isEditMode = true         │
│ - transactionId = "tx_456"  │
└──────────┬──────────────────┘
           │
           ▼
┌─────────────────────────────┐
│ TransactionViewModel        │
│ loadTransaction(id)         │
└──────────┬──────────────────┘
           │
           ▼
┌─────────────────────────────┐
│ TransactionRepository       │
│ getTransactionById(id)      │
└──────────┬──────────────────┘
           │
           ▼
┌─────────────────────────────┐
│ ApiService                  │
│ GET /transactions/:id       │
└──────────┬──────────────────┘
           │
           ▼
┌─────────────────────────────┐
│ Backend API                 │
│ - Validate user owns tx     │
│ - Return transaction data   │
└──────────┬──────────────────┘
           │
           ▼
┌─────────────────────────────┐
│ TransactionViewModel        │
│ - Populate form fields      │
│ - Set transactionId         │
│ - Extract month/year        │
└──────────┬──────────────────┘
           │
           ▼
┌─────────────────────────────┐
│ User Edit Fields            │
│ - Name, Amount, Date, Note  │
│ - Cannot change type        │
│ - Click Save                │
└──────────┬──────────────────┘
           │
           ▼
┌─────────────────────────────┐
│ TransactionViewModel        │
│ saveTransaction()           │
│ - Detect transactionId ≠ null│
│ - Call updateTransaction()  │
└──────────┬──────────────────┘
           │
           ▼
┌─────────────────────────────┐
│ ApiService                  │
│ PUT /transactions/:id       │
└──────────┬──────────────────┘
           │
           ▼
┌─────────────────────────────┐
│ Backend API                 │
│ - Update database           │
│ - Return updated data       │
└──────────┬──────────────────┘
           │
           ▼
┌─────────────────────────────┐
│ Navigate Back               │
│ - Refresh DetailScreen      │
└─────────────────────────────┘
```

### Step-by-Step Detail

#### 1. Load Transaction Data
```kotlin
fun loadTransaction(transactionId: String) {
    viewModelScope.launch {
        _uiState.value = _uiState.value.copy(isLoading = true)
        
        val result = transactionRepository.getTransactionById(transactionId)
        result.onSuccess { transaction ->
            // Extract month and year from date
            val (month, year) = try {
                val parts = transaction.date.split('-')
                val y = parts.getOrNull(0)?.toIntOrNull()
                val m = parts.getOrNull(1)?.toIntOrNull()
                Pair(if (m != null) m - 1 else null, y)
            } catch (e: Exception) {
                Pair(null, null)
            }
            
            _uiState.value = _uiState.value.copy(
                transactionId = transaction.id,
                transactionType = transaction.type,
                transactionName = transaction.name,
                selectedCategory = transaction.category,
                amount = transaction.amount.toString(),
                date = transaction.date,
                note = transaction.note ?: "",
                transactionMonth = month,
                transactionYear = year,
                isLoading = false
            )
        }
    }
}
```

#### 2. Update Transaction
```kotlin
fun saveTransaction(onSuccess: () -> Unit) {
    val transactionId = _uiState.value.transactionId
    
    // ... validation ...
    
    val result = if (transactionId != null) {
        // Update mode
        transactionRepository.updateTransaction(
            id = transactionId,
            name = name,
            categoryId = category!!.id,
            amount = amount,
            date = date,
            note = note
        )
    } else {
        // Create mode
        transactionRepository.createTransaction(...)
    }
}
```

#### 3. API Request (Update)
**Request:**
```
PUT https://blui.elginbrian.com/api/v1/transactions/tx_456
Authorization: Bearer {JWT_TOKEN}
Content-Type: application/json

{
  "name": "Makan siang (updated)",
  "categoryId": "cat_123",
  "amount": 60000,
  "date": "2025-11-20",
  "note": "Makan di restoran baru"
}
```

**Note:** Type tidak bisa diubah saat edit.

---

## Alur Delete Transaction

### Flow Diagram
```
┌─────────────────────────┐
│ User in Edit Mode       │
│ Click Delete Button     │
└──────────┬──────────────┘
           │
           ▼
┌─────────────────────────────┐
│ Confirmation Dialog         │
│ "Hapus transaksi?"          │
└──────────┬──────────────────┘
           │
           ▼
┌─────────────────────────────┐
│ TransactionViewModel        │
│ deleteTransaction()         │
└──────────┬──────────────────┘
           │
           ▼
┌─────────────────────────────┐
│ TransactionRepository       │
│ deleteTransaction(id)       │
└──────────┬──────────────────┘
           │
           ▼
┌─────────────────────────────┐
│ ApiService                  │
│ DELETE /transactions/:id    │
└──────────┬──────────────────┘
           │
           ▼
┌─────────────────────────────┐
│ Backend API                 │
│ - Validate user owns tx     │
│ - Delete from database      │
│ - Return success            │
└──────────┬──────────────────┘
           │
           ▼
┌─────────────────────────────┐
│ TransactionViewModel        │
│ - Set isSuccess = true      │
│ - Call onSuccess callback   │
└──────────┬──────────────────┘
           │
           ▼
┌─────────────────────────────┐
│ Navigate Back               │
│ - Pass month & year         │
│ - Refresh DetailScreen      │
└─────────────────────────────┘
```

### Implementation
```kotlin
fun deleteTransaction(onSuccess: () -> Unit) {
    val transactionId = _uiState.value.transactionId
    if (transactionId == null) {
        _uiState.value = _uiState.value.copy(
            errorMessage = "ID transaksi tidak ditemukan"
        )
        return
    }
    
    viewModelScope.launch {
        _uiState.value = _uiState.value.copy(isLoading = true)
        
        val result = transactionRepository.deleteTransaction(transactionId)
        result.onSuccess {
            _uiState.value = _uiState.value.copy(
                isLoading = false,
                isSuccess = true
            )
            onSuccess()
        }.onFailure { exception ->
            _uiState.value = _uiState.value.copy(
                isLoading = false,
                errorMessage = exception.message ?: "Gagal menghapus transaksi"
            )
        }
    }
}
```

**API Request:**
```
DELETE https://blui.elginbrian.com/api/v1/transactions/tx_456
Authorization: Bearer {JWT_TOKEN}
```

**Response:** 204 No Content (success)

---

## Alur Get Transactions

### Simple List (Filtered)

#### Flow Diagram
```
┌─────────────────────────┐
│ HomeScreen / DetailScreen│
│ Request transactions    │
└──────────┬──────────────┘
           │
           ▼
┌─────────────────────────────┐
│ ViewModel                   │
│ loadTransactions()          │
└──────────┬──────────────────┘
           │
           ▼
┌─────────────────────────────┐
│ TransactionRepository       │
│ getTransactions(            │
│   month, year, date         │
│ )                           │
└──────────┬──────────────────┘
           │
           ▼
┌─────────────────────────────┐
│ ApiService                  │
│ GET /transactions?          │
│   month=11&year=2025        │
└──────────┬──────────────────┘
           │
           ▼
┌─────────────────────────────┐
│ Backend API                 │
│ - Query database            │
│ - Apply filters             │
│ - Return transactions list  │
└──────────┬──────────────────┘
           │
           ▼
┌─────────────────────────────┐
│ TransactionRepository       │
│ - Map each response         │
│ - Convert to domain models  │
└──────────┬──────────────────┘
           │
           ▼
┌─────────────────────────────┐
│ ViewModel                   │
│ - Update UI state           │
│ - Display in list           │
└─────────────────────────────┘
```

#### Implementation
```kotlin
suspend fun getTransactions(
    month: Int? = null,
    year: Int? = null,
    date: String? = null,
    startDate: String? = null,
    endDate: String? = null
): Result<List<Transaction>> {
    return try {
        val response = apiService.getTransactions(
            month, year, date, startDate, endDate
        )
        val transactions = response.transactions.map { it.toDomain() }
        Result.success(transactions)
    } catch (e: Exception) {
        Result.failure(e)
    }
}
```

#### API Examples

**Get all transactions for November 2025:**
```
GET /transactions?month=11&year=2025
```

**Get transactions for specific date:**
```
GET /transactions?date=2025-11-20
```

**Get transactions in date range:**
```
GET /transactions?startDate=2025-11-01&endDate=2025-11-30
```

---

## Alur Grouped Transactions

### Group by Date

#### Flow Diagram
```
┌─────────────────────────┐
│ DetailScreen            │
│ Request grouped data    │
└──────────┬──────────────┘
           │
           ▼
┌─────────────────────────────┐
│ DetailViewModel             │
│ loadTransactions()          │
└──────────┬──────────────────┘
           │
           ▼
┌─────────────────────────────┐
│ TransactionRepository       │
│ getGroupedTransactions(     │
│   month, year               │
│ )                           │
└──────────┬──────────────────┘
           │
           ▼
┌─────────────────────────────┐
│ ApiService                  │
│ GET /transactions/grouped?  │
│   month=11&year=2025        │
└──────────┬──────────────────┘
           │
           ▼
┌─────────────────────────────┐
│ Backend API                 │
│ - Query transactions        │
│ - Group by date             │
│ - Calculate totals per date │
│ - Return grouped structure  │
└──────────┬──────────────────┘
           │
           ▼
┌─────────────────────────────┐
│ TransactionRepository       │
│ - Map to TransactionsByDate │
│ - Convert each transaction  │
└──────────┬──────────────────┘
           │
           ▼
┌─────────────────────────────┐
│ DetailViewModel             │
│ - Apply type filter         │
│ - Update UI state           │
└──────────┬──────────────────┘
           │
           ▼
┌─────────────────────────────┐
│ DetailScreen                │
│ - Display grouped list      │
│ - Show date headers         │
│ - Show totals per group     │
└─────────────────────────────┘
```

#### Implementation
```kotlin
suspend fun getGroupedTransactions(
    month: Int? = null,
    year: Int? = null,
    startDate: String? = null,
    endDate: String? = null
): Result<List<TransactionsByDate>> {
    return try {
        val response = apiService.getGroupedTransactions(
            month, year, startDate, endDate
        )
        val grouped = response.groups.map { group ->
            TransactionsByDate(
                date = group.date,
                transactions = group.transactions.map { it.toDomain() },
                totalIncome = group.totalIncome,
                totalExpense = group.totalExpense
            )
        }
        Result.success(grouped)
    } catch (e: Exception) {
        Result.failure(e)
    }
}
```

#### Response Structure
```json
{
  "groups": [
    {
      "date": "2025-11-20",
      "totalIncome": 100000,
      "totalExpense": 75000,
      "transactions": [
        {
          "id": "tx_1",
          "type": "expense",
          "name": "Makan siang",
          "amount": 50000,
          "category": { "name": "Makanan", "icon": "restaurant", "color": "#FF5733" }
        },
        {
          "id": "tx_2",
          "type": "income",
          "name": "Gaji",
          "amount": 100000,
          "category": { "name": "Salary", "icon": "work", "color": "#4CAF50" }
        }
      ]
    },
    {
      "date": "2025-11-19",
      "totalIncome": 0,
      "totalExpense": 30000,
      "transactions": [...]
    }
  ]
}
```

#### Type Filtering
```kotlin
fun loadTransactions() {
    // ... load from repository ...
    
    // Filter by type if not "All"
    val filteredGroups = if (_uiState.value.selectedType == "All") {
        groups
    } else {
        groups.map { group ->
            group.copy(
                transactions = group.transactions.filter { tx ->
                    tx.type.equals(_uiState.value.selectedType, ignoreCase = true)
                }
            )
        }.filter { it.transactions.isNotEmpty() }
    }
    
    _uiState.value = _uiState.value.copy(
        transactionGroups = filteredGroups
    )
}
```

---

## Data Models

### 1. Domain Models

#### Transaction
```kotlin
data class Transaction(
    val id: String,
    val userId: String,
    val type: String,           // "income" or "expense"
    val name: String,
    val categoryId: String,
    val amount: Double,
    val date: String,           // Format: "YYYY-MM-DD"
    val note: String?,
    val category: Category? = null
)
```

#### TransactionsByDate
```kotlin
data class TransactionsByDate(
    val date: String,           // Format: "YYYY-MM-DD"
    val transactions: List<Transaction>,
    val totalIncome: Double,
    val totalExpense: Double
)
```

### 2. Request Models

#### CreateTransactionRequest
```kotlin
data class CreateTransactionRequest(
    val type: String,           // "income" or "expense"
    val name: String,
    val categoryId: String,
    val amount: Double,
    val date: String,           // Format: "YYYY-MM-DD"
    val note: String?
)
```

#### UpdateTransactionRequest
```kotlin
data class UpdateTransactionRequest(
    val name: String,
    val categoryId: String,
    val amount: Double,
    val date: String,
    val note: String?
)
```

**Note:** Type tidak bisa diubah saat update.

### 3. Response Models

#### TransactionResponse
```kotlin
data class TransactionResponse(
    @SerializedName("id")
    val id: String,
    @SerializedName("userId")
    val userId: String,
    @SerializedName("type")
    val type: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("categoryId")
    val categoryId: String,
    @SerializedName("amount")
    val amount: Double,
    @SerializedName("date")
    val date: String,
    @SerializedName("note")
    val note: String?,
    @SerializedName("category")
    val category: CategoryResponse?
)
```

#### TransactionsListResponse
```kotlin
data class TransactionsListResponse(
    @SerializedName("transactions")
    val transactions: List<TransactionResponse>
)
```

#### GroupedTransactionsResponse
```kotlin
data class GroupedTransactionsResponse(
    @SerializedName("groups")
    val groups: List<TransactionsByDateResponse>
)

data class TransactionsByDateResponse(
    @SerializedName("date")
    val date: String,
    @SerializedName("transactions")
    val transactions: List<TransactionResponse>,
    @SerializedName("totalIncome")
    val totalIncome: Double,
    @SerializedName("totalExpense")
    val totalExpense: Double
)
```

---

## Error Handling

### 1. Validation Errors

#### Input Validation
```kotlin
when {
    name.isEmpty() -> 
        "Nama transaksi tidak boleh kosong"
    
    category == null -> 
        "Pilih kategori terlebih dahulu"
    
    amount.isEmpty() -> 
        "Jumlah tidak boleh kosong"
    
    amount.toDoubleOrNull() == null || amount.toDouble() <= 0 -> 
        "Jumlah harus lebih dari 0"
    
    date.isEmpty() -> 
        "Tanggal tidak boleh kosong"
}
```

#### Amount Input Filtering
```kotlin
fun onAmountChange(amount: String) {
    // Only allow numbers and one decimal point
    val filtered = amount.filter { it.isDigit() || it == '.' }
    if (filtered.count { it == '.' } <= 1) {
        _uiState.value = _uiState.value.copy(
            amount = filtered, 
            errorMessage = null
        )
    }
}
```

### 2. API Errors

#### 400 Bad Request
**Causes:**
- Invalid transaction type
- Invalid category ID
- Invalid date format
- Amount <= 0

**Handling:**
```kotlin
result.onFailure { exception ->
    _uiState.value = _uiState.value.copy(
        isLoading = false,
        errorMessage = exception.message ?: "Gagal menyimpan transaksi"
    )
}
```

#### 401 Unauthorized
**Causes:**
- Token expired
- Invalid token
- No token provided

**Action:**
- Logout user
- Navigate to login screen

#### 403 Forbidden
**Causes:**
- User trying to access/modify someone else's transaction
- User trying to use someone else's category

**Handling:**
```kotlin
"Anda tidak memiliki akses ke transaksi ini"
```

#### 404 Not Found
**Causes:**
- Transaction ID tidak ditemukan
- Category ID tidak ditemukan

**Handling:**
```kotlin
"Transaksi tidak ditemukan"
```

#### 500 Server Error
**Causes:**
- Database error
- Server crash

**Handling:**
```kotlin
"Terjadi kesalahan server. Silakan coba lagi"
```

### 3. Network Errors

#### No Internet Connection
```kotlin
try {
    val response = apiService.createTransaction(request)
    Result.success(response.toDomain())
} catch (e: Exception) {
    println("Create Transaction Error: ${e.message}")
    e.printStackTrace()
    Result.failure(e)
}
```

**User Message:**
```
"Tidak ada koneksi internet"
```

#### Timeout
**Timeout Configuration:** 30 seconds

**User Message:**
```
"Request timeout. Silakan coba lagi"
```

### 4. Error Display

#### Snackbar
```kotlin
// Di TransactionScreen
LaunchedEffect(uiState.errorMessage) {
    uiState.errorMessage?.let { message ->
        snackbarHostState.showSnackbar(message)
        viewModel.clearError()
    }
}
```

#### Loading State
```kotlin
if (uiState.isLoading) {
    CircularProgressIndicator()
}
```

---

## Integration dengan Category System

### Category Selection

#### Load Available Categories
```kotlin
fun loadCategories() {
    viewModelScope.launch {
        val result = categoryRepository.getCategories()
        result.onSuccess { categories ->
            _uiState.value = _uiState.value.copy(
                categories = categories
            )
        }
    }
}
```

#### Category Picker Dialog
```kotlin
CategoryPickerDialog(
    show = showCategoryPicker,
    categories = categoriesForDisplay.map { category ->
        CategoryData(
            id = category.id,
            name = category.name,
            icon = IconMapper.mapIcon(category.icon),
            color = parseColor(category.color)
        )
    },
    deleteMode = deleteMode,
    onCategorySelect = { categoryData ->
        val selected = uiState.categories.find { it.id == categoryData.id }
        selected?.let { viewModel.onCategorySelect(it) }
        showCategoryPicker = false
    },
    onDismiss = { 
        showCategoryPicker = false 
        deleteMode = false
    },
    onAddCategory = onAddCategory,
    onDeleteCategory = { categoryId ->
        viewModel.onDeleteCategory(categoryId)
    }
)
```

#### Filter by Type
```kotlin
val categoriesForDisplay = if (isEditMode) {
    // Edit mode: show all categories
    uiState.categories
} else {
    // Add mode: filter by transaction type
    uiState.categories.filter { category ->
        category.type.equals(uiState.transactionType, ignoreCase = true)
    }
}
```

### Category Data in Transaction
Saat create/update transaction, backend akan:
1. Validate category exists
2. Validate user owns category
3. Return transaction WITH category data (name, icon, color)

Response includes complete category info:
```json
{
  "id": "tx_456",
  "name": "Makan siang",
  "category": {
    "id": "cat_123",
    "name": "Makanan",
    "icon": "restaurant",
    "color": "#FF5733",
    "type": "expense"
  }
}
```

---

## Integration dengan Summary System

### Impact on Balance
Setiap create/update/delete transaction akan mempengaruhi:

1. **Total Income** (jika type = "income")
2. **Total Expense** (jika type = "expense")
3. **Balance** (income - expense)
4. **Category Summary** (total per category)

### Auto-Refresh Summary
Setelah transaction berhasil disimpan/dihapus:
```kotlin
// HomeScreen akan auto-refresh summary
LaunchedEffect(uiState.selectedMonth, uiState.selectedYear) {
    viewModel.loadSummary()
}
```

### Month/Year Context
Transaction menyimpan month & year untuk:
- Navigation context (kembali ke bulan yang sama)
- Summary calculation
- Filtering di DetailScreen

```kotlin
// Extract from date
val (month, year) = try {
    val parts = transaction.date.split('-')
    val y = parts.getOrNull(0)?.toIntOrNull()
    val m = parts.getOrNull(1)?.toIntOrNull()
    Pair(if (m != null) m - 1 else null, y) // 0-based month
} catch (e: Exception) {
    Pair(null, null)
}
```

---

## Navigation Flow

### Add Transaction Flow
```
MainScreen (Home)
    ↓ Click "+" Button
TransactionScreen (Add Mode)
    - initialTransactionType from button context
    - isEditMode = false
    ↓ User fills form & saves
    ↓ onSave() callback
Back to MainScreen
    - HomeScreen auto-refreshes
```

### Edit Transaction Flow
```
DetailScreen
    ↓ User clicks transaction item
TransactionScreen (Edit Mode)
    - isEditMode = true
    - transactionId = "tx_456"
    - Load transaction data
    - Type toggle hidden
    ↓ User edits & saves
    ↓ onSave() callback
Back to DetailScreen
    - Pass month & year context
    - DetailScreen refreshes
```

### Delete Transaction Flow
```
TransactionScreen (Edit Mode)
    ↓ User clicks delete button
    ↓ Confirmation dialog
    ↓ Confirm delete
    ↓ onDelete(month, year) callback
Back to DetailScreen
    - Navigate to correct month
    - DetailScreen refreshes
```

---

## Best Practices Implementation

### ✅ Data Validation
1. **Client-side validation** sebelum API call
2. **Server-side validation** di backend
3. **Input filtering** (amount only numbers)
4. **Required fields** check

### ✅ User Experience
1. **Loading states** untuk setiap action
2. **Error messages** via Snackbar
3. **Success feedback** dengan navigation
4. **Optimistic updates** (optional)
5. **Auto-refresh** setelah perubahan

### ✅ Performance
1. **Coroutines** untuk async operations
2. **Lazy loading** categories
3. **Grouped queries** untuk efisiensi
4. **Caching** (optional di repository)

### ✅ Architecture
1. **Separation of concerns** (Screen → ViewModel → Repository)
2. **Single source of truth** (ViewModel state)
3. **Reactive UI** (StateFlow)
4. **Domain models** vs Response models

### ✅ Security
1. **JWT authentication** untuk semua requests
2. **User ownership validation** di backend
3. **Input sanitization**
4. **HTTPS** untuk API calls

---

## Troubleshooting

### Problem: Transaction tidak tersimpan
**Kemungkinan Penyebab:**
1. Validation error (check logs)
2. Category tidak dipilih
3. Amount invalid
4. Network error

**Solution:**
```kotlin
// Check validation
println("Transaction Data:")
println("  Type: $type")
println("  Name: $name")
println("  Category: ${category?.id}")
println("  Amount: $amount")
println("  Date: $date")

// Check response
result.onFailure { exception ->
    println("Error: ${exception.message}")
    exception.printStackTrace()
}
```

### Problem: Categories tidak muncul di picker
**Kemungkinan Penyebab:**
1. Categories belum di-load
2. Filter type tidak match
3. API error

**Solution:**
```kotlin
// Force reload categories
LaunchedEffect(Unit) {
    viewModel.loadCategories()
}

// Check filter
val categoriesForDisplay = uiState.categories.filter { 
    it.type.equals(uiState.transactionType, ignoreCase = true)
}
println("Available categories: ${categoriesForDisplay.size}")
```

### Problem: Edit mode tidak load data
**Kemungkinan Penyebab:**
1. TransactionId null atau invalid
2. API error 404
3. User tidak punya akses

**Solution:**
```kotlin
// Check ID
LaunchedEffect(transactionId) {
    println("Loading transaction: $transactionId")
    if (isEditMode && !transactionId.isNullOrEmpty()) {
        viewModel.loadTransaction(transactionId)
    }
}
```

### Problem: Date picker tidak update
**Solution:**
```kotlin
// Update state from date picker
LaunchedEffect(datePickerState.selectedDateMillis) {
    datePickerState.selectedDateMillis?.let { millis ->
        val date = Date(millis)
        val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        viewModel.onDateChange(formatter.format(date))
    }
}
```

---

## Summary

### Transaction Operations

**Create:**
1. Select type (Expense/Income)
2. Fill form (name, category, amount, date, note)
3. Validate inputs
4. POST /transactions
5. Navigate back & refresh

**Read:**
1. GET /transactions (with filters)
2. GET /transactions/:id (single)
3. GET /transactions/grouped (by date)
4. Apply filters (month, year, type)

**Update:**
1. Load transaction by ID
2. Populate form
3. Edit fields (type cannot change)
4. PUT /transactions/:id
5. Navigate back & refresh

**Delete:**
1. Confirm deletion
2. DELETE /transactions/:id
3. Navigate back with context
4. Refresh list

### Key Features
- ✅ Transaction type: Income & Expense
- ✅ Category integration
- ✅ Date selection
- ✅ Amount validation
- ✅ Notes (optional)
- ✅ Grouped by date view
- ✅ Filter by month/year/type
- ✅ Edit & Delete
- ✅ Auto-refresh summary

---

## File Structure

```
app/src/main/java/com/kotlin/blui/
│
├── data/
│   ├── api/
│   │   ├── ApiService.kt              # Transaction endpoints
│   │   ├── request/
│   │   │   └── TransactionRequests.kt  # Request models
│   │   └── response/
│   │       └── TransactionResponses.kt # Response models
│   │
│   └── repository/
│       └── TransactionRepository.kt    # CRUD operations
│
├── domain/
│   └── model/
│       ├── Transaction.kt              # Domain model
│       └── TransactionsByDate.kt       # Grouped model
│
└── presentation/
    ├── transaction/
    │   ├── TransactionScreen.kt        # Add/Edit UI
    │   └── TransactionViewModel.kt     # State & Logic
    │
    ├── detail/
    │   ├── DetailScreen.kt             # List view UI
    │   └── DetailViewModel.kt          # Filter & Load
    │
    └── home/
        ├── HomeScreen.kt               # Summary view
        └── HomeScreenViewModel.kt      # Summary logic
```

---

## API Endpoints Summary

```
# Create Transaction
POST /transactions
Body: { type, name, categoryId, amount, date, note }

# Get All Transactions (with filters)
GET /transactions?month=11&year=2025
GET /transactions?date=2025-11-20
GET /transactions?startDate=2025-11-01&endDate=2025-11-30

# Get Single Transaction
GET /transactions/:id

# Update Transaction
PUT /transactions/:id
Body: { name, categoryId, amount, date, note }

# Delete Transaction
DELETE /transactions/:id

# Get Grouped Transactions
GET /transactions/grouped?month=11&year=2025
```

**Authentication:** All endpoints require JWT token via `Authorization: Bearer {token}`

---

**Dokumentasi dibuat:** November 20, 2025
**Aplikasi:** Blui - Personal Finance Tracker
**Architecture:** Clean Architecture + MVVM
**Version:** 1.0.0

