/* Copyright  2011 The Apache Software Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

T5.extend(T5, {
	ContextMenu : {
		
		elementStateArray: [],
		
		currentContextMenu : null, // we can only show/hide one context menu. Is this ok?
	
		mouseOverMenu : false, // is the mouse over the menu
	
		preventBodyOnMouseDown : false, // prevent the body of handling the mouse down event
		
		ClientEvents : {
			    CONTEXT 		: 0,
			    MOUSEDOWN 		: 1,
			    MOUSEOVER 		: 2,
			    MOUSEMOVE 		: 3
		},
		
		HideTypes : {
		    	MOUSEDOWN 		: 0,
		    	MOUSEOUT 		: 1
		}
	}
});
	
T5.extendInitializers(function() {
	
	function initializer(spec) {

		var contextmenu = $(spec.contextMenuId);
		
		/*
		 * If the id of the menu element is not defined (is just a place-holder) 
		 * than take the contextMenu.previousSibling to the the menu element.
		 * This is a compromise documented in ContextMenuBase.java
		 */
		var element = null;
		if(spec.elementId == undefined)
			element = contextmenu.previous();
		else
			element = $(spec.elementId);

		var gridCellIndex = spec.gridCellIndex;
		if(gridCellIndex != undefined)
		{
			gridCellIndex = parseInt(gridCellIndex);
			element = contextmenu.previous(gridCellIndex); // locate the grid
			element = $(element).select('tbody td')[gridCellIndex]; // locate the td - cell
		}
		
		var gridRowIndex = spec.gridRowIndex;
		if(gridRowIndex != undefined)
		{
			gridRowIndex = parseInt(gridRowIndex);
			element = contextmenu.previous(gridRowIndex); // locate the grid
			element = $(element).select('tbody tr')[gridRowIndex]; // locate the tr - row
		}
		
		var clientEvent = parseInt(spec.clientEvent);
		var hideType = parseInt(spec.hideType);

		var zoneManagerSpec = spec.zoneManagerSpec;
		
		switch(clientEvent){
			case T5.ContextMenu.ClientEvents.CONTEXT: 
					element.oncontextmenu = showContextMenu;
					break;
			case T5.ContextMenu.ClientEvents.MOUSEDOWN: 
					element.onmousedown = function(event) {
						// Prevent body.onmousedown to hide the menu
						T5.ContextMenu.preventBodyOnMouseDown = true;
						showContextMenu(event, element);
					}
					break;
			case T5.ContextMenu.ClientEvents.MOUSEOVER: 
					element.onmouseover = showContextMenu;
					break;
			case T5.ContextMenu.ClientEvents.MOUSEMOVE: 
					element.onmousemove = showContextMenu;
					break;
		}

		switch(hideType){
			case T5.ContextMenu.HideTypes.MOUSEDOWN: 
					// Prevent body.onmousedown to hide the menu
					document.body.onmousedown = function(event) {
						if(T5.ContextMenu.preventBodyOnMouseDown == true)
							T5.ContextMenu.preventBodyOnMouseDown = false;
						else
							hideContextMenu(event);
					}
					break;
			case T5.ContextMenu.HideTypes.MOUSEOUT: 
					element.onmouseout = hideContextMenu;
					break;
		}
		
		// track when the mouse is over the menu, in order to disable hiding the
		// menu on click
		contextmenu.onmouseover = function() { T5.ContextMenu.mouseOverMenu = true; };
		contextmenu.onmouseout = function() { T5.ContextMenu.mouseOverMenu = false; };
		
		var index = T5.ContextMenu.elementStateArray.size();
		// add the index (of this contextMenu state) in the stateArray
		// in some new internalId property of the dom element
		element.internalId = index;
		var state = T5.ContextMenu.elementStateArray[index] = {};
		state['contextMenuId'] = contextmenu.id;
		state['ajax'] = (zoneManagerSpec != null);
		state['spec'] = zoneManagerSpec;
		state['isZoneUpdated'] = false;
	}
	
	 function showContextMenu(event, element) {

		if(element == undefined)
			element = this;
		
		var state = T5.ContextMenu.elementStateArray[element.internalId];

		if(state['ajax'] == true && state['isZoneUpdated'] == false)
		{
			state['isZoneUpdated'] = true;
			var spec = state['spec'];
			/* taken from Progressivedisplay.js */
			var mgr = new Tapestry.ZoneManager(spec);
			mgr.updateFromURL.bind(mgr).defer(spec.url);
		}
		
		// IE doesn't pass the event object
		if (event == null)
			event = window.event;

		// document.body.scrollTop does not work in IE
		var scrollTop = document.body.scrollTop ? document.body.scrollTop
				: document.documentElement.scrollTop;
		var scrollLeft = document.body.scrollLeft ? document.body.scrollLeft
				: document.documentElement.scrollLeft;

		var contextmenu = state['contextMenuId'];
		contextmenu = $(contextmenu);
		
		if (T5.ContextMenu.currentContextMenu != null)
			T5.ContextMenu.currentContextMenu.style.display = 'none';

		T5.ContextMenu.currentContextMenu = contextmenu;

		// hide the menu first to avoid an "up-then-over" visual effect
		contextmenu.style.display = 'none';
		var left = event.clientX + scrollLeft + 'px';
		var top = event.clientY + scrollTop + 'px';
		contextmenu.style.left = left
		contextmenu.style.top = top;
		contextmenu.style.display = 'block';

		return false;
	}

	 function hideContextMenu(event) {
		if (!T5.ContextMenu.mouseOverMenu) {
			if(T5.ContextMenu.currentContextMenu != null)
				T5.ContextMenu.currentContextMenu.style.display = 'none';
		}
	}

	return {
		contextMenu : initializer
	};
});
