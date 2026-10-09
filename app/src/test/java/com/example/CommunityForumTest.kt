package com.example

import android.app.Application
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.core.app.ApplicationProvider
import com.example.ui.screens.CommunityForumScreen
import com.example.viewmodel.AlHujurViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class CommunityForumTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testCommunityForumDisplaysInitialPostsAndAllowsPostingQuestion() {
        val app = ApplicationProvider.getApplicationContext<Application>()
        val viewModel = AlHujurViewModel(app)

        composeTestRule.setContent {
            CommunityForumScreen(viewModel = viewModel)
        }

        // Verify title & initial posts are rendered
        composeTestRule.onNodeWithText("কমিউনিটি ফোরাম").assertIsDisplayed()
        composeTestRule.onNodeWithTag("ask_question_button").assertIsDisplayed()

        // Verify initial post exists
        val initialCount = viewModel.forumPosts.value.size
        assertTrue("Forum should start with initial Islamic threads", initialCount > 0)

        // Open Dialog to Ask a new question
        composeTestRule.onNodeWithTag("ask_question_button").performClick()

        // Submit new question
        composeTestRule.onNodeWithTag("question_text_input").performTextInput("রমজানে মিসওয়াক ব্যবহারের সঠিক নিয়ম কী?")
        composeTestRule.onNodeWithTag("submit_question_button").performClick()

        // Verify new post is added to StateFlow
        assertEquals("Post count should increase by 1", initialCount + 1, viewModel.forumPosts.value.size)
        val latestPost = viewModel.forumPosts.value.first()
        assertEquals("রমজানে মিসওয়াক ব্যবহারের সঠিক নিয়ম কী?", latestPost.questionText)
    }

    @Test
    fun testCommunityForumAllowsAddingReplyAndLiking() {
        val app = ApplicationProvider.getApplicationContext<Application>()
        val viewModel = AlHujurViewModel(app)
        val firstPostId = viewModel.forumPosts.value.first().id
        val initialRepliesCount = viewModel.forumPosts.value.first().replies.size
        val initialLikes = viewModel.forumPosts.value.first().likes

        // Like the post
        viewModel.togglePostLike(firstPostId)
        val updatedLikes = viewModel.forumPosts.value.first { it.id == firstPostId }.likes
        assertEquals("Likes should increase by 1", initialLikes + 1, updatedLikes)

        // Add a reply
        viewModel.addReplyToPost(firstPostId, "জাযাকাল্লাহু খাইরান, সুন্দর ও দলিলভিত্তিক সমাধান।")
        val updatedPost = viewModel.forumPosts.value.first { it.id == firstPostId }
        assertEquals("Replies should increase by 1", initialRepliesCount + 1, updatedPost.replies.size)
        assertEquals("জাযাকাল্লাহু খাইরান, সুন্দর ও দলিলভিত্তিক সমাধান।", updatedPost.replies.last().replyText)
    }
}
