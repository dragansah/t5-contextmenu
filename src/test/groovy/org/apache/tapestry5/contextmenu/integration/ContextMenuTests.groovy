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

package org.apache.tapestry5.contextmenu.integration;

import org.apache.tapestry5.test.SeleniumTestCase

import org.testng.annotations.Test

class ContextMenuTests extends SeleniumTestCase
{
    @Test
    void basic_test_on_any() {

        openLinks "Context Menu Demo"

        contextMenuPresent "element1", "Context Menu Element", "element1_0", "Context Menu"
    }

    @Test
    void contextmenu_displays_loop_context() {

        openLinks "Context Menu Demo"

        contextMenuPresent "loop",   "Loop Element 0", "loop_0", "Loop Context Menu 1"
        contextMenuPresent "loop_1", "Loop Element 1", "loop_2", "Loop Context Menu 2"
        contextMenuPresent "loop_3", "Loop Element 2", "loop_4", "Loop Context Menu 3"
        contextMenuPresent "loop_5", "Loop Element 3", "loop_6", "Loop Context Menu 4"

    }

    @Test
    void contextmenu_grid_tests() {

        openLinks "Context Menu Demo"

        // test first - non-ajax grid
        // first row title cell
        String gridCell = "//div[@class='t-data-grid'][1]//tr[@class='t-first']/td[@class='title']"
        String contextMenuText = contextMenuPresentAndReturnText(gridCell, "Bug Juice", "grid")
        assertTrue contextMenuText.contains("object:Bug Juice")
        assertTrue contextMenuText.contains("propertyName:title")
        assertTrue contextMenuText.contains("propertyValue:Bug Juice")

        // first row genre cell
        gridCell = "//div[@class='t-data-grid'][1]//tr[@class='t-first']/td[@class='genre']"
        contextMenuText = contextMenuPresentAndReturnText(gridCell, "Electronica", "grid_2")
        assertTrue contextMenuText.contains("object:Bug Juice")
        assertTrue contextMenuText.contains("propertyName:genre")
        assertTrue contextMenuText.contains("propertyValue:Electronica")

        // test second - ajax grid
        // first row title cell
        gridCell = "//div[@class='t-data-grid'][2]//tr[@class='t-first']/td[@class='title']"
        String contextMenuZone = "//div[@id='gridAjax']/div[@class='t-zone']"

        contextMenuPresentAndReturnText(gridCell, "Bug Juice", "gridAjax")
        waitForAjaxRequestsToComplete("20000")

        contextMenuText = getText(contextMenuZone)
        assertTrue contextMenuText.contains("object:Bug Juice")
        assertTrue contextMenuText.contains("propertyName:title")
        assertTrue contextMenuText.contains("propertyValue:Bug Juice")


        // first row genre cell
        gridCell = "//div[@class='t-data-grid'][2]//tr[@class='t-first']/td[@class='genre']"
        contextMenuZone = "//div[@id='gridAjax_2']/div[@class='t-zone']"

        contextMenuPresentAndReturnText(gridCell, "Electronica", "gridAjax_2")
        waitForAjaxRequestsToComplete("20000")

        contextMenuText = getText(contextMenuZone)
        assertTrue contextMenuText.contains("object:Bug Juice")
        assertTrue contextMenuText.contains("propertyName:genre")
        assertTrue contextMenuText.contains("propertyValue:Electronica")
    }

    private void contextMenuPresent(menuElement, elementContent, contextMenu, contextMenuContent) {

        assertText menuElement, elementContent
        assertFalse isVisible(contextMenu)

        mouseDown menuElement

        assertTrue isVisible(contextMenu)
        assertText contextMenu, contextMenuContent
    }

    private String contextMenuPresentAndReturnText(menuElement, elementContent, contextMenu) {

        assertText menuElement, elementContent
        assertFalse isVisible(contextMenu)

        mouseDown menuElement

        assertTrue isVisible(contextMenu)

        return getText(contextMenu)
    }
}
