/*
 * Copyright 2002-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.security.config.annotation.web.configurers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import org.junit.After;
import org.junit.Test;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

import org.springframework.security.web.servlet.support.csrf.CsrfRequestDataValueProcessor;
import org.springframework.web.servlet.support.RequestDataValueProcessor;

/**
 * @author Rob Winch
 *
 */
public class CsrfConfigurerNoWebMvcTests {
	ConfigurableApplicationContext context;

	@After
	public void teardown() {
		if (context != null) {
			context.close();
		}
	}

	@Test
	public void missingDispatcherServletPreventsCsrfRequestDataValueProcessor() {
		loadContext(EnableWebConfig.class);

		assertThat(context.containsBeanDefinition("requestDataValueProcessor")).isTrue();
	}

	@Test
	public void findDispatcherServletPreventsCsrfRequestDataValueProcessor() {
		loadContext(EnableWebMvcConfig.class);

		assertThat(context.containsBeanDefinition("requestDataValueProcessor")).isTrue();
	}

	@Test
	public void overrideCsrfRequestDataValueProcessor() {
		loadContext(EnableWebOverrideRequestDataConfig.class);

		assertThat(context.getBean(RequestDataValueProcessor.class).getClass())
				.isNotEqualTo(CsrfRequestDataValueProcessor.class);
	}

	@EnableWebSecurity
	static class EnableWebConfig extends WebSecurityConfigurerAdapter {

		@Override
		protected void configure(HttpSecurity http) {
		}
	}

	@EnableWebSecurity
	static class EnableWebOverrideRequestDataConfig {
		@Bean
		@Primary
		public RequestDataValueProcessor requestDataValueProcessor() {
			return mock(RequestDataValueProcessor.class);
		}
	}

	@EnableWebSecurity
	static class EnableWebMvcConfig extends WebSecurityConfigurerAdapter {

		@Override
		protected void configure(HttpSecurity http) {
		}
	}

	private void loadContext(Class<?> configs) {
		AnnotationConfigApplicationContext annotationConfigApplicationContext = new AnnotationConfigApplicationContext();
		annotationConfigApplicationContext.register(configs);
		annotationConfigApplicationContext.refresh();
		this.context = annotationConfigApplicationContext;
	}
}
