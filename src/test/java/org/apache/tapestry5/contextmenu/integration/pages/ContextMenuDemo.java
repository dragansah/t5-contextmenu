// Copyright 2011 The Apache Software Foundation
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package org.apache.tapestry5.contextmenu.integration.pages;

import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.contextmenu.integration.data.Track;
import org.apache.tapestry5.contextmenu.integration.services.MusicLibrary;
import org.apache.tapestry5.ioc.annotations.Inject;

import java.util.List;

public class ContextMenuDemo
{
    @SuppressWarnings("unused")
    @Property
    private int loopValue;

    public int[] getLoopSource()
    {
        return new int[]
        { 1, 2, 3, 4 };
    }

    @SuppressWarnings("unused")
    @Property
    private int loopContext;

    void onContextMenuFromLoop(int loopValue)
    {
        loopContext = loopValue;
    }

    @Inject
    private MusicLibrary library;

    public List<Track> getTracks()
    {
        return library.getTracks();
    }

    @SuppressWarnings("unused")
    @Property
    private Track track;

    @SuppressWarnings("unused")
    @Property
    private Object propertyValue;

    @SuppressWarnings("unused")
    @Property
    private String propertyName;

    void onContextmenuFromGrid(Track track, String propertyName, Object propertyValue)
    {
        this.track = track;
        this.propertyName = propertyName;
        this.propertyValue = propertyValue;
    }

    void onContextmenuFromGridAjax(Track track, String propertyName, Object propertyValue)
    {
        this.track = track;
        this.propertyName = propertyName;
        this.propertyValue = propertyValue;
    }

}
