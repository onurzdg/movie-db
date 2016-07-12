package com.onur.moviedb.metric

import java.lang.management.ManagementFactory
import java.util.concurrent.TimeUnit

import com.codahale.metrics.health.HealthCheckRegistry
import com.codahale.metrics.{JmxReporter, MetricRegistry, Slf4jReporter}
import com.codahale.metrics.jvm.{BufferPoolMetricSet, GarbageCollectorMetricSet, MemoryUsageGaugeSet, ThreadStatesGaugeSet}
import org.slf4j.LoggerFactory


object Metrics {
  lazy val metricRegistry: MetricRegistry = {
    val metricRegistry: MetricRegistry = new MetricRegistry
    Slf4jReporter.forRegistry(metricRegistry)
      .outputTo(LoggerFactory.getLogger("metrics"))
      .convertRatesTo(TimeUnit.SECONDS)
      .convertDurationsTo(TimeUnit.MILLISECONDS)
      .build
      .start(30, TimeUnit.SECONDS)
    JmxReporter.forRegistry(metricRegistry).build.start()
    metricRegistry.register("jvm-gc", new GarbageCollectorMetricSet)
    metricRegistry.register("jvm-buffer", new BufferPoolMetricSet(ManagementFactory.getPlatformMBeanServer))
    metricRegistry.register("jvm-memory", new MemoryUsageGaugeSet)
    metricRegistry.register("jvm-threads", new ThreadStatesGaugeSet)
    metricRegistry
  }

  lazy val healthCheckRegistry = new HealthCheckRegistry
}
