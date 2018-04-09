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
package net.sourceforge.seqware.common.business.impl;

import com.google.common.collect.Iterables;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import net.sourceforge.seqware.common.business.SampleProvenanceService;
import net.sourceforge.seqware.common.dao.IUSDAO;
import net.sourceforge.seqware.common.dto.SampleProvenanceDto;
import net.sourceforge.seqware.common.dto.builders.SampleProvenanceDtoFromObjects;
import net.sourceforge.seqware.common.model.IUS;
import net.sourceforge.seqware.common.model.Sample;
import org.springframework.beans.BeanUtils;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author mlaszloffy
 */
@Transactional(rollbackFor=Exception.class)
public class SampleProvenanceServiceImpl implements SampleProvenanceService {

    private IUSDAO iusDAO;

    @Override
    public void setIUSDAO(IUSDAO iusDAO) {
        this.iusDAO = iusDAO;
    }

    @Override
    public List<SampleProvenanceDto> list() {
        return buildList(iusDAO.list());
    }

    public static List<SampleProvenanceDto> buildList(Collection<IUS> iuses) {
        List<SampleProvenanceDto> dtos = new ArrayList<>();
        for (IUS ius : iuses) {
            Sample sample = ius.getSample();
            if (sample == null) {
                continue;
            }
            SampleProvenanceDtoFromObjects sp = new SampleProvenanceDtoFromObjects();
            sp.setIus(ius);
            sp.setLane(ius.getLane());
            sp.setSequencerRun(ius.getLane().getSequencerRun());
            sp.setSample(sample);
            sp.setExperiment(sample.getExperiment());
            sp.setStudy(sample.getExperiment().getStudy());

            List<Sample> parentSamples = new ArrayList<>();
            Sample parentSample = getOnlyOrMostRecentlyUpdatedSample(sample.getParents());
            while (parentSample != null) {
                parentSamples.add(parentSample);
                parentSample = getOnlyOrMostRecentlyUpdatedSample(parentSample.getParents());
            }
            sp.setParentSamples(parentSamples);

            SampleProvenanceDto dto = new SampleProvenanceDto();
            BeanUtils.copyProperties(sp, dto, "createdDate", "lastModified");
            dto.setCreatedDate(sp.getCreatedDate());
            dto.setLastModified(sp.getLastModified());

            dtos.add(dto);
        }
        return dtos;
    }

    private static final Comparator<Sample> SAMPLE_UPDATE_TSTMP_COMPARATOR = new Comparator<Sample>() {
        @Override
        public int compare(Sample o1, Sample o2) {
            int dateComparisonInt = o1.getUpdateTimestamp().compareTo(o2.getUpdateTimestamp());
            if (dateComparisonInt == 0) {
                return o1.getSwAccession().compareTo(o2.getSwAccession());
            } else {
                return dateComparisonInt;
            }
        }
    };

    private static Sample getOnlyOrMostRecentlyUpdatedSample(Set<Sample> sampleSet) {
        if (sampleSet == null || sampleSet.isEmpty()) {
            return null;
        } else if (sampleSet.size() == 1) {
            return Iterables.getOnlyElement(sampleSet);
        } else {
            return Collections.max(sampleSet, SAMPLE_UPDATE_TSTMP_COMPARATOR);
        }
    }

}
