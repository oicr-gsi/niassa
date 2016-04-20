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
package net.sourceforge.seqware.common.business.impl;

import ca.on.oicr.gsi.provenance.model.IusLimsKey;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.sourceforge.seqware.common.business.AnalysisProvenanceService;
import net.sourceforge.seqware.common.dao.AnalysisProvenanceDAO;
import net.sourceforge.seqware.common.dao.IUSDAO;
import net.sourceforge.seqware.common.dto.AnalysisProvenanceDto;
import net.sourceforge.seqware.common.dto.builders.AnalysisProvenanceDtoBuilder;
import net.sourceforge.seqware.common.model.File;
import net.sourceforge.seqware.common.model.IUS;
import net.sourceforge.seqware.common.model.Processing;
import net.sourceforge.seqware.common.model.WorkflowRun;
import net.sourceforge.seqware.common.dto.IusLimsKeyDto;

/**
 *
 * @author mlaszloffy
 */
public class AnalysisProvenanceServiceImpl implements AnalysisProvenanceService {

    private AnalysisProvenanceDAO analysisProvenanceDAO;
    private IUSDAO iusDAO;

    @Override
    public void setAnalysisProvenanceDAO(AnalysisProvenanceDAO analysisProvenanceDAO) {
        this.analysisProvenanceDAO = analysisProvenanceDAO;
    }

    @Override
    public void setIUSDAO(IUSDAO iusDAO) {
        this.iusDAO = iusDAO;
    }

    @Override
    public List<AnalysisProvenanceDto> list() {
        return buildList(iusDAO.list());
    }

    @Override
    public List<AnalysisProvenanceDto> findForIus(IUS ius) {
        //TODO: this will not work for cases where AP has multiple IUS
        //return buildList(Arrays.asList(iusDAO.findByID(ius.getIusId())));
        Integer targetIusSwid = ius.getSwAccession();

        List<AnalysisProvenanceDto> aps = new ArrayList<>();
        for (AnalysisProvenanceDto ap : buildList(iusDAO.list())) {
            for (IusLimsKey ilk : ap.getIusLimsKeys()) {
                if (targetIusSwid.equals(ilk.getIusSWID())) {
                    aps.add(ap);
                    break;
                }
            }
        }

        return aps;
    }

    public static List<AnalysisProvenanceDto> buildList(Collection<IUS> iuses) {
        return AnalysisProvenanceListBuilder.calculate(iuses);
    }

    private static IusLimsKeyDto getIusLimsKey(IUS ius) {
        IusLimsKeyDto ik = new IusLimsKeyDto();
        ik.setIusSWID(ius.getSwAccession());
        ik.setLimsKey(ius.getLimsKey());
        return ik;
    }

    public static class AnalysisProvenanceListBuilder {

        private List<AnalysisProvenanceDto> aps = new ArrayList<>();
        private Map<Integer, AnalysisProvenanceDtoBuilder> buildersRelatedToWorkflowRun = new HashMap<>();
        private Map<Integer, AnalysisProvenanceDtoBuilder> buildersRelatedToFile = new HashMap<>();

        public AnalysisProvenanceListBuilder(Collection<IUS> iuses) {
            for (IUS ius : iuses) {

                //IUS is not linked to LimsKey - not a target for Analyis Provenance
                if (ius.getLimsKey() == null) {
                    continue;
                }

                Set<WorkflowRun> workflowRuns = ius.getWorkflowRuns();
                if (workflowRuns == null || workflowRuns.isEmpty()) {
                    generateOrUpdateAnalysisProvenanceFor(ius, null, null, null);
                } else {
                    for (WorkflowRun workflowRun : workflowRuns) {
                        Set<Processing> processings = new HashSet<>();
                        Set<Processing> primaryProcessings = workflowRun.getProcessings();
                        Set<Processing> offspringProcessings = workflowRun.getOffspringProcessings();
                        if (primaryProcessings != null) {
                            processings.addAll(primaryProcessings);
                        }
                        if (offspringProcessings != null) {
                            processings.addAll(offspringProcessings);
                        }
                        if (processings.isEmpty()) {
                            generateOrUpdateAnalysisProvenanceFor(ius, workflowRun, null, null);
                        } else {
                            for (Processing processing : processings) {
                                Set<File> files = processing.getFiles();
                                if (files == null || files.isEmpty()) {
                                    generateOrUpdateAnalysisProvenanceFor(ius, workflowRun, processing, null);
                                } else {
                                    for (File file : files) {
                                        generateOrUpdateAnalysisProvenanceFor(ius, workflowRun, processing, file);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        private void generateOrUpdateAnalysisProvenanceFor(IUS ius, WorkflowRun workflowRun, Processing processing, File file) {

            if (workflowRun == null) {
                AnalysisProvenanceDtoBuilder ap = new AnalysisProvenanceDtoBuilder();
                ap.addIusLimsKey(getIusLimsKey(ius));
                aps.add(ap.build());
                return;
            }

             //no processing records or the workflow run may not have any files yet
            if (processing == null || file == null) {

                //an explicitly set null workflow builder signals that a builder should not be created (there are file builders)
                if (buildersRelatedToWorkflowRun.containsKey(workflowRun.getSwAccession())
                        && buildersRelatedToWorkflowRun.get(workflowRun.getSwAccession()) == null) {
                    return;
                }

                //create a analysis provenance record with no processing or file information
                AnalysisProvenanceDtoBuilder ap = buildersRelatedToWorkflowRun.get(workflowRun.getSwAccession());
                if (ap == null) {
                    ap = new AnalysisProvenanceDtoBuilder();
                    ap.addIusLimsKey(getIusLimsKey(ius));
                    ap.setWorkflowRun(workflowRun);
                    ap.setWorkflow(workflowRun.getWorkflow());
                    buildersRelatedToWorkflowRun.put(workflowRun.getSwAccession(), ap);
                } else {
                    ap.addIusLimsKey(getIusLimsKey(ius));
                }
                return;
            }

            //the workflow run has files - clear any previous builders and signal that workflow run new builders should not be created
            buildersRelatedToWorkflowRun.put(workflowRun.getSwAccession(), null);

            //handle the case where processings are directly linked to the appropriate IUS
            Set<IUS> processingIus = processing.getIUS();
            if (processingIus != null && !processingIus.isEmpty()) {
                if (!processingIus.contains(ius)) {
                    //the target IUS is not relevant to the target processing
                    return;
                }
            }

            AnalysisProvenanceDtoBuilder ap = buildersRelatedToFile.get(file.getSwAccession());
            if (ap == null) {
                ap = new AnalysisProvenanceDtoBuilder();
                ap.addIusLimsKey(getIusLimsKey(ius));
                ap.setWorkflowRun(workflowRun);
                ap.setWorkflow(workflowRun.getWorkflow());
                ap.setProcessing(processing);
                ap.setFile(file);
                buildersRelatedToFile.put(file.getSwAccession(), ap);
            } else {
                ap.addIusLimsKey(getIusLimsKey(ius));
            }
        }

        public List<AnalysisProvenanceDto> build() {

            for (AnalysisProvenanceDtoBuilder ap : buildersRelatedToFile.values()) {
                if (ap != null) {
                    aps.add(ap.build());
                }
            }
            buildersRelatedToFile.clear();

            for (AnalysisProvenanceDtoBuilder ap : buildersRelatedToWorkflowRun.values()) {
                if (ap != null) {
                    aps.add(ap.build());
                }
            }
            buildersRelatedToWorkflowRun.clear();

            return aps;
        }

        public static List<AnalysisProvenanceDto> calculate(Collection<IUS> iuses) {
            return new AnalysisProvenanceListBuilder(iuses).build();
        }
    }
}
