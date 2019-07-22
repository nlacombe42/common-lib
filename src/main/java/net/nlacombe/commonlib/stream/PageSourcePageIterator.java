package net.nlacombe.commonlib.stream;

import java.util.List;

public class PageSourcePageIterator<ElementType> implements PageIterator<ElementType> {

    private PageSource<ElementType> pageSource;
    private List<ElementType> bufferPage;

    public PageSourcePageIterator(PageSource<ElementType> pageSource) {
        this.pageSource = pageSource;
    }

    @Override
    public boolean hasNext() {
        if (bufferPage == null)
            bufferPage = pageSource.getNextPage();

        return !bufferPage.isEmpty();
    }

    @Override
    public List<ElementType> next() {
        if (bufferPage == null)
            bufferPage = pageSource.getNextPage();

        var previousPage = bufferPage;

        bufferPage = pageSource.getNextPage();

        return previousPage;
    }
}
