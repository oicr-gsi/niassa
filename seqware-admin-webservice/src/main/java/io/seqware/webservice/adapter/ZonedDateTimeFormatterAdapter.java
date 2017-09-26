package io.seqware.webservice.adapter;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class ZonedDateTimeFormatterAdapter extends XmlAdapter<String, ZonedDateTime> {
	@Override
	public ZonedDateTime unmarshal(String value) {
		return value == null ? null : DateTimeFormatter.ISO_ZONED_DATE_TIME.parse(value, ZonedDateTime::from);
	}

	@Override
	public String marshal(ZonedDateTime value) {
		return value != null ? DateTimeFormatter.ISO_ZONED_DATE_TIME.format(value) : null;
	}
}
