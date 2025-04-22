# Group Log
- Please remember to update this accordingly

## Xander
### 4/8/2025 11:11pm 
- Refactor import deck to get a list of specialized card with the card
- Fix class Fields correct Char causing app to crash cause of the Parcelable
- Add ImportRepository, and Import Tables
- Add ModalContent for better code structure
- Add new ReturnValues
### 4/9/2025 1:16am
- Fix Cute theming
### 4/9/2025 1:04pm
- Renaming some things
- New xml string for spanish.
### 4/9/2025 9:32pm
- New SQLite tables (SyncedDeckInfo and ImportedDeckInfo)
- Migrations for adding the new tables, and new column for deck (for later)
- Changed up supabase DB functions (in the actual supabase website/back-end) and added better error handling
- Added view and view model for user to see their own exported decks
- Added a repository for letting users see their own decks
### 4/11/2025 10:02am
- Create a sign up/in option for email
- New activity to handle deep links
- New GETTINGSTARTED.md for helping people understand the project
- Move navigation directory out of controller for better structure
### 4/14/2025 10:15am
- Connect second supabase client and handle signing in/up for both clients
- Store encrypted temporary password in DB to deal with signing in with synced SB client.
- Migration for adding the temp password table to the DB
- Update VMs, Repos, DB files accordingly
### 4/20/2025 9.52am
- Add new table to show cards to display for exported decks
- Update supabase back-end transaction accordingly to handle the new table input
- New deck_content.html to display deck content more cleanly
- New return values, new props, new icons, etc
- Update password input to not be visible(optional)
- Add data classes to handle the cards to display
- Some loading screens for better UI
### 4/21/2025 2:13pm
- Search query for local cards in database
- Drawer modal content increased
- Refactor of SBNavHost
- New CardListView.
- More options for Supabase decks for owners.
## John
### 4/8/2025 11:50pm
- Cute Theme: ADDED for people who like cute stuff
- DARK CUTE THEME: for people who like dark Cute stuff
- added to Color.kt and Theme.kt
- added logic for supporting theme switching for CuteTheme & DarkCuteTheme
- edited in preference management file
- added new string resources for both CuteTheme and DarkCuteTheme
## Gus
