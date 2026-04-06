package br.com.agibank.qa.bdd.web;

import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

import static io.cucumber.junit.platform.engine.Constants.FILTER_TAGS_PROPERTY_NAME;
import static io.cucumber.junit.platform.engine.Constants.GLUE_PROPERTY_NAME;
import static io.cucumber.junit.platform.engine.Constants.PLUGIN_PROPERTY_NAME;

@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features/web/blog_search.feature")
@ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "br.com.agibank.qa.bdd.web")
@ConfigurationParameter(key = FILTER_TAGS_PROPERTY_NAME, value = "@web")
@ConfigurationParameter(key = PLUGIN_PROPERTY_NAME, value = "pretty, html:target/cucumber/web-report.html, json:target/cucumber/web-report.json")
public class RunBlogSearchBddTest {
}
