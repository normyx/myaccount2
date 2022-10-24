package org.mgoulene.cucumber;

import io.cucumber.spring.CucumberContextConfiguration;
import org.mgoulene.IntegrationTest;
import org.springframework.test.context.web.WebAppConfiguration;

@CucumberContextConfiguration
@IntegrationTest
@WebAppConfiguration
public class CucumberTestContextConfiguration {}
