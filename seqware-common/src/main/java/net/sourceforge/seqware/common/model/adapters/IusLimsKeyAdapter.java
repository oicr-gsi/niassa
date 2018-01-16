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
package net.sourceforge.seqware.common.model.adapters;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import ca.on.oicr.gsi.provenance.model.IusLimsKey;
import ca.on.oicr.gsi.provenance.model.LimsKey;
import net.sourceforge.seqware.common.dto.IusLimsKeyDto;
import net.sourceforge.seqware.common.dto.LimsKeyDto;
import net.sourceforge.seqware.common.model.types.IusLimsKeyDtoType;
import net.sourceforge.seqware.common.model.types.LimsKeyType;

/**
 * Adapter to marshal/unmarshal from {@link net.sourceforge.seqware.common.model.L}
 *
 * @author mlaszloffy
 */
public class IusLimsKeyAdapter extends XmlAdapter<IusLimsKeyDtoType, IusLimsKey> {

    @Override
    public IusLimsKey unmarshal(IusLimsKeyDtoType v) throws Exception {
        IusLimsKeyDto ik = new IusLimsKeyDto();
        ik.setIusSWID(v.getIusSwid());

        LimsKeyType limsKeyType = v.getLimsKey();
        LimsKeyDto lk = new LimsKeyDto();
        lk.setProvider(limsKeyType.getProvider());
        lk.setId(limsKeyType.getId());
        lk.setVersion(limsKeyType.getVersion());
        lk.setLastModified(limsKeyType.getLastModified());
        ik.setLimsKey(lk);

        return ik;
    }

    @Override
    public IusLimsKeyDtoType marshal(IusLimsKey v) throws Exception {
        IusLimsKeyDtoType iusLimsKeyDtoType = new IusLimsKeyDtoType();
        iusLimsKeyDtoType.setIusSwid(v.getIusSWID());

        LimsKey limsKey = v.getLimsKey();
        LimsKeyType limsKeyType = new LimsKeyType();
        limsKeyType.setProvider(limsKey.getProvider());
        limsKeyType.setId(limsKey.getId());
        limsKeyType.setVersion(limsKey.getVersion());
        limsKeyType.setLastModified(limsKey.getLastModified());

        iusLimsKeyDtoType.setLimsKey(limsKeyType);

        return iusLimsKeyDtoType;
    }

}
