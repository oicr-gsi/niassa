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
package net.sourceforge.seqware.common.business;

import ca.on.oicr.gsi.provenance.FileProvenanceFilter;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.sourceforge.seqware.common.dao.AnalysisProvenanceDAO;
import net.sourceforge.seqware.common.dao.IUSDAO;
import net.sourceforge.seqware.common.dto.AnalysisProvenanceDto;
import net.sourceforge.seqware.common.model.IUS;

/**
 *
 * @author mlaszloffy
 */
public interface AnalysisProvenanceService {

    public void setAnalysisProvenanceDAO(AnalysisProvenanceDAO analysisProvenanceDAO);

    public void setIUSDAO(IUSDAO iusDAO);

    public List<AnalysisProvenanceDto> list();

    public List<AnalysisProvenanceDto> list(Map<FileProvenanceFilter, Set<String>> filters);

    public List<AnalysisProvenanceDto> findForIus(IUS ius);

    public Set<FileProvenanceFilter> getSupportedFilters();

}
