package com.atlassian.performance.tools.jiraactions.api.page

import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.support.ui.ExpectedConditions.*
import java.time.Duration

class IssueNavigatorPage(
    private val driver: WebDriver,
    val jql: String
) {

    /**
     * Requires an issue detail pane to be visible.
     * For example, if there are no issues found, this will time out.
     */
    fun waitForIssueNavigator(): IssueNavigatorPage {
        val jiraErrors = JiraErrors(driver)
        driver.wait(
            Duration.ofSeconds(30),
            or(
                and(
                    or(
                        presenceOfElementLocated(By.cssSelector("ol.issue-list")),
                        presenceOfElementLocated(By.id("issuetable")),
                        presenceOfElementLocated(By.id("issue-content"))
                    ),
                    presenceOfElementLocated(By.id("key-val")),
                    presenceOfElementLocated(By.className("issue-body-content"))
                ),
                presenceOfElementLocated(By.className("no-results-hint")),
                jiraErrors.anyCommonError()
            )
        )
        return this
    }

    fun getIssueKeys(): Set<String> {
        return driver
            .findElements(By.className("issue-link-key"))
            .map { it.text }
            .map { it.trim() }
            .toSet()
    }

    fun issueView(): IssuePage {
        return IssuePage(driver)
    }

    fun selectedIssueId(): Long {
        return IssuePage(driver).getIssueId()
    }

    fun getTotalResults(): Int = driver
        .findElements(By.className("showing"))
        .singleOrNull()
        ?.text
        ?.trim()
        ?.substringAfter("of ")
        ?.toInt() ?: 0
}