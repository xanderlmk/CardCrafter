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
### 4/28/2025 11:55am
- Sync option to replace local DB 
- Some UI Icon fixes
- Create some reusable functions for more clean code.
- New override sync function forthcoming.
### 4/28/2025 4:19pm
- Override sync function done along with it's UI components.
### 4/30/2025 8:35pm
- Allow for users to send requests for co-owner by username
- Allow users to accept requests to become co-owner of a deck
- New Composable function pages, update SBNavHost, etc
- New VM, Repo, View accordingly.
- That's all (Sourcery AI don't get mad i put this LOL)
### 5/2/2025 11:17am
- Allow to decline requests
- Added a new export repository for better code readability
- Making sure that the lastUpdatedOn for ImportDeckInfo is updated properly when dealing 
with exporting/upserting decks
### 5/3/2025
- Implement deck merging functionality and refactor code to support more robust deck synchronization
- #### New Features:
- - Added merge functionality for remote and local decks
- - Introduced card info tracking for local and remote cards
- - Enhanced deck synchronization process
- #### Enhancements:
- - Refactored repositories and data access objects
- - Improved error handling in deck import and export
- - Added more flexible card management during synchronization
- #### Chores:
- - Updated database migrations
- - Reorganized package structure
- - Removed redundant code
### 5/4/2025 1:42pm
- Add ownership and co-ownership checks for card insertion in decks
- #### New Features:
- - Implement ownership verification for card insertion
- - Add ability to check if user is owner or co-owner of a deck
- #### Enhancements:
- - Refactor card insertion methods to include ownership parameter
- - Create new repository and DAO interfaces for flexible insert strategies
- #### Chores:
- - Modify navigation and view model to support deck UUID passing
- - Update multiple interfaces to support new ownership check mechanism
### 5/6/2025 9.37pm
- Update Schema images, and update back buttons to be on the top bar.
## John
### 4/8/2025 11:50pm
- Cute Theme: ADDED for people who like cute stuff
- DARK CUTE THEME: for people who like dark Cute stuff
- added to Color.kt and Theme.kt
- added logic for supporting theme switching for CuteTheme & DarkCuteTheme
- edited in preference management file
- added new string resources for both CuteTheme and DarkCuteTheme
### 4/22/2025
- Start of the Deck Sync ( Doesn't Work At ALL!)
- Added a cool cloud sync logo
- Added files in /multiple files in Supabase/Controller/viewModels, Supabase/Controllermodel/daoAndRepository/daos
- Added files Supabase/model/daoAndRepository/repositories
- Added files in Supabase/model/viewModels
- added files in Supabase/model/tables
- added then the Cloud_sync.xml
### 4/24/2025
- added functionality to the syncing
- sends data to online database
- cannot be retrieved yet
- fixed bug where password wasn't being correctly sent to sign up on the second supabase auth
### 4/25/2025
- Successfully synced decks, from local to supabase database
- fixed bugs
- I want to die 
- make sure that the last updated on, is locally saved for future comparisons to make sure there 
is no syncing conflicts
## Gus
### 4/20/2025 4:05pm
- Katex mapping
- autocomplete for users who need it fast
### 4/27/2025 2:46
- Uppercase Katex mapping