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
import com.example.data.local.entity.DailyAmalEntity
import com.example.data.local.entity.DhikrLogEntity
import com.example.data.local.entity.QuranLogEntity
import com.example.data.local.entity.SalahLogEntity
import com.example.data.model.*
import com.example.data.repository.AmalRepository
import com.example.data.repository.IslamicRepository
import com.example.data.service.LocationService
import com.example.data.service.UserLocationInfo
import com.example.data.service.PrayerTimeCalculatorService
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Locale

class AlHujurViewModel(application: Application) : AndroidViewModel(application) {

    // --- ROOM DATABASE REPOSITORY ---
    private val database = AppDatabase.getInstance(application)
    val amalRepository = AmalRepository(database.amalDao())

    // --- HOME SCREEN STATE ---
    private val _dailyNasihotIndex = MutableStateFlow(0)
    val dailyNasihotIndex: StateFlow<Int> = _dailyNasihotIndex.asStateFlow()

    // Location for prayer times calculation
    private val _currentLocation = MutableStateFlow(LocationService.DHAKA)
    val currentLocation: StateFlow<UserLocationInfo> = _currentLocation.asStateFlow()

    private val _isLocating = MutableStateFlow(false)
    val isLocating: StateFlow<Boolean> = _isLocating.asStateFlow()

    private val _prayerTimes = MutableStateFlow(
        IslamicRepository.getTodayPrayerTimes(
            LocationService.DHAKA.latitude,
            LocationService.DHAKA.longitude,
            LocationService.DHAKA.cityName
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
        text = "আসসালামু আলাইকুম ওয়া রাহমাতুল্লাহি ওয়া বারাকাতুহু।\nবিসমিল্লাহির রাহমানির রাহীম।\n\nআমি 'Scholar AI' (স্কলার এআই)—আপনার নির্ভরযোগ্য ইসলামিক পণ্ডিত ও দ্বীনি মাসআলা সহায়ক। পবিত্র কুরআন ও সহীহ সুন্নাহর আলোকে রোজা, নামাজ, যাকাত, দৈনন্দিন আমল বা যেকোনো শারঈ জিজ্ঞাসা শুদ্ধ বাংলায় করতে পারেন। আল্লাহ আমাদের সঠিক বুঝ দান করুন।",
        isFromUser = false
    )

    private val _chatMessages = MutableStateFlow(listOf(defaultScholarWelcome))
    val chatMessages: StateFlow<List<ChatMessage>> = _chatMessages.asStateFlow()

    private val _isAiThinking = MutableStateFlow(false)
    val isAiThinking: StateFlow<Boolean> = _isAiThinking.asStateFlow()

    private val _isVoiceRecording = MutableStateFlow(false)
    val isVoiceRecording: StateFlow<Boolean> = _isVoiceRecording.asStateFlow()

    // --- COMMUNITY FORUM STATE ---
    private val _forumPosts = MutableStateFlow(IslamicRepository.initialForumPosts)
    val forumPosts: StateFlow<List<ForumPost>> = _forumPosts.asStateFlow()

    private val _selectedForumCategory = MutableStateFlow("সবগুলো")
    val selectedForumCategory: StateFlow<String> = _selectedForumCategory.asStateFlow()

    // --- AMAL TRACKER STATE (POWERED BY ROOM DATABASE) ---
    val todayAmalRecord: StateFlow<DailyAmalEntity> = amalRepository.getTodayAmal()
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

    val todayDhikrTotal: StateFlow<Int> = amalRepository.getTodayDhikrTotal()
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

    val recentDhikrLogs: StateFlow<List<DhikrLogEntity>> = amalRepository.getDhikrLogsForDate()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val recentQuranLogs: StateFlow<List<QuranLogEntity>> = amalRepository.getQuranLogsForDate()
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

    val recentSalahLogs: StateFlow<List<SalahLogEntity>> = amalRepository.getSalahLogsForDate()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _amalProgress = MutableStateFlow(AmalDailyProgress(quranSurah = "সূরা আল-কাহাফ"))
    val amalProgress: StateFlow<AmalDailyProgress> = _amalProgress.asStateFlow()

    // --- PROFILE & SETTINGS STATE ---
    private val _userName = MutableStateFlow("হাসিবুল হাসান (বাংলাদেশ)")
    val userName: StateFlow<String> = _userName.asStateFlow()

    private val _spiritualGoal = MutableStateFlow("এই রমজানে সম্পূর্ণ ৩০ পারা কুরআন খতম করা")
    val spiritualGoal: StateFlow<String> = _spiritualGoal.asStateFlow()

    private val _prayerNotificationsEnabled = MutableStateFlow(true)
    val prayerNotificationsEnabled: StateFlow<Boolean> = _prayerNotificationsEnabled.asStateFlow()

    private val _aiDailyRemindersEnabled = MutableStateFlow(true)
    val aiDailyRemindersEnabled: StateFlow<Boolean> = _aiDailyRemindersEnabled.asStateFlow()

    private val _calculationMethod = MutableStateFlow("ইসলামিক ফাউন্ডেশন বাংলাদেশ (হানাফী)")
    val calculationMethod: StateFlow<String> = _calculationMethod.asStateFlow()

    // --- HIJRI CALENDAR STATE ---
    private val _hijriOffsetDays = MutableStateFlow(0)
    val hijriOffsetDays: StateFlow<Int> = _hijriOffsetDays.asStateFlow()

    private val _currentHijriDate = MutableStateFlow(
        com.example.data.service.HijriCalendarService.getTodayHijriDate(0)
    )
    val currentHijriDate: StateFlow<HijriDateInfo> = _currentHijriDate.asStateFlow()

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

    init {
        startLiveCountdown()
        observeRoomAmalData()
        syncPrayerAlerts()
    }

    private fun observeRoomAmalData() {
        viewModelScope.launch {
            amalRepository.getTodayAmal().collect { entity ->
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
                delay(1000)
            }
        }
    }

    fun setLocation(locationInfo: UserLocationInfo) {
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
        _dailyNasihotIndex.value = (_dailyNasihotIndex.value + 1) % total
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

    // --- HIJRI CALENDAR ACTIONS ---
    fun setHijriOffset(offset: Int) {
        _hijriOffsetDays.value = offset
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
                    text = GeminiClient.getOfflineScholarGuidance(userText),
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

    fun toggleVoiceRecording() {
        if (_isVoiceRecording.value) {
            // End recording and send prompt
            _isVoiceRecording.value = false
            val sampleQuestions = listOf(
                "রমজানের শেষ দশকে ইতিকাফ ও সদকাতুল ফিতরের নিয়ম কী?",
                "নামাজে একাগ্রতা ও খুশু-খুজু বাড়ানোর উপায় কী?",
                "সফরে রোজা রাখা ও কাজা আদায় করার বিধান কী?",
                "মনের দুশ্চিন্তা ও পেরেশানি দূর করার জন্য কোন দোয়াটি পড়ব?"
            )
            val selected = sampleQuestions.random()
            sendChatMessage(selected, isVoice = true)
        } else {
            _isVoiceRecording.value = true
        }
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

    fun addForumPost(question: String, category: String) {
        if (question.isBlank()) return

        val newId = "post_" + System.currentTimeMillis()
        val newPost = ForumPost(
            id = newId,
            authorName = _userName.value,
            timeAgo = "Just now",
            category = category,
            questionText = question.trim(),
            likes = 1,
            isLiked = true,
            replies = emptyList()
        )

        _forumPosts.value = listOf(newPost) + _forumPosts.value

        // Automatically trigger AI Moderator to provide an authentic verified response!
        viewModelScope.launch {
            delay(1200) // gentle natural delay
            val aiResponse = GeminiClient.generateModeratorSummary(question)
            val aiReply = ForumReply(
                id = "rep_ai_" + System.currentTimeMillis(),
                authorName = "AI Moderator",
                replyText = aiResponse,
                timeAgo = "Just now",
                isAiModerator = true,
                verifiedReference = "Al-Hujur AI Scholarly Review"
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
    }

    fun updateProfile(name: String, goal: String, calcMethod: String) {
        _userName.value = name
        _spiritualGoal.value = goal
        _calculationMethod.value = calcMethod
    }

    override fun onCleared() {
        super.onCleared()
        countdownJob?.cancel()
    }
}
