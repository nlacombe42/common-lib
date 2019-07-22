package net.nlacombe.commonlib.stream;

import org.junit.Test;
import org.mockito.Mockito;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

public class StreamUtilTest {

    @Test
    public void returns_empty_stream_when_no_element() {
        var emptyIterator = getEmptyPageIteratorMock();
        var stream = StreamUtil.createStream(emptyIterator);

        assertThat(stream.collect(Collectors.toList())).isEmpty();
    }

    @Test
    public void returns_stream_with_one_element_when_one_element() {
        var firstPage = List.of(1);
        var pageIterator = getPageIterator(firstPage);
        var stream = StreamUtil.createStream(pageIterator);

        var results = stream.collect(Collectors.toList());

        assertThat(results).containsExactlyElementsOf(firstPage);
    }

    @Test
    public void returns_stream_with_all_elements_of_first_page_when_one_page() {
        var firstPage = List.of(1, 2);
        var pageIterator = getPageIterator(firstPage);
        var stream = StreamUtil.createStream(pageIterator);

        var results = stream.collect(Collectors.toList());

        assertThat(results).containsExactlyElementsOf(firstPage);
    }

    @Test
    public void returns_stream_with_all_elements_of_multiple_pages_when_multiple_pages() {
        var firstPage = List.of(1, 2);
        var secondPage = List.of(3, 4);
        var pageIterator = getPageIterator(firstPage, secondPage);
        var stream = StreamUtil.createStream(pageIterator);

        var results = stream.collect(Collectors.toList());

        assertThat(results).containsExactlyElementsOf(join(firstPage, secondPage));
    }

    @Test
    public void returns_stream_with_all_elements_of_first_pages_when_page_source_with_one_page() {
        var firstPage = List.of(1, 2);
        var stream = StreamUtil.createStream(new PageSource<Integer>() {
            private boolean firstPageReturned = false;

            @Override
            public List<Integer> getNextPage() {
                if (firstPageReturned)
                    return List.of();

                firstPageReturned = true;

                return firstPage;
            }
        });

        var results = stream.collect(Collectors.toList());

        assertThat(results).isEqualTo(firstPage);
    }

    private <ElementType> List<ElementType> join(List<ElementType> firstList, List<ElementType> secondList) {
        return Stream.concat(firstList.stream(), secondList.stream()).collect(Collectors.toList());
    }

    private PageIterator<Integer> getEmptyPageIteratorMock() {
        var emptyIterator = getIntegerPageIteratorMock();

        when(emptyIterator.hasNext()).thenReturn(false);
        when(emptyIterator.next()).thenThrow(NoSuchElementException.class);

        return emptyIterator;
    }

    private PageIterator<Integer> getIntegerPageIteratorMock() {
        @SuppressWarnings("unchecked")
        var iterator = (PageIterator<Integer>) Mockito.mock(PageIterator.class);

        return iterator;
    }

    private <ElementType> PageIterator<ElementType> getPageIterator(List<ElementType>... pages) {
        return new PageIterator<>() {

            private int pageIndex = 0;

            @Override
            public boolean hasNext() {
                return pageIndex < pages.length;
            }

            @Override
            public List<ElementType> next() {
                return pages[pageIndex++];
            }
        };
    }
}
