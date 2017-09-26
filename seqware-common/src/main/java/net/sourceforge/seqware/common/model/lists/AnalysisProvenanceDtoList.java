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
package net.sourceforge.seqware.common.model.lists;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import net.sourceforge.seqware.common.dto.AnalysisProvenanceDto;

/**
 *
 * @author mlaszloffy
 */
@XmlRootElement
public class AnalysisProvenanceDtoList {

    private List<AnalysisProvenanceDto> analysisProvenanceDtos = new ArrayList<>();

    @XmlElement(name = "dto")
    public List<AnalysisProvenanceDto> getAnalysisProvenanceDtos() {
        return analysisProvenanceDtos;
    }

    public void setAnalysisProvenanceDtos(List<AnalysisProvenanceDto> analysisProvenanceDtos) {
        this.analysisProvenanceDtos = analysisProvenanceDtos;
    }

}
