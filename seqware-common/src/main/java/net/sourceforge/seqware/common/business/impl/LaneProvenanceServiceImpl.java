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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import net.sourceforge.seqware.common.business.LaneProvenanceService;
import net.sourceforge.seqware.common.dao.LaneDAO;
import net.sourceforge.seqware.common.dto.LaneProvenanceDto;
import net.sourceforge.seqware.common.dto.builders.LaneProvenanceDtoFromObjects;
import net.sourceforge.seqware.common.model.Lane;
import org.springframework.beans.BeanUtils;

/**
 *
 * @author mlaszloffy
 */
public class LaneProvenanceServiceImpl implements LaneProvenanceService {

    private LaneDAO laneDAO;

    @Override
    public void setLaneDAO(LaneDAO laneDAO) {
        this.laneDAO = laneDAO;
    }

    @Override
    public List<LaneProvenanceDto> list() {
        return buildList(laneDAO.list());
    }

    public static List<LaneProvenanceDto> buildList(Collection<Lane> lanes) {
        List<LaneProvenanceDto> dtos = new ArrayList<>();
        for (Lane lane : lanes) {
            if (lane == null || lane.getSequencerRun() == null) {
                continue;
            }
            LaneProvenanceDtoFromObjects lp = new LaneProvenanceDtoFromObjects();
            lp.setLane(lane);
            lp.setSequencerRun(lane.getSequencerRun());

            LaneProvenanceDto dto = new LaneProvenanceDto();
            BeanUtils.copyProperties(lp, dto);

            dtos.add(dto);
        }

        return dtos;
    }

}
