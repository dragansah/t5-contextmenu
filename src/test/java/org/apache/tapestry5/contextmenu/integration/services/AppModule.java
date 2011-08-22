// Copyright 2006, 2007, 2008, 2009, 2010, 2011 The Apache Software Foundation
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package org.apache.tapestry5.contextmenu.integration.services;

import org.apache.tapestry5.SymbolConstants;
import org.apache.tapestry5.ValueEncoder;
import org.apache.tapestry5.contextmenu.integration.data.Track;
import org.apache.tapestry5.contextmenu.services.ContextMenuModule;
import org.apache.tapestry5.internal.services.GenericValueEncoderFactory;
import org.apache.tapestry5.ioc.MappedConfiguration;
import org.apache.tapestry5.ioc.annotations.SubModule;
import org.apache.tapestry5.ioc.internal.util.CollectionFactory;
import org.apache.tapestry5.services.ValueEncoderFactory;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.net.URL;

@SubModule(ContextMenuModule.class)
public class AppModule
{
    public static void contributeApplicationDefaults(
            MappedConfiguration<String, String> configuration)
    {
        configuration.add(SymbolConstants.PRODUCTION_MODE, "false");
    }

    public MusicLibrary buildMusicLibrary(Logger log) throws IOException, SAXException
    {
        URL library = getClass().getResource("iTunes.xml");
        final List<Track> tracks = new MusicLibraryParser(log).parseTracks(library);

        final Map<Long, Track> idToTrack = CollectionFactory.newMap();

        for (Track t : tracks)
        {
            idToTrack.put(t.getId(), t);
        }

        return new MusicLibrary()
        {
            public Track getById(long id)
            {
                Track result = idToTrack.get(id);

                if (result != null) return result;

                throw new IllegalArgumentException(String.format("No track with id #%d.", id));
            }

            public List<Track> getTracks()
            {
                return tracks;
            }

            public List<Track> findByMatchingTitle(String title)
            {
                String titleLower = title.toLowerCase();

                List<Track> result = CollectionFactory.newList();

                for (Track t : tracks)
                {
                    if (t.getTitle().toLowerCase().contains(titleLower)) result.add(t);
                }

                return result;
            }
        };
    }

    @SuppressWarnings("rawtypes")
    public static void contributeValueEncoderSource(
            MappedConfiguration<Class, ValueEncoderFactory> configuration,
            final MusicLibrary library)
    {
        ValueEncoder<Track> trackEncoder = new ValueEncoder<Track>()
        {
            public String toClient(Track value)
            {
                return Long.toString(value.getId());
            }

            public Track toValue(String clientValue)
            {
                long id = Long.parseLong(clientValue);

                return library.getById(id);
            }
        };

        configuration.add(Track.class, GenericValueEncoderFactory.create(trackEncoder));
    }
}
