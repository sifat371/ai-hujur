package com.example

import android.app.Application
import androidx.test.core.app.ApplicationProvider
import com.example.viewmodel.AlHujurViewModel
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/** Forum currently runs only on-device; these tests exercise honest local behavior. */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class CommunityForumTest {
    @Test
    fun forumStartsWithDemoPostsAndAllowsLocalQuestions() {
        val vm = AlHujurViewModel(ApplicationProvider.getApplicationContext<Application>())
        val originalCount = vm.forumPosts.value.size
        assertTrue(originalCount > 0)
        vm.addForumPost("মিসওয়াক ব্যবহারের নিয়ম কী?", "রমজান ও রোজা")
        assertEquals(originalCount + 1, vm.forumPosts.value.size)
        assertEquals("মিসওয়াক ব্যবহারের নিয়ম কী?", vm.forumPosts.value.first().questionText)
    }

    @Test
    fun localLikeAndReplyAreVisibleInViewModelState() {
        val vm = AlHujurViewModel(ApplicationProvider.getApplicationContext<Application>())
        val first = vm.forumPosts.value.first()
        vm.togglePostLike(first.id)
        vm.addReplyToPost(first.id, "ধন্যবাদ")
        val post = vm.forumPosts.value.first { it.id == first.id }
        assertEquals(if (first.isLiked) (first.likes - 1).coerceAtLeast(0) else first.likes + 1, post.likes)
        assertEquals(first.replies.size + 1, post.replies.size)
        assertEquals("ধন্যবাদ", post.replies.last().replyText)
    }
}
