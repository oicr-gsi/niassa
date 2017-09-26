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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 * JAXB adapter for map of sets.
 * 
 * @author mlaszloffy
 */
public class MapOfSetAdapter extends XmlAdapter<MapOfSetType, Map<String, Set<String>>> {

    @Override
    public Map<String, Set<String>> unmarshal(MapOfSetType v) throws Exception {
        HashMap<String, Set<String>> map = new HashMap<>();

        for (MapOfSetEntryType mapEntryType : v.getEntry()) {
            String key = mapEntryType.getKey();
            Set values = new HashSet<>();
            for(String valueType : mapEntryType.getValues()){
                values.add(valueType);
            }
            map.put(key, values);
        }
        return map;
    }

    @Override
    public MapOfSetType marshal(Map<String, Set<String>> v) throws Exception {
        MapOfSetType mapType = new MapOfSetType();

        for (Entry<String, Set<String>> entry : v.entrySet()) {
            MapOfSetEntryType mapEntryType = new MapOfSetEntryType();
            mapEntryType.setKey(entry.getKey());
            
            List<String> values = new ArrayList<>();
            for(String s : entry.getValue()) {
                values.add(s);
            }
            
            mapEntryType.setValues(values);
            
            mapType.getEntry().add(mapEntryType);
        }
        return mapType;
    }
}
