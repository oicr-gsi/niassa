package net.sourceforge.seqware.queryengine.webservice.controller;

import io.prometheus.client.Histogram;

public class LatencyHistogram {

	private final Histogram histogram;

	public LatencyHistogram(String name, String help, String... labels) {
		histogram = Histogram.build().buckets(1, 5, 10, 30, 60, 300, 600, 3600).name(name).help(help).labelNames(labels)
				.register();
	}

	public void observe(long startTime, final String... labels) {
		histogram.labels(labels).observe((System.nanoTime() - startTime) / 1e9);
	}

}
