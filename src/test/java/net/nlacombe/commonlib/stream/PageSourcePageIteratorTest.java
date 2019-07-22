package net.nlacombe.commonlib.stream;

import org.junit.Test;
import org.mockito.Mockito;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

public class PageSourcePageIteratorTest {

    @Test
    public void hasNext_returns_false_when_first_page_is_empty() {
        var pageSource = getIntegerPageSourceMock();
        when(pageSource.getNextPage()).thenReturn(List.of());

        boolean hasNext = new PageSourcePageIterator<>(pageSource).hasNext();

        assertThat(hasNext).isFalse();
    }

    @Test
    public void returns_good_values_for_hasNext_and_next_when_one_page() {
        var pageSource = getIntegerPageSourceMock();
        List<Integer> firstPage = List.of(1, 2, 3);
        when(pageSource.getNextPage()).thenReturn(firstPage, List.of());

        var pageSourcePageIterator = new PageSourcePageIterator<>(pageSource);

        assertThat(pageSourcePageIterator.hasNext()).isTrue();
        assertThat(pageSourcePageIterator.next()).isEqualTo(firstPage);
        assertThat(pageSourcePageIterator.hasNext()).isFalse();
    }

    private PageSource<Integer> getIntegerPageSourceMock() {
        @SuppressWarnings("unchecked")
        var pageSource = (PageSource<Integer>) Mockito.mock(PageSource.class);

        return pageSource;
    }

}
