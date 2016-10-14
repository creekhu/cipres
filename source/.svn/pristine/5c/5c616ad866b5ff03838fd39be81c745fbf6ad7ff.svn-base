/*
 * MonitoredList.java
 */
package org.ngbw.sdk.database;


import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;


/**
 *
 * @author Paul Hoover
 *
 * @param <E>
 */
abstract class MonitoredList<E> implements List<E> {

	/**
	 *
	 */
	private class MonitoredIterator implements Iterator<E> {

		private final Iterator<E> m_listIter;
		private E m_currentElement;


		// constructors


		protected MonitoredIterator(Iterator<E> listIter)
		{
			m_listIter = listIter;
		}


		// public methods


		@Override
		public boolean hasNext()
		{
			return m_listIter.hasNext();
		}

		@Override
		public E next()
		{
			m_currentElement = m_listIter.next();

			return m_currentElement;
		}

		@Override
		public void remove()
		{
			addListRemoveOp(m_currentElement);

			m_listIter.remove();
		}
	}

	/**
	 *
	 */
	private class MonitoredListIterator implements ListIterator<E> {

		private final ListIterator<E> m_listIter;
		private E m_currentElement;


		// constructors


		protected MonitoredListIterator(ListIterator<E> listIter)
		{
			m_listIter = listIter;
		}


		// public methods


		@Override
		public void add(E o)
		{
			m_listIter.add(o);

			addListAddOp(o);
		}

		@Override
		public boolean hasNext()
		{
			return m_listIter.hasNext();
		}

		@Override
		public boolean hasPrevious()
		{
			return m_listIter.hasPrevious();
		}

		@Override
		public E next()
		{
			m_currentElement = m_listIter.next();

			return m_currentElement;
		}

		@Override
		public int nextIndex()
		{
			return m_listIter.nextIndex();
		}

		@Override
		public E previous()
		{
			m_currentElement = m_listIter.previous();

			return m_currentElement;
		}

		@Override
		public int previousIndex()
		{
			return m_listIter.previousIndex();
		}

		@Override
		public void remove()
		{
			addListRemoveOp(m_currentElement);

			m_listIter.remove();
		}

		@Override
		public void set(E o)
		{
			addListSetOp(m_currentElement, o);

			m_listIter.set(o);

			m_currentElement = o;
		}
	}


	private final List<E> m_list;


	// constructors


	MonitoredList(List<E> list)
	{
		m_list = list;
	}


	// public methods


	@Override
	public boolean add(E o)
	{
		if (m_list.add(o)) {
			addListAddOp(o);

			return true;
		}

		return false;
	}

	@Override
	public void add(int index, E element)
	{
		m_list.add(index, element);

		addListAddOp(element);
	}

	@Override
	public boolean addAll(Collection<? extends E> c)
	{
		if (m_list.addAll(c)) {
			for (Iterator<? extends E> elements = c.iterator() ; elements.hasNext() ; )
				addListAddOp(elements.next());

			return true;
		}

		return false;
	}

	@Override
	public boolean addAll(int index, Collection<? extends E> c)
	{
		if (m_list.addAll(index, c)) {
			for (Iterator<? extends E> elements = c.iterator() ; elements.hasNext() ; )
				addListAddOp(elements.next());

			return true;
		}

		return false;
	}

	@Override
	public void clear()
	{
		for (Iterator<E> elements = m_list.iterator() ; elements.hasNext() ; )
			addListRemoveOp(elements.next());

		m_list.clear();
	}

	@Override
	public boolean contains(Object o)
	{
		return m_list.contains(o);
	}

	@Override
	public boolean containsAll(Collection<?> c)
	{
		return m_list.containsAll(c);
	}

	@Override
	public E get(int index)
	{
		return m_list.get(index);
	}

	@Override
	public int indexOf(Object o)
	{
		return m_list.indexOf(o);
	}

	@Override
	public boolean isEmpty()
	{
		return m_list.isEmpty();
	}

	@Override
	public Iterator<E> iterator()
	{
		return new MonitoredIterator(m_list.iterator());
	}

	@Override
	public int lastIndexOf(Object o)
	{
		return m_list.lastIndexOf(o);
	}

	@Override
	public ListIterator<E> listIterator()
	{
		return new MonitoredListIterator(m_list.listIterator());
	}

	@Override
	public ListIterator<E> listIterator(int index)
	{
		return new MonitoredListIterator(m_list.listIterator(index));
	}

	@Override
	@SuppressWarnings("unchecked")
	public boolean remove(Object o)
	{
		if (m_list.remove(o)) {
			addListRemoveOp((E) o);

			return true;
		}

		return false;
	}

	@Override
	public E remove(int index)
	{
		E removed = m_list.remove(index);

		addListRemoveOp(removed);

		return removed;
	}

	@Override
	public boolean removeAll(Collection<?> c)
	{
		boolean changed = false;

		for (Iterator<?> removed = c.iterator() ; removed.hasNext() ; )
			changed = remove(removed.next());

		return changed;
	}

	@Override
	public boolean retainAll(Collection<?> c)
	{
		boolean changed = false;

		for (Iterator<E> elements = m_list.iterator() ; elements.hasNext() ; ) {
			E element = elements.next();

			if (!c.contains(element)) {
				remove(element);

				changed = true;
			}
		}

		return changed;
	}

	@Override
	public E set(int index, E element)
	{
		E oldElement = m_list.set(index, element);

		addListSetOp(oldElement, element);

		return oldElement;
	}

	@Override
	public int size()
	{
		return m_list.size();
	}

	@Override
	public List<E> subList(int fromIndex, int toIndex)
	{
		return newListInstance(m_list.subList(fromIndex, toIndex));
	}

	@Override
	public Object[] toArray()
	{
		return m_list.toArray();
	}

	@Override
	public <T> T[] toArray(T[] a)
	{
		return m_list.toArray(a);
	}

	@Override
	public boolean equals(Object other)
	{
		return m_list.equals(other);
	}

	@Override
	public int hashCode()
	{
		return m_list.hashCode();
	}


	// protected methods


	protected abstract void addListAddOp(E element);

	protected abstract void addListSetOp(E oldElement, E newElement);

	protected abstract void addListRemoveOp(E element);

	protected abstract MonitoredList<E> newListInstance(List<E> list);
}
