/* SelectEvent.java

	Purpose:
		
	Description:
		
	History:
		Thu Jun 16 18:05:51     2005, Created by tomyeh

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 2.1 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui.event;

import static org.zkoss.lang.Generics.cast;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.zkoss.zk.au.AuRequest;
import org.zkoss.zk.au.AuRequests;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Desktop;

/**
 * Represents an event cause by user's the list selection is changed
 * at the client.
 * 
 * @author tomyeh
 */
@SuppressWarnings("serial")
public class SelectEvent<T extends Component, E> extends Event {
	private final Set<T> _selectedItems;
	private final Set<T> _prevSelectedItems;
	private final Set<T> _unselectedItems;
	private final Set<E> _prevSelectedObjects;
	private final Set<E> _selectedObjects;
	private final Set<E> _unselectedObjects;
	private final T _ref;
	private final int _keys;

	/** Indicates whether the Alt key is pressed.
	 * It might be returned as part of {@link #getKeys}.
	 */
	public static final int ALT_KEY = MouseEvent.ALT_KEY;
	/** Indicates whether the Ctrl key is pressed.
	 * It might be returned as part of {@link #getKeys}.
	 */
	public static final int CTRL_KEY = MouseEvent.CTRL_KEY;
	/** Indicates whether the Shift key is pressed.
	 * It might be returned as part of {@link #getKeys}.
	 */
	public static final int SHIFT_KEY = MouseEvent.SHIFT_KEY;

	/** Converts an AU request to a select event.
	 * @since 5.0.0
	 */
	public static final <T extends Component, E> SelectEvent<T, E> getSelectEvent(AuRequest request) {
		return getSelectEvent(request, null);
	}

	/** Converts an AU request to a select event.
	 * @since 6.0.0
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static final <T extends Component, E> SelectEvent<T, E> getSelectEvent(AuRequest request,
			SelectedObjectHandler<T> handler) {
		final Map<String, Object> data = request.getData();
		final Desktop desktop = request.getDesktop();
		final List<String> sitems = cast((List) data.get("items"));
		final Set<T> items = AuRequests.convertToItems(desktop, sitems);
		final Set<T> prevSelectedItems = (Set<T>) (handler == null ? null : handler.getPreviousSelectedItems());
		final Set<T> unselectedItems = (Set<T>) (handler == null ? null : handler.getUnselectedItems());
		final Set<E> prevSelectedObjects = (Set<E>) (handler == null ? null : handler.getPreviousSelectedObjects());
		final Set<E> unselectedObjects = (Set<E>) (handler == null ? null : handler.getUnselectedObjects());
		final Set<E> objs = (Set<E>) (handler == null ? null : handler.getObjects(items));
		return new SelectEvent<T, E>(request.getCommand(), request.getComponent(), items, prevSelectedItems,
				unselectedItems, objs, prevSelectedObjects, unselectedObjects,
				(T) desktop.getComponentByUuidIfAny((String) data.get("reference")), null, AuRequests.parseKeys(data));
	}

	/**
	 * A handle to retrieve selected objects from selected items (components)
	 * if possible.
	 */
	public interface SelectedObjectHandler<T extends Component> {
		/**
		 * Return selected objects from selected items if possible.
		 */
		public Set<Object> getObjects(Set<T> items);

		/**
		 * Return the previous selected items from the target component.
		 * @since 7.0.0
		 */
		public Set<T> getPreviousSelectedItems();

		/**
		 * Return the unselected items from the target component.
		 * @since 7.0.1
		 */
		public Set<T> getUnselectedItems();

		/**
		 * Return the previous selected objects from the target component.
		 * @since 7.0.1
		 */
		public Set<Object> getPreviousSelectedObjects();

		/**
		 * Return the unselected objects from the target component.
		 * @since 7.0.1
		 */
		public Set<Object> getUnselectedObjects();
	}

	/** Constructs a selection event.
	 * @param selectedItems a set of items that shall be selected.
	 */
	public SelectEvent(String name, Component target, Set<T> selectedItems) {
		this(name, target, selectedItems, null, 0);
	}

	/** Constructs a selection event.
	 * @param selectedItems a set of items that shall be selected.
	 */
	public SelectEvent(String name, Component target, Set<T> selectedItems, T ref) {
		this(name, target, selectedItems, ref, 0);
	}

	/** Constructs a selection event.
	 * @param selectedItems a set of items that shall be selected.
	 * @param keys a combination of {@link #CTRL_KEY}, {@link #SHIFT_KEY}
	 * and {@link #ALT_KEY}.
	 * @since 3.6.0
	 */
	public SelectEvent(String name, Component target, Set<T> selectedItems, T ref, int keys) {
		this(name, target, selectedItems, null, null, null, null, null, ref, null, keys);
	}

	/** Constructs a selection event containing the data objects that model
	 * provided.
	 * 
	 * @param selectedItems a set of items that shall be selected.
	 * @param selectedObjects a set of data objects that shall be selected.
	 * @param data an arbitrary data
	 * @param keys a combination of {@link #CTRL_KEY}, {@link #SHIFT_KEY}
	 * and {@link #ALT_KEY}.
	 * @since 6.0.0
	 */
	public SelectEvent(String name, Component target, Set<T> selectedItems, Set<T> previousSelectedItems,
			Set<T> unselectedItems, Set<E> selectedObjects, Set<E> prevSelectedObjects, Set<E> unselectedObjects, T ref,
			Object data, int keys) {
		super(name, target, data);

		if (selectedItems != null)
			_selectedItems = selectedItems;
		else
			_selectedItems = Collections.emptySet();

		if (previousSelectedItems != null)
			_prevSelectedItems = previousSelectedItems;
		else
			_prevSelectedItems = Collections.emptySet();

		if (unselectedItems != null)
			_unselectedItems = unselectedItems;
		else
			_unselectedItems = Collections.emptySet();

		if (selectedObjects != null)
			_selectedObjects = selectedObjects;
		else
			_selectedObjects = Collections.emptySet();

		if (prevSelectedObjects != null)
			_prevSelectedObjects = prevSelectedObjects;
		else
			_prevSelectedObjects = Collections.emptySet();

		if (unselectedObjects != null)
			_unselectedObjects = unselectedObjects;
		else
			_unselectedObjects = Collections.emptySet();

		_ref = ref;
		_keys = keys;
	}

	/** Returns the selected items (never null).
	 */
	public final Set<T> getSelectedItems() {
		return _selectedItems;
	}

	/** Returns the previous selected items (never null).
	 * @since 7.0.0
	 */
	public final Set<T> getPreviousSelectedItems() {
		return _prevSelectedItems;
	}

	/** Returns the previous selected objects. The information is available
	 * only when the target component has a model.
	 * @since 7.0.1
	 */
	public final Set<E> getPreviousSelectedObjects() {
		return _prevSelectedObjects;
	}

	/** Returns the unselected items.
	 * @since 7.0.1
	 */
	public final Set<T> getUnselectedItems() {
		return _unselectedItems;
	}

	/** Returns the unselected objects. The information is available
	 * only when the target component has a model.
	 * @since 7.0.1
	 */
	public final Set<E> getUnselectedObjects() {
		return _unselectedObjects;
	}

	/** Returns the selected objects (never null). The information is available
	 * only when the target component has a model. 
	 * @since 6.0.0
	 */
	public final Set<E> getSelectedObjects() {
		return _selectedObjects;
	}

	/** Returns the reference item that is the component causing the onSelect 
	 * event(select or deselect) to be fired.
	 *
	 * <p>It is null, if the onSelect event is not caused by listbox or tree or combobox.
	 * Note: if not multiple, the {@link #getReference} is the same with the first item of {@link #getSelectedItems}.
	 * @since 3.0.2
	 */
	public T getReference() {
		return _ref;
	}

	/** Returns what keys were pressed when the mouse is clicked, or 0 if
	 * none of them was pressed.
	 * It is a combination of {@link #CTRL_KEY}, {@link #SHIFT_KEY}
	 * and {@link #ALT_KEY}.
	 * @since 3.6.0
	 */
	public final int getKeys() {
		return _keys;
	}
}
