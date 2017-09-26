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

import com.google.common.base.Joiner;
import java.util.SortedSet;
import java.util.TreeSet;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import org.apache.commons.collections.SetUtils;

/**
 *
 * @author mlaszloffy
 */
public class IntegerSortedSet extends XmlAdapter<String,SortedSet<Integer>>{

    @Override
    public SortedSet<Integer> unmarshal(String v) throws Exception {
        if("".equals(v)){
            return SetUtils.EMPTY_SORTED_SET;
        } else {
            String[] vals = v.split(",");
            SortedSet<Integer> result = new TreeSet<>();
            for(String s : vals){
                result.add(Integer.parseInt(s));
            }
            return result;
        }
    }

    @Override
    public String marshal(SortedSet<Integer> v) throws Exception {
        if (v == null) {
            return null;
        } else {
            return Joiner.on(",").skipNulls().join(v);
        }
    }

}
