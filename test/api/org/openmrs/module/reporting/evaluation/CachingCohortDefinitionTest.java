/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.reporting.evaluation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.Cohort;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.cohort.definition.GenderCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.service.CohortDefinitionService;
import org.openmrs.module.reporting.definition.configuration.ConfigurationPropertyCachingStrategy;
import org.openmrs.test.BaseModuleContextSensitiveTest;

public class CachingCohortDefinitionTest extends BaseModuleContextSensitiveTest {
	
	@Before
	public void setup() throws Exception {
		executeDataSet("org/openmrs/module/reporting/include/ReportTestDataset.xml");
	}
	
	@Test
	public void shouldCacheCohortDefinition() throws Exception {
		
		EvaluationContext ec = new EvaluationContext();
		
		GenderCohortDefinition males = new GenderCohortDefinition();
		males.setMaleIncluded(true);
		
		GenderCohortDefinition females = new GenderCohortDefinition();
		females.setFemaleIncluded(true);
		
		ConfigurationPropertyCachingStrategy strategy = new ConfigurationPropertyCachingStrategy();
		String maleKey = strategy.getCacheKey(males);
		String femaleKey = strategy.getCacheKey(females);
		assertNull("Cache should not have male filter yet", ec.getFromCache(maleKey));

		Cohort maleCohort = Context.getService(CohortDefinitionService.class).evaluate(males, ec);		
		assertNotNull("Cache should have male filter now", ec.getFromCache(maleKey));
		assertNull("Cache should not have female filter", ec.getFromCache(femaleKey));

		Cohort malesAgain = Context.getService(CohortDefinitionService.class).evaluate(males, ec);
		assertEquals("Uncached and cached runs should be equals", maleCohort.size(), malesAgain.size());
		
		ec.setBaseCohort(maleCohort);
		assertEquals("Cache should have been automatically cleared", 0, ec.getCache().size());
	}
	
}