package com.example.viewmodel

import android.app.Application
import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.api.GeminiClient
import com.example.data.local.AppDatabase
import com.example.data.local.UserSettingsStore
import com.example.data.local.entity.DailyAmalEntity
import com.example.data.local.entity.DhikrLogEntity
import com.example.data.local.entity.QuranLogEntity
import com.example.data.local.entity.QuranVerseEntity
import com.example.data.local.entity.SalahLogEntity
import com.example.data.model.*
import com.example.data.repository.AmalRepository
import com.example.data.repository.BadHabitRepository
import com.example.data.repository.IslamicRepository
import com.example.data.repository.QuranRepository
import com.example.data.service.BanglaCalendarService
import com.example.data.service.BanglaDateInfo
import com.example.data.service.HijriCalendarService
import com.example.data.service.LocationService
import com.example.data.service.UserLocationInfo
import com.example.data.service.PrayerTimeCalculatorService
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Locale

@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
class AlHujurViewModel(application: Application) : AndroidViewModel(application) {

    // --- ROOM DATABASE REPOSITORIES ---
    private val database = AppDatabase.getInstance(application)
    private val settings = UserSettingsStore(application)
    val amalRepository = AmalRepository(database.amalDao())
    val quranRepository = QuranRepository(database.quranVerseDao())

    // --- QURAN READER STATE ---
    private val _showQuranReaderSheet = MutableStateFlow(false)
    val showQuranReaderSheet: StateFlow<Boolean> = _showQuranReaderSheet.asStateFlow()

    private val _selectedQuranSurahNumber = MutableStateFlow(1)
    val selectedQuranSurahNumber: StateFlow<Int> = _selectedQuranSurahNumber.asStateFlow()

    private val _quranSearchQuery = MutableStateFlow("")
    val quranSearchQuery: StateFlow<String> = _quranSearchQuery.asStateFlow()

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val quranVerses: StateFlow<List<QuranVerseEntity>> = combine(
        _selectedQuranSurahNumber,
        _quranSearchQuery
    ) { surah, query ->
        Pair(surah, query)
    }.flatMapLatest { (surah, query) ->
        if (query.isNotBlank()) {
            quranRepository.searchVerses(query.trim())
        } else {
            quranRepository.getVersesBySurah(surah)
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val lastReadVerse: StateFlow<QuranVerseEntity?> = quranRepository.getLastReadVerse()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    val bookmarkedQuranVerses: StateFlow<List<QuranVerseEntity>> = quranRepository.getBookmarkedVerses()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // --- HOME SCREEN STATE ---
    private val _dailyNasihotIndex = MutableStateFlow(0)
    val dailyNasihotIndex: StateFlow<Int> = _dailyNasihotIndex.asStateFlow()

    // Location for prayer times calculation
    private val _currentLocation = MutableStateFlow(settings.location)
    val currentLocation: StateFlow<UserLocationInfo> = _currentLocation.asStateFlow()

    private val _isLocating = MutableStateFlow(false)
    val isLocating: StateFlow<Boolean> = _isLocating.asStateFlow()

    private val _prayerTimes = MutableStateFlow(
        IslamicRepository.getTodayPrayerTimes(
            _currentLocation.value.latitude,
            _currentLocation.value.longitude,
            _currentLocation.value.cityName
        )
    )
    val prayerTimes: StateFlow<List<PrayerTimeInfo>> = _prayerTimes.asStateFlow()

    private val _countdownText = MutableStateFlow("00:00:00")
    val countdownText: StateFlow<String> = _countdownText.asStateFlow()

    private val _nextPrayerTitle = MutableStateFlow("Maghrib (Iftar)")
    val nextPrayerTitle: StateFlow<String> = _nextPrayerTitle.asStateFlow()

    private val _nextPrayerSubtitle = MutableStateFlow("Time to Break Fast & Pray")
    val nextPrayerSubtitle: StateFlow<String> = _nextPrayerSubtitle.asStateFlow()

    private val _countdownProgress = MutableStateFlow(0.65f)
    val countdownProgress: StateFlow<Float> = _countdownProgress.asStateFlow()

    // Dialog sheets on Home
    private val _showRamadanCalendar = MutableStateFlow(false)
    val showRamadanCalendar: StateFlow<Boolean> = _showRamadanCalendar.asStateFlow()

    private val _showHijriCalendar = MutableStateFlow(false)
    val showHijriCalendar: StateFlow<Boolean> = _showHijriCalendar.asStateFlow()

    private val _showPrayerTimesSheet = MutableStateFlow(false)
    val showPrayerTimesSheet: StateFlow<Boolean> = _showPrayerTimesSheet.asStateFlow()

    private val _showLocationPicker = MutableStateFlow(false)
    val showLocationPicker: StateFlow<Boolean> = _showLocationPicker.asStateFlow()

    private val _showLibrarySheet = MutableStateFlow(false)
    val showLibrarySheet: StateFlow<Boolean> = _showLibrarySheet.asStateFlow()

    private val _showTasbeehSheet = MutableStateFlow(false)
    val showTasbeehSheet: StateFlow<Boolean> = _showTasbeehSheet.asStateFlow()

    private val _showDuaSheet = MutableStateFlow(false)
    val showDuaSheet: StateFlow<Boolean> = _showDuaSheet.asStateFlow()

    private val _showQiblaSheet = MutableStateFlow(false)
    val showQiblaSheet: StateFlow<Boolean> = _showQiblaSheet.asStateFlow()

    private val _showAsmaulHusnaSheet = MutableStateFlow(false)
    val showAsmaulHusnaSheet: StateFlow<Boolean> = _showAsmaulHusnaSheet.asStateFlow()

    private val _showZakatSheet = MutableStateFlow(false)
    val showZakatSheet: StateFlow<Boolean> = _showZakatSheet.asStateFlow()

    private val _showIslamicEventsSheet = MutableStateFlow(false)
    val showIslamicEventsSheet: StateFlow<Boolean> = _showIslamicEventsSheet.asStateFlow()

    // --- TAZKIYAH & BAD HABIT BREAKER STATE ---
    private val _showTazkiyahSheet = MutableStateFlow(false)
    val showTazkiyahSheet: StateFlow<Boolean> = _showTazkiyahSheet.asStateFlow()

    private val _showWaswasahSosDialog = MutableStateFlow(false)
    val showWaswasahSosDialog: StateFlow<Boolean> = _showWaswasahSosDialog.asStateFlow()

    private val _badHabits = MutableStateFlow<List<BadHabit>>(emptyList())
    val badHabits: StateFlow<List<BadHabit>> = _badHabits.asStateFlow()

    // --- VIRAL GROWTH & DAWAH HUB STATE ---
    private val _showQuizDialog = MutableStateFlow(false)
    val showQuizDialog: StateFlow<Boolean> = _showQuizDialog.asStateFlow()

    private val _showDawahCardMakerDialog = MutableStateFlow(false)
    val showDawahCardMakerDialog: StateFlow<Boolean> = _showDawahCardMakerDialog.asStateFlow()

    private val _showFajrBuddyDialog = MutableStateFlow(false)
    val showFajrBuddyDialog: StateFlow<Boolean> = _showFajrBuddyDialog.asStateFlow()

    // --- TASBEEH TOOL STATE ---
    private val _tasbeehCount = MutableStateFlow(0)
    val tasbeehCount: StateFlow<Int> = _tasbeehCount.asStateFlow()

    private val _tasbeehTarget = MutableStateFlow(33)
    val tasbeehTarget: StateFlow<Int> = _tasbeehTarget.asStateFlow()

    private val _selectedDhikr = MutableStateFlow("সুবহানাল্লাহ (سُبْحَانَ اللَّهِ)")
    val selectedDhikr: StateFlow<String> = _selectedDhikr.asStateFlow()

    // --- AI CHAT STATE ---
    private val defaultScholarWelcome = ChatMessage(
        id = "welcome",
        text = "আসসালামু আলাইকুম ওয়া রাহমাতুল্লাহি ওয়া বারাকাতুহু।\nবিসমিল্লাহির রাহমানির রাহীম।\n\nআমি 'Scholar AI' (স্কলার এআই)—একটি এআই সহায়ক, যোগ্য আলেমের বিকল্প নই। পবিত্র কুরআন ও সহীহ সুন্নাহর আলোকে রোজা, নামাজ, যাকাত, দৈনন্দিন আমল বা যেকোনো শারঈ জিজ্ঞাসা শুদ্ধ বাংলায় করতে পারেন। আল্লাহ আমাদের সঠিক বুঝ দান করুন।",
        isFromUser = false
    )

    private val _chatMessages = MutableStateFlow(listOf(defaultScholarWelcome))
    val chatMessages: StateFlow<List<ChatMessage>> = _chatMessages.asStateFlow()

    private val _isAiThinking = MutableStateFlow(false)
    val isAiThinking: StateFlow<Boolean> = _isAiThinking.asStateFlow()


    // --- COMMUNITY FORUM STATE ---
    private val _forumPosts = MutableStateFlow(IslamicRepository.initialForumPosts)
    val forumPosts: StateFlow<List<ForumPost>> = _forumPosts.asStateFlow()

    private val _selectedForumCategory = MutableStateFlow("সবগুলো")
    val selectedForumCategory: StateFlow<String> = _selectedForumCategory.asStateFlow()

    // Local date changes trigger fresh Room queries without an app restart.
    private val _localToday = MutableStateFlow(amalRepository.getTodayDate())

    // --- AMAL TRACKER STATE (POWERED BY ROOM DATABASE) ---
    val todayAmalRecord: StateFlow<DailyAmalEntity> = amalRepository.observeDailyAmal(_localToday)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = DailyAmalEntity(date = amalRepository.getTodayDate())
        )

    val recentAmalHistory: StateFlow<List<DailyAmalEntity>> = amalRepository.getRecentAmalHistory(14)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val todayDhikrTotal: StateFlow<Int> = _localToday.flatMapLatest { amalRepository.getTodayDhikrTotal(it) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0
        )

    val allTimeDhikrTotal: StateFlow<Int> = amalRepository.getAllTimeDhikrTotal()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0
        )

    val recentDhikrLogs: StateFlow<List<DhikrLogEntity>> = _localToday.flatMapLatest { amalRepository.getDhikrLogsForDate(it) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val recentQuranLogs: StateFlow<List<QuranLogEntity>> = _localToday.flatMapLatest { amalRepository.getQuranLogsForDate(it) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val totalQuranPages: StateFlow<Int> = amalRepository.getAllTimePagesReadTotal()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0
        )

    val recentSalahLogs: StateFlow<List<SalahLogEntity>> = _localToday.flatMapLatest { amalRepository.getSalahLogsForDate(it) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _amalProgress = MutableStateFlow(AmalDailyProgress(quranSurah = "সূরা আল-কাহাফ"))
    val amalProgress: StateFlow<AmalDailyProgress> = _amalProgress.asStateFlow()

    // --- PROFILE & SETTINGS STATE ---
    private val _userName = MutableStateFlow(settings.userName)
    val userName: StateFlow<String> = _userName.asStateFlow()

    private val _isUserLoggedIn = MutableStateFlow(true)
    val isUserLoggedIn: StateFlow<Boolean> = _isUserLoggedIn.asStateFlow()

    private val _userEmail = MutableStateFlow("mdhasibulhasanofficial@gmail.com")
    val userEmail: StateFlow<String> = _userEmail.asStateFlow()

    private val _showAuthDialog = MutableStateFlow(false)
    val showAuthDialog: StateFlow<Boolean> = _showAuthDialog.asStateFlow()

    private val _spiritualGoal = MutableStateFlow(settings.spiritualGoal)
    val spiritualGoal: StateFlow<String> = _spiritualGoal.asStateFlow()

    private val _prayerNotificationsEnabled = MutableStateFlow(settings.prayerNotificationsEnabled)
    val prayerNotificationsEnabled: StateFlow<Boolean> = _prayerNotificationsEnabled.asStateFlow()

    private val _aiDailyRemindersEnabled = MutableStateFlow(settings.aiDailyRemindersEnabled)
    val aiDailyRemindersEnabled: StateFlow<Boolean> = _aiDailyRemindersEnabled.asStateFlow()

    private val _calculationMethod = MutableStateFlow("ইসলামিক ফাউন্ডেশন বাংলাদেশ (হানাফী)")
    val calculationMethod: StateFlow<String> = _calculationMethod.asStateFlow()

    // --- HIJRI CALENDAR STATE ---
    private val _hijriOffsetDays = MutableStateFlow(settings.hijriOffsetDays)
    val hijriOffsetDays: StateFlow<Int> = _hijriOffsetDays.asStateFlow()

    private val _currentHijriDate = MutableStateFlow(
        com.example.data.service.HijriCalendarService.getTodayHijriDate(_hijriOffsetDays.value)
    )
    val currentHijriDate: StateFlow<HijriDateInfo> = _currentHijriDate.asStateFlow()

    // --- BANGLA CALENDAR STATE ---
    private val _currentBanglaDate = MutableStateFlow(
        com.example.data.service.BanglaCalendarService.getBanglaDate()
    )
    val currentBanglaDate: StateFlow<BanglaDateInfo> = _currentBanglaDate.asStateFlow()

    private val _selectedHijriMonth = MutableStateFlow(_currentHijriDate.value.month)
    val selectedHijriMonth: StateFlow<Int> = _selectedHijriMonth.asStateFlow()

    private val _selectedHijriYear = MutableStateFlow(_currentHijriDate.value.year)
    val selectedHijriYear: StateFlow<Int> = _selectedHijriYear.asStateFlow()

    private val _hijriMonthData = MutableStateFlow(
        com.example.data.service.HijriCalendarService.getHijriMonthData(
            _currentHijriDate.value.year,
            _currentHijriDate.value.month,
            0
        )
    )
    val hijriMonthData: StateFlow<HijriMonthData> = _hijriMonthData.asStateFlow()

    private val _upcomingIslamicEvents = MutableStateFlow(
        com.example.data.service.HijriCalendarService.getUpcomingEvents(
            _currentHijriDate.value.year,
            _currentHijriDate.value.month,
            _currentHijriDate.value.day,
            0
        )
    )
    val upcomingIslamicEvents: StateFlow<List<IslamicEvent>> = _upcomingIslamicEvents.asStateFlow()

    private val _nextSignificantEvent = MutableStateFlow(
        com.example.data.service.HijriCalendarService.getNextSignificantEvent(
            _currentHijriDate.value.year,
            _currentHijriDate.value.month,
            _currentHijriDate.value.day,
            0
        )
    )
    val nextSignificantEvent: StateFlow<IslamicEvent?> = _nextSignificantEvent.asStateFlow()

    private var countdownJob: Job? = null
    private var nasihotAutoJob: Job? = null

    init {
        startLiveCountdown()
        startAutoUpdatingNasihot()
        observeRoomAmalData()
        syncPrayerAlerts()
        initializeQuranData()
        initializeTazkiyahHabits()
    }

    private fun startAutoUpdatingNasihot() {
        nasihotAutoJob?.cancel()
        nasihotAutoJob = viewModelScope.launch {
            while (true) {
                delay(8000) // auto rotate advice every 8 seconds
                val total = IslamicRepository.dailyNasihotList.size
                if (total > 0) {
                    _dailyNasihotIndex.value = (_dailyNasihotIndex.value + 1) % total
                }
            }
        }
    }

    private fun initializeQuranData() {
        viewModelScope.launch {
            quranRepository.ensureDatabaseInitialized()
        }
    }

    private fun observeRoomAmalData() {
        viewModelScope.launch {
            todayAmalRecord.collect { entity ->
                _amalProgress.value = AmalDailyProgress(
                    fajrDone = entity.fajrDone,
                    dhuhrDone = entity.dhuhrDone,
                    asrDone = entity.asrDone,
                    maghribDone = entity.maghribDone,
                    ishaDone = entity.ishaDone,
                    taraweehDone = entity.taraweehDone,
                    tahajjudDone = entity.tahajjudDone,
                    fastingToday = entity.fastingDone,
                    charityGiven = entity.charityDone,
                    morningAdhkarDone = entity.morningAdhkarDone,
                    eveningAdhkarDone = entity.eveningAdhkarDone,
                    quranJuz = entity.quranJuz,
                    quranSurah = entity.quranSurah,
                    quranPagesReadToday = entity.quranPagesReadToday,
                    quranDailyGoalPages = entity.quranDailyGoalPages,
                    dhikrTotalCount = entity.dhikrTotalCount
                )
            }
        }
    }

    private fun startLiveCountdown() {
        countdownJob?.cancel()
        countdownJob = viewModelScope.launch {
            while (true) {
                val date = amalRepository.getTodayDate()
                if (_localToday.value != date) {
                    _localToday.value = date
                    refreshHijriData()
                }
                val loc = _currentLocation.value
                val (title, subtitle, diffSec) = IslamicRepository.getNextPrayerCountdown(
                    latitude = loc.latitude,
                    longitude = loc.longitude,
                    locationName = loc.cityName
                )
                _nextPrayerTitle.value = title
                _nextPrayerSubtitle.value = subtitle

                val hours = diffSec / 3600
                val mins = (diffSec % 3600) / 60
                val secs = diffSec % 60
                _countdownText.value = String.format(Locale.US, "%02d:%02d:%02d", hours, mins, secs)

                // calculate approximate progress percentage of current interval
                val totalWindow = 6 * 3600f
                val elapsed = (totalWindow - diffSec).coerceAtLeast(0f)
                _countdownProgress.value = (elapsed / totalWindow).coerceIn(0.1f, 0.95f)

                _prayerTimes.value = IslamicRepository.getTodayPrayerTimes(
                    latitude = loc.latitude,
                    longitude = loc.longitude,
                    locationName = loc.cityName
                )

                // Continuously keep all 3 calendars (Hijri, Bangla, Gregorian) dynamically updated
                _currentHijriDate.value = com.example.data.service.HijriCalendarService.getTodayHijriDate(_hijriOffsetDays.value)
                _currentBanglaDate.value = com.example.data.service.BanglaCalendarService.getBanglaDate()

                delay(1000)
            }
        }
    }

    fun setLocation(locationInfo: UserLocationInfo) {
        settings.location = locationInfo
        _currentLocation.value = locationInfo
        _prayerTimes.value = IslamicRepository.getTodayPrayerTimes(
            latitude = locationInfo.latitude,
            longitude = locationInfo.longitude,
            locationName = locationInfo.cityName
        )
        startLiveCountdown()
        syncPrayerAlerts()
    }

    fun detectCurrentLocation(context: Context) {
        viewModelScope.launch {
            _isLocating.value = true
            val detected = LocationService.getDeviceLocation(context)
            if (detected != null) {
                setLocation(detected)
            }
            _isLocating.value = false
        }
    }

    fun openLocationPicker(show: Boolean) {
        _showLocationPicker.value = show
    }

    // --- HOME ACTIONS ---
    fun nextNasihot() {
        val total = IslamicRepository.dailyNasihotList.size
        if (total > 0) {
            _dailyNasihotIndex.value = (_dailyNasihotIndex.value + 1) % total
            startAutoUpdatingNasihot() // reset auto timer
        }
    }

    fun previousNasihot() {
        val total = IslamicRepository.dailyNasihotList.size
        if (total > 0) {
            _dailyNasihotIndex.value = (_dailyNasihotIndex.value - 1 + total) % total
            startAutoUpdatingNasihot() // reset auto timer
        }
    }

    fun openAuthDialog(show: Boolean) {
        _showAuthDialog.value = show
    }

    // Online authentication is not configured. Never simulate a verified login.
    fun login(email: String, name: String) { /* intentionally disabled */ }

    fun logout() {
        _isUserLoggedIn.value = false
        _userEmail.value = ""
        _userName.value = settings.userName
    }

    fun openRamadanCalendar(show: Boolean) { _showRamadanCalendar.value = show }
    fun openHijriCalendar(show: Boolean) {
        _showHijriCalendar.value = show
        if (show) {
            refreshHijriData()
        }
    }
    fun openPrayerTimesSheet(show: Boolean) { _showPrayerTimesSheet.value = show }
    fun openLibrarySheet(show: Boolean) { _showLibrarySheet.value = show }
    fun openTasbeehSheet(show: Boolean) { _showTasbeehSheet.value = show }
    fun openDuaSheet(show: Boolean) { _showDuaSheet.value = show }
    fun openQiblaSheet(show: Boolean) { _showQiblaSheet.value = show }
    fun openAsmaulHusnaSheet(show: Boolean) { _showAsmaulHusnaSheet.value = show }
    fun openZakatSheet(show: Boolean) { _showZakatSheet.value = show }
    fun openQuranReader(show: Boolean) { _showQuranReaderSheet.value = show }
    fun openIslamicEventsSheet(show: Boolean) {
        _showIslamicEventsSheet.value = show
        if (show) {
            refreshHijriData()
        }
    }
    fun openTazkiyahSheet(show: Boolean) { _showTazkiyahSheet.value = show }
    fun openWaswasahSosDialog(show: Boolean) { _showWaswasahSosDialog.value = show }
    fun openQuizDialog(show: Boolean) { _showQuizDialog.value = show }
    fun openDawahCardMakerDialog(show: Boolean) { _showDawahCardMakerDialog.value = show }
    fun openFajrBuddyDialog(show: Boolean) { _showFajrBuddyDialog.value = show }

    private fun initializeTazkiyahHabits() {
        val app = getApplication<Application>()
        _badHabits.value = BadHabitRepository.loadHabits(app)
    }

    fun markHabitCleanToday(habitId: String) {
        val today = BadHabitRepository.getTodayDateString()
        val updated = _badHabits.value.map { h ->
            if (h.id == habitId) {
                if (h.lastCleanDate == today) {
                    h
                } else {
                    h.copy(
                        streakDays = h.streakDays + 1,
                        totalCleanDays = h.totalCleanDays + 1,
                        lastCleanDate = today
                    )
                }
            } else {
                h
            }
        }
        _badHabits.value = updated
        BadHabitRepository.saveHabits(getApplication(), updated)
    }

    fun resetHabitRelapse(habitId: String) {
        val updated = _badHabits.value.map { h ->
            if (h.id == habitId) {
                h.copy(
                    streakDays = 0,
                    lastCleanDate = ""
                )
            } else {
                h
            }
        }
        _badHabits.value = updated
        BadHabitRepository.saveHabits(getApplication(), updated)
    }

    fun addCustomBadHabit(titleBn: String, reasonBn: String, category: String) {
        val newHabit = BadHabit(
            id = "custom_" + System.currentTimeMillis(),
            titleBn = titleBn.trim(),
            category = category.ifBlank { "ব্যক্তিগত অভ্যাস" },
            iconCategory = "flag",
            streakDays = 0,
            lastCleanDate = "",
            totalCleanDays = 0,
            reasonToQuit = reasonBn.trim().ifBlank { "আল্লাহর সন্তুষ্টি ও আত্মশুদ্ধির উদ্দেশ্যে এই অভ্যাস ত্যাগ করছি।" },
            spiritualRemedy = "নিয়মিত ইস্তিগফার পাঠ করুন এবং নির্জনতায় সময় কাটানো পরিহার করুন।",
            quranAyahOrHadith = "নিশ্চয়ই সৎকাজ অসৎকাজকে মিটিয়ে দেয়।",
            reference = "সূরা হুদ: ১১৪",
            isTracking = true
        )
        val updated = _badHabits.value + newHabit
        _badHabits.value = updated
        BadHabitRepository.saveHabits(getApplication(), updated)
    }

    fun deleteBadHabit(habitId: String) {
        val updated = _badHabits.value.filterNot { it.id == habitId }
        _badHabits.value = updated
        BadHabitRepository.saveHabits(getApplication(), updated)
    }

    // --- QURAN READER ACTIONS ---
    fun selectSurah(surahNumber: Int) {
        _selectedQuranSurahNumber.value = surahNumber
        _quranSearchQuery.value = "" // Reset search when picking a surah
    }

    fun setQuranSearchQuery(query: String) {
        _quranSearchQuery.value = query
    }

    fun toggleQuranBookmark(verse: QuranVerseEntity) {
        viewModelScope.launch {
            quranRepository.toggleBookmark(verse)
        }
    }

    fun markVerseAsLastRead(verse: QuranVerseEntity) {
        viewModelScope.launch {
            quranRepository.setLastRead(verse)
        }
    }

    // --- HIJRI CALENDAR ACTIONS ---
    fun setHijriOffset(offset: Int) {
        settings.hijriOffsetDays = offset
        _hijriOffsetDays.value = settings.hijriOffsetDays
        refreshHijriData()
    }

    fun nextHijriMonth() {
        var m = _selectedHijriMonth.value + 1
        var y = _selectedHijriYear.value
        if (m > 12) {
            m = 1
            y += 1
        }
        _selectedHijriMonth.value = m
        _selectedHijriYear.value = y
        _hijriMonthData.value = com.example.data.service.HijriCalendarService.getHijriMonthData(
            y, m, _hijriOffsetDays.value
        )
    }

    fun previousHijriMonth() {
        var m = _selectedHijriMonth.value - 1
        var y = _selectedHijriYear.value
        if (m < 1) {
            m = 12
            y -= 1
        }
        _selectedHijriMonth.value = m
        _selectedHijriYear.value = y
        _hijriMonthData.value = com.example.data.service.HijriCalendarService.getHijriMonthData(
            y, m, _hijriOffsetDays.value
        )
    }

    fun resetToCurrentHijriMonth() {
        val today = com.example.data.service.HijriCalendarService.getTodayHijriDate(_hijriOffsetDays.value)
        _selectedHijriMonth.value = today.month
        _selectedHijriYear.value = today.year
        _hijriMonthData.value = com.example.data.service.HijriCalendarService.getHijriMonthData(
            today.year, today.month, _hijriOffsetDays.value
        )
    }

    fun refreshHijriData() {
        val today = com.example.data.service.HijriCalendarService.getTodayHijriDate(_hijriOffsetDays.value)
        _currentHijriDate.value = today
        _hijriMonthData.value = com.example.data.service.HijriCalendarService.getHijriMonthData(
            _selectedHijriYear.value, _selectedHijriMonth.value, _hijriOffsetDays.value
        )
        _upcomingIslamicEvents.value = com.example.data.service.HijriCalendarService.getUpcomingEvents(
            today.year, today.month, today.day, _hijriOffsetDays.value
        )
        _nextSignificantEvent.value = com.example.data.service.HijriCalendarService.getNextSignificantEvent(
            today.year, today.month, today.day, _hijriOffsetDays.value
        )
    }

    // --- DIGITAL TASBEEH ACTIONS ---
    fun incrementTasbeeh() {
        vibrateFeedback()
        val current = _tasbeehCount.value + 1
        if (current >= _tasbeehTarget.value) {
            _tasbeehCount.value = 0
            viewModelScope.launch {
                amalRepository.recordDhikrSession(
                    dhikrName = _selectedDhikr.value,
                    arabicText = "",
                    count = _tasbeehTarget.value,
                    target = _tasbeehTarget.value
                )
            }
        } else {
            _tasbeehCount.value = current
            viewModelScope.launch {
                amalRepository.incrementDailyDhikr(1)
            }
        }
    }

    fun resetTasbeeh() {
        _tasbeehCount.value = 0
    }

    fun setDhikr(dhikr: String, target: Int = 33) {
        _selectedDhikr.value = dhikr
        _tasbeehTarget.value = target
        _tasbeehCount.value = 0
    }

    private fun vibrateFeedback() {
        try {
            val context = getApplication<Application>()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                vibratorManager?.defaultVibrator?.vibrate(
                    VibrationEffect.createOneShot(35, VibrationEffect.DEFAULT_AMPLITUDE)
                )
            } else {
                @Suppress("DEPRECATION")
                val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrator?.vibrate(VibrationEffect.createOneShot(35, VibrationEffect.DEFAULT_AMPLITUDE))
                } else {
                    @Suppress("DEPRECATION")
                    vibrator?.vibrate(35)
                }
            }
        } catch (_: Exception) {}
    }

    // --- AI CHAT ACTIONS ---
    fun sendChatMessage(userText: String, isVoice: Boolean = false) {
        if (userText.isBlank()) return

        val historySnapshot = _chatMessages.value
        val userMsg = ChatMessage(text = userText.trim(), isFromUser = true, isVoiceMessage = isVoice)
        _chatMessages.value = _chatMessages.value + userMsg
        _isAiThinking.value = true

        viewModelScope.launch {
            try {
                val replyText = GeminiClient.askScholarChat(historySnapshot, userText.trim())
                val aiMsg = ChatMessage(text = replyText, isFromUser = false)
                _chatMessages.value = _chatMessages.value + aiMsg
            } catch (e: Exception) {
                val errorMsg = ChatMessage(
                    text = GeminiClient.UNAVAILABLE_MESSAGE,
                    isFromUser = false
                )
                _chatMessages.value = _chatMessages.value + errorMsg
            } finally {
                _isAiThinking.value = false
            }
        }
    }

    fun clearChat() {
        _chatMessages.value = listOf(defaultScholarWelcome)
    }



    // --- COMMUNITY FORUM ACTIONS ---
    fun setForumCategory(category: String) {
        _selectedForumCategory.value = category
    }

    fun togglePostLike(postId: String) {
        _forumPosts.value = _forumPosts.value.map { post ->
            if (post.id == postId) {
                val newLiked = !post.isLiked
                val newLikes = if (newLiked) post.likes + 1 else (post.likes - 1).coerceAtLeast(0)
                post.copy(isLiked = newLiked, likes = newLikes)
            } else post
        }
    }

    fun togglePostAmin(postId: String) {
        _forumPosts.value = _forumPosts.value.map { post ->
            if (post.id == postId) {
                val newAmin = !post.isAminGiven
                val count = if (newAmin) post.aminCount + 1 else (post.aminCount - 1).coerceAtLeast(0)
                post.copy(isAminGiven = newAmin, aminCount = count)
            } else post
        }
    }

    fun togglePostSaved(postId: String) {
        _forumPosts.value = _forumPosts.value.map { post ->
            if (post.id == postId) {
                post.copy(isSaved = !post.isSaved)
            } else post
        }
    }

    fun incrementShareCount(postId: String) {
        _forumPosts.value = _forumPosts.value.map { post ->
            if (post.id == postId) {
                post.copy(sharesCount = post.sharesCount + 1)
            } else post
        }
    }

    fun addForumPost(
        question: String,
        category: String,
        authorRole: String = "দ্বীনি ভাই",
        quoteText: String? = null,
        quoteReference: String? = null
    ) {
        if (question.isBlank()) return

        val newId = "post_" + System.currentTimeMillis()
        val newPost = ForumPost(
            id = newId,
            authorName = _userName.value,
            timeAgo = "এইমাত্র",
            category = category,
            questionText = question.trim(),
            likes = 1,
            isLiked = true,
            authorRole = authorRole,
            aminCount = if (category.contains("দোয়া")) 1 else 0,
            isAminGiven = category.contains("দোয়া"),
            quoteOrAyatText = quoteText?.takeIf { it.isNotBlank() },
            quoteReference = quoteReference?.takeIf { it.isNotBlank() },
            replies = emptyList()
        )

        _forumPosts.value = listOf(newPost) + _forumPosts.value

        // Automatically trigger AI Scholar to provide an authentic verified response!
        viewModelScope.launch {
            delay(1200) // gentle natural delay
            val aiResponse = GeminiClient.generateModeratorSummary(question)
            if (aiResponse == GeminiClient.UNAVAILABLE_MESSAGE) return@launch
            val aiReply = ForumReply(
                id = "rep_ai_" + System.currentTimeMillis(),
                authorName = "আল-হুজুর এআই স্কলার",
                replyText = aiResponse,
                timeAgo = "এইমাত্র",
                isAiModerator = true,
                verifiedReference = null
            )

            _forumPosts.value = _forumPosts.value.map { post ->
                if (post.id == newId) {
                    post.copy(replies = listOf(aiReply) + post.replies)
                } else post
            }
        }
    }

    fun addReplyToPost(postId: String, replyText: String) {
        if (replyText.isBlank()) return
        val newReply = ForumReply(
            id = "rep_" + System.currentTimeMillis(),
            authorName = _userName.value,
            replyText = replyText.trim(),
            timeAgo = "Just now",
            isAiModerator = false
        )
        _forumPosts.value = _forumPosts.value.map { post ->
            if (post.id == postId) {
                post.copy(replies = post.replies + newReply)
            } else post
        }
    }

    // --- AMAL TRACKER ACTIONS (PERSISTED IN ROOM DATABASE) ---
    fun togglePrayer(prayer: String) {
        viewModelScope.launch {
            amalRepository.togglePrayer(prayer)
        }
    }

    fun toggleFasting() {
        viewModelScope.launch {
            amalRepository.toggleFasting()
        }
    }

    fun addQuranPages(pages: Int) {
        viewModelScope.launch {
            amalRepository.addQuranPages(pages)
        }
    }

    fun setQuranProgress(juz: Int, surah: String, pagesToday: Int, goalPages: Int = 20) {
        viewModelScope.launch {
            amalRepository.updateQuranBookmark(juz, surah, pagesToday, goalPages)
        }
    }

    fun logQuranSession(
        surah: String,
        juz: Int,
        pages: Int,
        durationMinutes: Int = 0,
        note: String = ""
    ) {
        viewModelScope.launch {
            amalRepository.logQuranSession(surah, juz, pages, durationMinutes, note)
        }
    }

    fun recordSalahDetail(prayerName: String, isPrayed: Boolean, prayedInJamat: Boolean) {
        viewModelScope.launch {
            amalRepository.recordSalahDetail(prayerName, isPrayed, prayedInJamat)
        }
    }

    fun recordDhikrSession(
        dhikrName: String,
        arabicText: String = "",
        count: Int,
        target: Int = 33
    ) {
        viewModelScope.launch {
            amalRepository.recordDhikrSession(dhikrName, arabicText, count, target)
        }
    }

    fun incrementDhikrCounter(amount: Int = 1) {
        viewModelScope.launch {
            amalRepository.incrementDailyDhikr(amount)
        }
    }

    // --- PROFILE & SETTINGS ACTIONS ---
    fun togglePrayerNotifications() {
        _prayerNotificationsEnabled.value = !_prayerNotificationsEnabled.value
        settings.prayerNotificationsEnabled = _prayerNotificationsEnabled.value
        syncPrayerAlerts()
    }

    fun syncPrayerAlerts() {
        try {
            val app = getApplication<Application>()
            if (_prayerNotificationsEnabled.value) {
                val loc = _currentLocation.value
                com.example.data.service.PrayerNotificationScheduler.scheduleAllPrayerAlerts(
                    context = app,
                    latitude = loc.latitude,
                    longitude = loc.longitude,
                    locationName = loc.cityName
                )
            } else {
                com.example.data.service.PrayerNotificationScheduler.cancelAllPrayerAlerts(app)
            }
        } catch (e: Exception) {
            android.util.Log.e("AlHujurViewModel", "Error syncing prayer alerts: ${e.message}")
        }
    }

    fun sendTestPrayerAlert() {
        try {
            val app = getApplication<Application>()
            val nextPrayer = _prayerTimes.value.find { it.isNext } ?: _prayerTimes.value.firstOrNull()
            val pName = nextPrayer?.name?.split(" ")?.firstOrNull() ?: "যোহর"
            val pTime = nextPrayer?.timeString ?: "০১:১৫ অপরাহ্ন"
            com.example.data.service.PrayerNotificationScheduler.sendInstantTestAlert(app, pName, pTime)
        } catch (e: Exception) {
            android.util.Log.e("AlHujurViewModel", "Error sending test alert: ${e.message}")
        }
    }

    fun toggleAiReminders() {
        _aiDailyRemindersEnabled.value = !_aiDailyRemindersEnabled.value
        settings.aiDailyRemindersEnabled = _aiDailyRemindersEnabled.value
    }

    fun updateProfile(name: String, goal: String, calcMethod: String) {
        _userName.value = name
        settings.userName = name
        _spiritualGoal.value = goal
        settings.spiritualGoal = goal
        _calculationMethod.value = calcMethod
    }

    override fun onCleared() {
        super.onCleared()
        countdownJob?.cancel()
    }
}
