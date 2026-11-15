package br.edu.arquitetura.newsdemo.app

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.*
import android.content.Context
import androidx.test.core.app.ApplicationProvider
import java.io.File
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class NewsAppTest {
    @get:Rule val compose = createAndroidComposeRule<MainActivity>()
    private val title = "Arquitetura que facilita mudanças"
    private fun awaitFeed() {
        compose.waitUntil(10000) { compose.onAllNodesWithTag("favorite-01").fetchSemanticsNodes().isNotEmpty() }
    }
    private fun ensureNotSaved() {
        awaitFeed()
        val saved = compose.onAllNodesWithContentDescription("Remover dos favoritos: $title").fetchSemanticsNodes()
        if (saved.isNotEmpty()) {
            compose.onNodeWithTag("favorite-01").performClick()
            compose.waitUntil(5000) {
                compose.onAllNodesWithContentDescription("Salvar nos favoritos: $title").fetchSemanticsNodes().isNotEmpty()
            }
        }
    }
    @Test fun detailFavoriteAndRemovalStayConsistent() {
        ensureNotSaved()
        compose.onNodeWithTag("news-01").performClick()
        compose.onNodeWithText(title).assertIsDisplayed()
        compose.onNodeWithTag("favorite-01").performClick()
        compose.waitUntil(5000) {
            compose.onAllNodesWithContentDescription("Remover dos favoritos: $title").fetchSemanticsNodes().isNotEmpty()
        }
        compose.onNodeWithContentDescription("Voltar").performClick()
        compose.onNodeWithTag("nav-favorites").performClick()
        compose.onNodeWithText(title).assertIsDisplayed()
        compose.onNodeWithTag("favorite-01").performClick()
        compose.waitUntil(5000) { compose.onAllNodesWithText("Seus favoritos aparecem aqui").fetchSemanticsNodes().isNotEmpty() }
        compose.onNodeWithTag("nav-news").performClick()
        compose.onNodeWithContentDescription("Salvar nos favoritos: $title").assertIsDisplayed()
    }
    @Test fun favoriteControlHasLabelAndMinimumTouchArea() {
        awaitFeed()
        val checked = mutableListOf<String>()
        fun check(node: SemanticsNodeInteraction, label: String) {
            val bounds = node.getUnclippedBoundsInRoot()
            assertTrue((bounds.right - bounds.left).value >= 48f)
            assertTrue((bounds.bottom - bounds.top).value >= 48f)
            node.assertHasClickAction().assertIsEnabled()
            checked += label
        }
        check(compose.onNodeWithTag("favorite-01"), "marcador-lista")
        assertTrue(compose.onAllNodes(hasContentDescription("favoritos", substring = true)).fetchSemanticsNodes().isNotEmpty())
        check(compose.onNodeWithTag("nav-news").assert(hasText("Notícias")), "aba-noticias")
        check(compose.onNodeWithTag("nav-favorites").assert(hasText("Favoritos")), "aba-favoritos")
        compose.onNodeWithTag("news-01").performClick()
        compose.onNodeWithText(title).assertIsDisplayed()
        check(compose.onNodeWithTag("favorite-01"), "marcador-detalhes")
        check(compose.onNodeWithTag("back").assert(hasContentDescription("Voltar")), "voltar")
        val context = ApplicationProvider.getApplicationContext<Context>()
        android.util.Log.i("NewsDemoAccessibility", "verified_controls=5;passed=5;minimum_dp=48")
        File(context.filesDir, "accessibility-checks.json").writeText(
            "{\"checked_controls\":[" + checked.joinToString(",") { "\"$it\"" } + "],\"passed\":5,\"total\":5}")

    }
}
