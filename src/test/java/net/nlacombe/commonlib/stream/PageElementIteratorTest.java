package net.nlacombe.commonlib.stream;

import org.junit.Test;
import org.mockito.Mockito;

import java.util.List;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

public class PageElementIteratorTest {

    @Test
    public void hasNext_returns_false_when_no_element() {
        var emptyIterator = getEmptyPageIteratorMock();
        var pageElementIterator = new PageElementIterator<>(emptyIterator);

        assertThat(pageElementIterator.hasNext()).isFalse();
    }

    @Test(expected = NoSuchElementException.class)
    public void next_throws_no_such_element_exception_when_no_element() {
        var emptyIterator = getEmptyPageIteratorMock();
        var pageElementIterator = new PageElementIterator<>(emptyIterator);

        pageElementIterator.next();
    }

    @Test
    public void hasNext_returns_true_when_one_element() {
        var pageIterator = getIntegerPageIteratorMock();
        var pageElementIterator = new PageElementIterator<>(pageIterator);

        when(pageIterator.hasNext()).thenReturn(true).thenReturn(false);
        when(pageIterator.next()).thenReturn(List.of(1)).thenThrow(NoSuchElementException.class);

        assertThat(pageElementIterator.hasNext()).isTrue();
    }

    @Test
    public void next_returns_first_element_when_one_element() {
        var pageIterator = getIntegerPageIteratorMock();
        var pageElementIterator = new PageElementIterator<>(pageIterator);
        var firstPage = List.of(1);

        when(pageIterator.hasNext()).thenReturn(true).thenReturn(false);
        when(pageIterator.next()).thenReturn(firstPage).thenThrow(NoSuchElementException.class);

        assertThat(pageElementIterator.next()).isEqualTo(firstPage.get(0));
    }

    @Test
    public void next_returns_all_elements_of_first_page_when_one_page_element() {
        var pageIterator = getIntegerPageIteratorMock();
        var pageElementIterator = new PageElementIterator<>(pageIterator);
        var firstPage = List.of(1, 2);

        when(pageIterator.hasNext()).thenReturn(true).thenReturn(false);
        when(pageIterator.next()).thenReturn(firstPage).thenThrow(NoSuchElementException.class);

        assertThat(pageElementIterator.next()).isEqualTo(firstPage.get(0));
        assertThat(pageElementIterator.next()).isEqualTo(firstPage.get(1));
    }

    @Test
    public void next_returns_all_elements_of_multiple_pages_when_multiple_page_element() {
        var pageIterator = getIntegerPageIteratorMock();
        var pageElementIterator = new PageElementIterator<>(pageIterator);
        var firstPage = List.of(1, 2);
        var secondPage = List.of(3, 4);

        when(pageIterator.hasNext()).thenReturn(true).thenReturn(true).thenReturn(false);
        when(pageIterator.next()).thenReturn(firstPage).thenReturn(secondPage).thenThrow(NoSuchElementException.class);

        assertThat(pageElementIterator.next()).isEqualTo(firstPage.get(0));
        assertThat(pageElementIterator.next()).isEqualTo(firstPage.get(1));
        assertThat(pageElementIterator.next()).isEqualTo(secondPage.get(0));
        assertThat(pageElementIterator.next()).isEqualTo(secondPage.get(1));
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
}
