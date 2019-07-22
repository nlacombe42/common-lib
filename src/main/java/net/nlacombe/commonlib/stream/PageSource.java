package net.nlacombe.commonlib.stream;

import java.util.List;

public interface PageSource<ElementType> {

    List<ElementType> getNextPage();

}
