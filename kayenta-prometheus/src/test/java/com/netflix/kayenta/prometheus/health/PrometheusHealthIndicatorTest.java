/*
 * Copyright 2020 Playtika.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
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

package com.netflix.kayenta.prometheus.health;

import static junit.framework.TestCase.assertTrue;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.lenient;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.Status;

@ExtendWith(MockitoExtension.class)
public class PrometheusHealthIndicatorTest {

  @Mock PrometheusHealthCache healthCache;
  @InjectMocks PrometheusHealthIndicator healthIndicator;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  public void downWhenHealthStatusesEmpty() {
    lenient().when(healthCache.getHealthStatuses()).thenReturn(Collections.emptyList());

    Health health = healthIndicator.health();

    assertThat(health)
        .isEqualTo(Health.down().withDetail("reason", "Health status is not yet ready.").build());
  }

  @Test
  public void upWhenHealthStatusesAreAllUp() {
    PrometheusHealthCache mockHealthCache = Mockito.mock(PrometheusHealthCache.class);
    List<PrometheusHealthJob.PrometheusHealthStatus> healthStatuses =
        Arrays.asList(
            new PrometheusHealthJob.PrometheusHealthStatus("Service1", Status.UP, null),
            new PrometheusHealthJob.PrometheusHealthStatus("Service2", Status.UP, null));
    Mockito.when(mockHealthCache.getHealthStatuses()).thenReturn(healthStatuses);

    PrometheusHealthIndicator healthIndicator = new PrometheusHealthIndicator(mockHealthCache);

    Health.Builder builder = new Health.Builder();
    healthIndicator.doHealthCheck(builder);

    Health health = builder.build();
    assertEquals("Health status should be UP", Status.UP, health.getStatus());
  }

  @Test
  public void downWhenAtLeastOneHealthStatusIsDown() {
    PrometheusHealthCache mockHealthCache = Mockito.mock(PrometheusHealthCache.class);
    List<PrometheusHealthJob.PrometheusHealthStatus> healthStatuses =
        Arrays.asList(
            new PrometheusHealthJob.PrometheusHealthStatus("Service1", Status.UP, null),
            new PrometheusHealthJob.PrometheusHealthStatus(
                "Service2", Status.DOWN, "Error details"));
    Mockito.when(mockHealthCache.getHealthStatuses()).thenReturn(healthStatuses);

    PrometheusHealthIndicator healthIndicator = new PrometheusHealthIndicator(mockHealthCache);

    Health.Builder builder = new Health.Builder();
    healthIndicator.doHealthCheck(builder);

    Health health = builder.build();
    assertEquals("Health status should be DOWN", Status.DOWN, health.getStatus());
    assertTrue(
        "Incorrect reason",
        health
            .getDetails()
            .get("reason")
            .toString()
            .contains("One of the Prometheus remote services is DOWN."));
  }
}
