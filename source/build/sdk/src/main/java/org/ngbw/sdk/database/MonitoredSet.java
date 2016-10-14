/*
 * MonitoredSet.java
 */
package org.ngbw.sdk.database;


import java.util.Collection;
import java.util.Iterator;
import java.util.Set;


/**
 *
 * @author Paul Hoover
 *
 * @param <E>
 */
abstract class MonitoredSet<E> implements Set<E> {

	/**
	 *
	 */
	private class SetIterator implements Iterator<E> {

		private final Iterator<E> m_setIter;
		private E m_currentElement;


		// constructors


		protected SetIterator(Iterator<E> setIter)
		{
			m_setIter = setIter;
		}


		// public methods


		@Override
		public boolean hasNext()
		{
			return m_setIter.hasNext();
		}

		@Override
		public E next()
		{
			m_currentElement = m_setIter.next();

			return m_currentElement;
		}

		@Override
		public void remove()
		{
			addSetRemoveOp(m_currentElement);

			m_setIter.remove();
		}
	}


	private final Set<E> m_set;


	// constructors


	MonitoredSet(Set<E> set)
	{
		m_set = set;
	}


	// public methods


	@Override
	public boolean add(E o)
	{
		if (m_set.add(o)) {
			addSetAddOp(o);

			return true;
		}

		return false;
	}

	@Override
	public boolean addAll(Collection<? extends E> c)
	{
		boolean changed = false;

		for (Iterator<? extends E> elements = c.iterator() ; elements.hasNext() ; )
			changed = add(elements.next());

		return changed;
	}

	@Override
	public void clear()
	{
		m_set.clear();

		addSetClearOp();
	}

	@Override
	public boolean contains(Object o)
	{
		return m_set.contains(o);
	}

	@Override
	public boolean containsAll(Collection<?> c)
	{
		return m_set.containsAll(c);
	}

	@Override
	public boolean isEmpty()
	{
		return m_set.isEmpty();
	}

	@Override
	public Iterator<E> iterator()
	{
		return new SetIterator(m_set.iterator());
	}

	@Override
	@SuppressWarnings("unchecked")
	public boolean remove(Object o)
	{
		if (m_set.remove(o)) {
			addSetRemoveOp((E) o);

			return true;
		}

		return false;
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

		for (Iterator<E> elements = m_set.iterator() ; elements.hasNext() ; ) {
			E element = elements.next();

			if (!c.contains(element)) {
				remove(element);

				changed = true;
			}
		}

		return changed;
	}

	@Override
	public int size()
	{
		return m_set.size();
	}

	@Override
	public Object[] toArray()
	{
		return m_set.toArray();
	}

	@Override
	public <T> T[] toArray(T[] a)
	{
		return m_set.toArray(a);
	}

	@Override
	public boolean equals(Object other)
	{
		return m_set.equals(other);
	}

	@Override
	public int hashCode()
	{
		return m_set.hashCode();
	}


	// protected methods


	protected abstract void addSetAddOp(E element);

	protected abstract void addSetRemoveOp(E element);

	protected abstract void addSetClearOp();
}