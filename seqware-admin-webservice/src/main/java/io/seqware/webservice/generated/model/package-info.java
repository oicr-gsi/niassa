/**
 * Auto-generated model classes.
 * 
 * @since 1.0
 */
@XmlJavaTypeAdapters({ @XmlJavaTypeAdapter(value = TimestampFormatterAdapter.class, type = Timestamp.class),
		@XmlJavaTypeAdapter(value = DateFormatterAdapter.class, type = Date.class),
		@XmlJavaTypeAdapter(value = ZonedDateTimeFormatterAdapter.class, type = java.time.ZonedDateTime.class) })
package io.seqware.webservice.generated.model;

import io.seqware.webservice.adapter.DateFormatterAdapter;
import io.seqware.webservice.adapter.TimestampFormatterAdapter;
import io.seqware.webservice.adapter.ZonedDateTimeFormatterAdapter;
import java.sql.Timestamp;
import java.util.Date;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapters;
