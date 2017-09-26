/*
 * Copyright (C) 2015 SeqWare
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
package net.sourceforge.seqware.common.dao.hibernate;

import ca.on.oicr.gsi.provenance.FileProvenanceFilter;
import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Collections2;
import com.google.common.collect.Sets;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import net.sourceforge.seqware.common.dao.AnalysisProvenanceDAO;
import net.sourceforge.seqware.common.dto.AnalysisProvenanceDto;
import net.sourceforge.seqware.common.dto.AnalysisProvenanceSqlResultDto;
import org.apache.commons.io.IOUtils;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.transform.Transformers;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;

/**
 *
 * @author mlaszloffy
 */
public class AnalysisProvenanceDAOHibernate extends HibernateDaoSupport implements AnalysisProvenanceDAO {

    private final String analysisProvenanceAllSql;
    private final Set<FileProvenanceFilter> supportedFilters = Sets.immutableEnumSet(
            FileProvenanceFilter.workflow_run,
            FileProvenanceFilter.workflow,
            FileProvenanceFilter.file,
            FileProvenanceFilter.file_meta_type);

    private static final Function<String, Integer> STRING_TO_INT = new Function<String, Integer>() {
        @Override
        public Integer apply(String s) {
            return Integer.parseInt(s);
        }
    };

    public AnalysisProvenanceDAOHibernate() {
        try {
            analysisProvenanceAllSql = IOUtils.toString(this.getClass().getResourceAsStream("analysis_provenance_all.sql"), "UTF-8");
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
    }

    @Override
    public List<AnalysisProvenanceDto> list() {
    return list(Collections.emptyMap());
    }

    @Override
    public List<AnalysisProvenanceDto> list(Map<FileProvenanceFilter, Set<String>> filters) {
        Session session = getSessionFactory().getCurrentSession();

        StringBuilder sqlQueryBuilder = new StringBuilder();
        sqlQueryBuilder.append(analysisProvenanceAllSql);

    Map<String, Collection<?>> parameterList = new HashMap<>();
        List<String> sqlFilters = new ArrayList<>();
        if (filters == null || filters.isEmpty()) {

        } else {
            if (filters.containsKey(FileProvenanceFilter.workflow)) {
                sqlFilters.add("w.sw_accession in :workflowIds");
                parameterList.put("workflowIds", Collections2.transform(filters.get(FileProvenanceFilter.workflow), STRING_TO_INT));
            }

            if (filters.containsKey(FileProvenanceFilter.workflow_run)) {
                sqlFilters.add("wr.sw_accession in :workflowRunIds");
                parameterList.put("workflowRunIds", Collections2.transform(filters.get(FileProvenanceFilter.workflow_run), STRING_TO_INT));
            }

            if (filters.containsKey(FileProvenanceFilter.file)) {
                sqlFilters.add("pff.file_swid in :fileIds");
                parameterList.put("fileIds", Collections2.transform(filters.get(FileProvenanceFilter.file), STRING_TO_INT));
            }

            if (filters.containsKey(FileProvenanceFilter.file_meta_type)) {
                sqlFilters.add("pff.file_meta_type in :fileMetaTypes");
                parameterList.put("fileMetaTypes", filters.get(FileProvenanceFilter.file_meta_type));
            }
        }

        if (!sqlFilters.isEmpty()) {
            sqlQueryBuilder.append("\nWHERE ");
            sqlQueryBuilder.append(Joiner.on("\nAND ").join(sqlFilters));
        }

        SQLQuery query = session.createSQLQuery(sqlQueryBuilder.toString());

    for (Entry<String, Collection<?>> e : parameterList.entrySet()) {
            query.setParameterList(e.getKey(), e.getValue());
        }

    @SuppressWarnings("unchecked")
        List<AnalysisProvenanceDto> dtos = query.setResultTransformer(Transformers.aliasToBean(AnalysisProvenanceSqlResultDto.class)).list();
        return dtos;
    }

    @Override
    public Set<FileProvenanceFilter> getSupportedFilters() {
        return supportedFilters;
    }

}
