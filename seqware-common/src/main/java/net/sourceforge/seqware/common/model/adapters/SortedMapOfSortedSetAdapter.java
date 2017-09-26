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

import net.sourceforge.seqware.common.model.types.MapOfSetEntryType;
import net.sourceforge.seqware.common.model.types.MapOfSetType;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 * JAXB adapter for sorted map of sorted sets.
 *
 * @author mlaszloffy
 */
public class SortedMapOfSortedSetAdapter extends XmlAdapter<MapOfSetType, SortedMap<String, SortedSet<String>>> {

    @Override
    public SortedMap<String, SortedSet<String>> unmarshal(MapOfSetType v) throws Exception {
        SortedMap<String, SortedSet<String>> map = new TreeMap<>();

        for (MapOfSetEntryType mapEntryType : v.getEntry()) {
            String key = mapEntryType.getKey();
            SortedSet values = new TreeSet<>();
            for (String valueType : mapEntryType.getValues()) {
                values.add(valueType);
            }
            map.put(key, values);
        }
        return map;
    }

    @Override
    public MapOfSetType marshal(SortedMap<String, SortedSet<String>> v) throws Exception {
        MapOfSetType mapType = new MapOfSetType();

        for (Entry<String, SortedSet<String>> entry : v.entrySet()) {
            MapOfSetEntryType mapEntryType = new MapOfSetEntryType();
            mapEntryType.setKey(entry.getKey());

            List<String> values = new ArrayList<>();
            for (String s : entry.getValue()) {
                values.add(s);
            }

            mapEntryType.setValues(values);

            mapType.getEntry().add(mapEntryType);
        }
        return mapType;
    }
}
