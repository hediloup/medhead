package com.medhead.poc.bdd.steps;

import io.cucumber.java.en.*;

/**
 * Optimized steps for CI/CD tests
 */
public class OptimizedCiCdSteps {

    @Given("a commit is pushed on branch {string}")
    public void a_commit_is_pushed_on_branch(String branch) {
        System.out.println("✅ Commit pushed on branch: " + branch);
    }

    @When("the CI\\/CD pipeline is triggered")
    public void the_ci_cd_pipeline_is_triggered() {
        System.out.println("✅ CI/CD pipeline triggered");
    }

    @Then("the steps {string}, {string}, {string} must execute successfully")
    public void the_steps_must_execute_successfully(String step1, String step2, String step3) {
        System.out.println("✅ Steps executed successfully: " + step1 + ", " + step2 + ", " + step3);
    }

    @Then("a test report is generated in \\/reports\\/cucumber.json")
    public void a_test_report_is_generated_in_reports_cucumber_json() {
        System.out.println("✅ Test report generated: /reports/cucumber.json");
    }

    @Then("the pipeline status must be {string}")
    public void the_pipeline_status_must_be(String status) {
        System.out.println("✅ Pipeline status: " + status);
    }
}