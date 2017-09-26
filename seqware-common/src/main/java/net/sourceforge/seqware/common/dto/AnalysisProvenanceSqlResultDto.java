/*
 * Copyright (C) 2016 SeqWare
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.sourceforge.seqware.common.dto;

import java.math.BigInteger;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 *
 * @author mlaszloffy
 */
public class AnalysisProvenanceSqlResultDto extends AnalysisProvenanceDto {

	public void setWorkflowAttributes(String workflowAttributes) {
		this.workflowAttributes = convertAttributesString(workflowAttributes);
	}

	public void setWorkflowRunAttributes(String workflowRunAttributes) {
		this.workflowRunAttributes = convertAttributesString(workflowRunAttributes);
	}

	public void setWorkflowRunInputFileIds(String workflowRunInputFileIds) {
		this.workflowRunInputFileIds = convertIntegerString(workflowRunInputFileIds);
	}

	public void setProcessingAttributes(String processingAttributes) {
		this.processingAttributes = convertAttributesString(processingAttributes);
	}

	public void setFileSize(BigInteger fileSize) {
		if (fileSize != null) {
			this.fileSize = fileSize.toString();
		}
	}

	public void setFileAttributes(String fileAttributes) {
		this.fileAttributes = convertAttributesString(fileAttributes);
	}

	public void setLastModified(Date lastModified) {
		this.lastModified = ZonedDateTime.ofInstant(lastModified.toInstant(), ZoneId.of("Z"));
	}

	public void setIusLimsKeys(String iusLimsKeys) {
		this.iusLimsKeys = convertIusLimsKeyString(iusLimsKeys);
	}

	public void setIusAttributes(String iusAttributes) {
		this.iusAttributes = convertAttributesString(iusAttributes);
	}

	private static SortedMap<String, SortedSet<String>> convertAttributesString(String attributesString) {
		if (attributesString == null || attributesString.isEmpty()) {
			return Collections.emptySortedMap();
		} else {
			SortedMap<String, SortedSet<String>> attrs = new TreeMap<>();
			for (String keyValues : attributesString.split(";")) {
				String[] tmp = keyValues.split("=");
				String key = tmp[0];
				SortedSet<String> values = new TreeSet<>(Arrays.asList(tmp[1].split("&")));
				attrs.put(key, values);
			}
			return attrs;
		}
	}

	private static SortedSet<Integer> convertIntegerString(String integersString) {
		if (integersString == null || integersString.isEmpty()) {
			return Collections.emptySortedSet();
		} else {
			SortedSet<Integer> integers = new TreeSet<>();
			for (String intString : integersString.split(",")) {
				integers.add(Integer.parseInt(intString));
			}
			return integers;
		}
	}

	public static final DateTimeFormatter FMT = new DateTimeFormatterBuilder()//
			.appendPattern("yyyy-MM-dd")//
			.appendOptional(new DateTimeFormatterBuilder()//
					.appendLiteral(' ')//
					.appendPattern("HH:mm:ss").appendOptional(new DateTimeFormatterBuilder()//
							.appendPattern(".SSS")//
							.toFormatter())
					.appendPattern("X")//
					.toFormatter())//
			.toFormatter();

	private static Set<IusLimsKeyDto> convertIusLimsKeyString(String iusLimsKeyString) {
		if (iusLimsKeyString == null || iusLimsKeyString.isEmpty()) {
			return Collections.emptySet();
		} else {

			Set<IusLimsKeyDto> iusLimsKeys = new HashSet<>();
			for (String record : iusLimsKeyString.split(";")) {
				String[] vals = record.split(",");

				IusLimsKeyDto dto = new IusLimsKeyDto();
				dto.setIusSWID(Integer.parseInt(vals[0]));

				LimsKeyDto lk = new LimsKeyDto();
				lk.setProvider(vals[1]);
				lk.setId(vals[2]);
				lk.setVersion(vals[3]);
				lk.setLastModified(ZonedDateTime.parse(vals[4], FMT));
				dto.setLimsKey(lk);

				iusLimsKeys.add(dto);
			}
			return iusLimsKeys;
		}
	}

}
