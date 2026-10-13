# Personal Budget & Expense Tracker --- Native Android (PRD v1.1)

A polished, offline-first personal finance application built for Android using Java, XML layouts, and Material Design, suitable for a college project and technical viva.

---

## 1. Product Overview
The Personal Budget & Expense Tracker allows users to record and analyze income, expenses, transfers, monthly budgets, savings goals, accounts, and recurring transactions securely and efficiently.

- **Offline-First**: Core financial operations work fully without an internet connection.
- **App Lock Security**: Protects financial information using device credentials (PIN/password/pattern) and biometric authentication (`BiometricPrompt`) without requiring a separate app password.
- **Modern UI / UX**: Material Design 3 guidelines with light/dark themes, cards, bottom navigation, empty/loading states, and accessible touch targets.

---

## 2. Key Features & Modules

1. **App Open & Authentication Screen**:
   - Requires biometric or device credential authentication upon app launch or resume timeout.
2. **Dashboard / Home Screen**:
   - Total balance, current-month income, and current-month expenses.
   - Monthly budget progress indicator.
   - **HorizontalScrollView** displaying user-entered accounts (`HDFC Bank`, `Savings`, `Cash`, etc.) with real-time balance cards.
   - Quick Add Transaction button.
3. **Transaction Management**:
   - Income, Expense, and Transfer types.
   - Search, filter (by date, category, account, type, amount), and sort transactions.
   - Attach receipt images (via camera/gallery implicit intents) and optional location coordinates.
4. **Accounts & Budgets**:
   - Manage multiple accounts (Cash, Bank, Card, UPI, Savings).
   - Category-based and overall monthly budgets with warning and exceeded states.
5. **Statistics & Savings Goals**:
   - Charts and numerical summaries for category spending and income vs. expense trends.
   - Track savings goals with progress indicators and contributions.
6. **Recurring Transactions & Notifications**:
   - Recurring income, expenses, and subscriptions scheduled with `BroadcastReceiver` and system notifications.
7. **File Handling**:
   - CSV export and import with robust error handling for malformed files.

---

## 3. Architecture & Tech Stack

- **Architecture**: MVVM + Repository pattern separating UI (Activities/Fragments) from business logic and local data.
- **Language**: Java
- **Core Libraries**:
  - AndroidX AppCompat & Material Components (Material 3)
  - ConstraintLayout, RecyclerView, HorizontalScrollView
  - AndroidX Biometric (`BiometricPrompt`)
  - ViewModel & LiveData / StateFlow
  - Navigation Component
