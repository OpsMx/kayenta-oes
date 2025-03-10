/*
 * Copyright 2019 Playtika
 *
 * Licensed under the Apache License, Version 2.0 (the "License")
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.netflix.kayenta.tests;

import com.netflix.kayenta.Main;
import com.netflix.kayenta.configuration.MetricsReportingConfiguration;
import com.netflix.kayenta.prometheus.config.PrometheusConfiguration;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.autoconfigure.actuate.observability.AutoConfigureObservability;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@AutoConfigureObservability
@ImportAutoConfiguration(PrometheusConfiguration.class)
@ComponentScan({
  "com.netflix.kayenta.standalonecanaryanalysis",
  "com.netflix.kayenta.configuration"
})
@SpringBootTest(
    classes = {MetricsReportingConfiguration.class, Main.class},
    webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT,
    value = "spring.application.name=kayenta")
@ExtendWith(SpringExtension.class)
@ActiveProfiles({"base", "prometheus", "graphite", "cases"})
public abstract class BaseIntegrationTest {

  @Value("${management.server.port}")
  protected int managementPort;

  @Value("${server.port}")
  protected int serverPort;
}
