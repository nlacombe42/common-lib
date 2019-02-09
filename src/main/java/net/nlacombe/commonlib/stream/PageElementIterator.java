package net.nlacombe.commonlib.stream;

import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

public class PageElementIterator<ElementType> implements Iterator<ElementType> {

    private Iterator<List<ElementType>> pageIterator;
    private List<ElementType> currentPage;
    private int currentPageIndex;

    public PageElementIterator(PageIterator<ElementType> pageIterator) {
        this.pageIterator = pageIterator;

        currentPage = null;
        currentPageIndex = 0;
    }

    @Override
    public boolean hasNext() {
        if (isCurrentElementInCurrentPage())
            return true;
        else
            return pageIterator.hasNext();
    }

    private boolean isCurrentElementInCurrentPage() {
        return currentPage != null && currentPageIndex < currentPage.size();
    }

    @Override
    public ElementType next() {
        if (!hasNext())
            throw new NoSuchElementException();

        if (!isCurrentElementInCurrentPage()) {
            currentPageIndex = 0;
            currentPage = pageIterator.next();
        }

        return currentPage.get(currentPageIndex++);
    }
}
